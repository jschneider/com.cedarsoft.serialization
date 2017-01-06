package com.cedarsoft.serialization.generator.intellij.model;

import com.cedarsoft.serialization.generator.intellij.SerializerResolver;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiCapturedWildcardType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeVisitor;
import com.intellij.psi.PsiWildcardType;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.codeStyle.VariableKind;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.TypeConversionUtil;
import com.siyeh.ig.psiutils.TypeUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SerializerModelFactory {
  @Nonnull
  private final SerializerResolver serializerResolver;
  @Nonnull
  private final JavaCodeStyleManager codeStyleManager;
  @Nonnull
  private final JavaPsiFacade javaPsiFacade;

  public SerializerModelFactory(@Nonnull SerializerResolver serializerResolver, @Nonnull JavaCodeStyleManager codeStyleManager, @Nonnull JavaPsiFacade javaPsiFacade) {
    this.serializerResolver = serializerResolver;
    this.codeStyleManager = codeStyleManager;
    this.javaPsiFacade = javaPsiFacade;
  }

  @Nonnull
  public SerializerModel create( @Nonnull PsiClass classToSerialize, @Nonnull Iterable<? extends PsiField> selectedFields ) {
    FieldAccessProvider fieldAccessProvider = new FieldAccessProvider( classToSerialize );

    Collection<? extends FieldToSerialize> fieldToSerializeEntries = calculateFieldToSerializeEntries( selectedFields, fieldAccessProvider );
    Collection<? extends DelegatingSerializer> delegatingSerializers = calculateSerializerDelegates( fieldToSerializeEntries );

    return new SerializerModel( classToSerialize, fieldToSerializeEntries, delegatingSerializers );
  }

  @Nonnull
  private Collection<? extends FieldToSerialize> calculateFieldToSerializeEntries(@Nonnull Iterable<? extends PsiField> selectedFields, @Nonnull final FieldAccessProvider fieldAccessProvider) {
    List<FieldToSerialize> entries = new ArrayList<FieldToSerialize>();

    for ( final PsiField selectedField : selectedFields ) {
      @javax.annotation.Nullable FieldToSerialize entry = selectedField.getType().accept( new PsiTypeVisitor<FieldToSerialize>() {
        @Nullable
        @Override
        public FieldToSerialize visitClassType( PsiClassType classType ) {
          return createFieldToSerialize(classType, selectedField, fieldAccessProvider);
        }

        @Nullable
        @Override
        public FieldToSerialize visitPrimitiveType( PsiPrimitiveType primitiveType ) {
          return new FieldToSerialize(primitiveType, selectedField, fieldAccessProvider.getFieldAccess(selectedField), false);
        }

        @Nullable
        @Override
        public FieldToSerialize visitWildcardType( PsiWildcardType wildcardType ) {
          System.out.println( "--> FIX ME: " + wildcardType );
          return super.visitWildcardType( wildcardType );
        }

        @Nullable
        @Override
        public FieldToSerialize visitCapturedWildcardType( PsiCapturedWildcardType capturedWildcardType ) {
          System.out.println( "--> FIX ME: " + capturedWildcardType );
          return super.visitCapturedWildcardType( capturedWildcardType );
        }
      } );


      if ( entry == null ) {
        throw new IllegalStateException( "No entry created for <" + selectedField + ">" );
      }
      entries.add(entry);
    }

    return entries;
  }

  /**
   * Creates a field to serialize for the given field type.
   * Checks if it is a collection
   */
  @Nonnull
  private FieldToSerialize createFieldToSerialize(@Nonnull PsiClassType fieldType, @Nonnull PsiField selectedField, @Nonnull FieldAccessProvider fieldAccessProvider) {
    //Check if it is a collection type
    PsiClass collectionType = javaPsiFacade.findClass("java.util.Collection", GlobalSearchScope.allScope(selectedField.getProject()));
    if (collectionType == null) {
      throw new IllegalArgumentException("No collection type found");
    }

    if (TypeConversionUtil.isAssignable(TypeUtils.getType(collectionType), fieldType)) {
      PsiType[] parameters = fieldType.getParameters();

      if (parameters.length == 0) {
        throw new IllegalArgumentException("No parameter found!");
      }

      //The relevant type from the parameter
      PsiType relevantType = parameters[0];
      return new FieldToSerialize(relevantType, selectedField, fieldAccessProvider.getFieldAccess(selectedField), true);
    }

    return new FieldToSerialize(fieldType, selectedField, fieldAccessProvider.getFieldAccess(selectedField), false);
  }

  /**
   * Returns the raw type of a collection
   */
  private static boolean getCollectionRawType(@Nonnull PsiType fieldType) {
    Queue<PsiType> queue = new LinkedList<>();
    queue.add(fieldType);

    while (!queue.isEmpty()) {
      PsiType current = queue.poll();
      queue.addAll(Arrays.asList(current.getSuperTypes()));

      PsiClass psiClass = ((PsiClassType) current).resolve();
      assert psiClass != null;
      String qualifiedName = psiClass.getQualifiedName();

      if ("java.util.Collection".equals(qualifiedName)) {
        return true;
      }
    }

    return false;
  }

  @Nonnull
  private Collection<? extends DelegatingSerializer> calculateSerializerDelegates( @Nonnull Iterable<? extends FieldToSerialize> fieldEntries ) {
    Map<PsiType, DelegatingSerializer> delegatingSerializersMap = new LinkedHashMap<PsiType, DelegatingSerializer>();

    for ( FieldToSerialize fieldEntry : fieldEntries ) {
      PsiType delegatingSerializerType = serializerResolver.findSerializerFor( fieldEntry.getFieldType() );
      String paramName = codeStyleManager.suggestVariableName( VariableKind.PARAMETER, null, null, delegatingSerializerType ).names[0];

      DelegatingSerializer entry = new DelegatingSerializer( fieldEntry.getFieldType(), delegatingSerializerType, paramName );
      delegatingSerializersMap.put( entry.getSerializedType(), entry );
    }

    return delegatingSerializersMap.values();
  }

}
