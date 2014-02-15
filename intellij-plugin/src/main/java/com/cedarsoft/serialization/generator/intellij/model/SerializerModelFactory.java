package com.cedarsoft.serialization.generator.intellij.model;

import com.cedarsoft.serialization.generator.intellij.SerializerResolver;
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
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SerializerModelFactory {
  @Nonnull
  private final SerializerResolver serializerResolver;
  @Nonnull
  private final JavaCodeStyleManager codeStyleManager;

  public SerializerModelFactory( @Nonnull SerializerResolver serializerResolver, @Nonnull JavaCodeStyleManager codeStyleManager ) {
    this.serializerResolver = serializerResolver;
    this.codeStyleManager = codeStyleManager;
  }

  @Nonnull
  public SerializerModel create( @Nonnull PsiClass classToSerialize, @Nonnull Iterable<? extends PsiField> selectedFields ) {
    FieldAccessProvider fieldAccessProvider = new FieldAccessProvider( classToSerialize );

    Collection<? extends FieldToSerialize> fieldToSerializeEntries = calculateFieldToSerializeEntries( selectedFields, fieldAccessProvider );
    Collection<? extends DelegatingSerializer> delegatingSerializers = calculateSerializerDelegates( fieldToSerializeEntries );

    return new SerializerModel( classToSerialize, fieldToSerializeEntries, delegatingSerializers );
  }

  @Nonnull
  private static Collection<? extends FieldToSerialize> calculateFieldToSerializeEntries( @Nonnull Iterable<? extends PsiField> selectedFields, @Nonnull final FieldAccessProvider fieldAccessProvider ) {
    List<FieldToSerialize> entries = new ArrayList<FieldToSerialize>();

    for ( final PsiField selectedField : selectedFields ) {
      @javax.annotation.Nullable FieldToSerialize entry = selectedField.getType().accept( new PsiTypeVisitor<FieldToSerialize>() {
        @Nullable
        @Override
        public FieldToSerialize visitClassType( PsiClassType classType ) {
          return new FieldToSerialize( classType, selectedField, fieldAccessProvider.getFieldAccess( selectedField ) );
        }

        @Nullable
        @Override
        public FieldToSerialize visitPrimitiveType( PsiPrimitiveType primitiveType ) {
          return new FieldToSerialize( primitiveType, selectedField, fieldAccessProvider.getFieldAccess( selectedField ) );
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
      entries.add( entry );
    }

    return entries;
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
