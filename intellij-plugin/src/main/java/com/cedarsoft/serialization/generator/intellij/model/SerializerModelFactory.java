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
import javax.inject.Inject;
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

  @Inject
  public SerializerModelFactory( @Nonnull SerializerResolver serializerResolver, @Nonnull JavaCodeStyleManager codeStyleManager ) {
    this.serializerResolver = serializerResolver;
    this.codeStyleManager = codeStyleManager;
  }

  @Nonnull
  public SerializerModel create( @Nonnull PsiClass classToSerialize, @Nonnull Iterable<? extends PsiField> selectedFields ) {
    FieldAccessProvider fieldAccessProvider = new FieldAccessProvider( classToSerialize );

    Collection<? extends FieldToSerializeEntry> fieldToSerializeEntries = calculateFieldToSerializeEntries( selectedFields, fieldAccessProvider );
    Collection<? extends DelegatingSerializerEntry> delegatingSerializers = calculateSerializerDelegates( fieldToSerializeEntries );

    return new SerializerModel( classToSerialize, fieldToSerializeEntries, delegatingSerializers );
  }

  @Nonnull
  private static Collection<? extends FieldToSerializeEntry> calculateFieldToSerializeEntries( @Nonnull Iterable<? extends PsiField> selectedFields, @Nonnull final FieldAccessProvider fieldAccessProvider ) {
    List<FieldToSerializeEntry> entries = new ArrayList<FieldToSerializeEntry>();

    for ( final PsiField selectedField : selectedFields ) {
      @javax.annotation.Nullable FieldToSerializeEntry entry = selectedField.getType().accept( new PsiTypeVisitor<FieldToSerializeEntry>() {
        @Nullable
        @Override
        public FieldToSerializeEntry visitClassType( PsiClassType classType ) {
          return new FieldToSerializeEntry( classType, selectedField, fieldAccessProvider.getFieldAccess( selectedField ) );
        }

        @Nullable
        @Override
        public FieldToSerializeEntry visitPrimitiveType( PsiPrimitiveType primitiveType ) {
          return new FieldToSerializeEntry( primitiveType, selectedField, fieldAccessProvider.getFieldAccess( selectedField ) );
        }

        @Nullable
        @Override
        public FieldToSerializeEntry visitWildcardType( PsiWildcardType wildcardType ) {
          System.out.println( "--> FIX ME: " + wildcardType );
          return super.visitWildcardType( wildcardType );
        }

        @Nullable
        @Override
        public FieldToSerializeEntry visitCapturedWildcardType( PsiCapturedWildcardType capturedWildcardType ) {
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
  private Collection<? extends DelegatingSerializerEntry> calculateSerializerDelegates( @Nonnull Iterable<? extends FieldToSerializeEntry> fieldEntries ) {
    Map<PsiType, DelegatingSerializerEntry> delegatingSerializersMap = new LinkedHashMap<PsiType, DelegatingSerializerEntry>();

    for ( FieldToSerializeEntry fieldEntry : fieldEntries ) {
      PsiType delegatingSerializerType = serializerResolver.findJacksonSerializerFor( fieldEntry.getFieldType() );
      String paramName = codeStyleManager.suggestVariableName( VariableKind.PARAMETER, null, null, delegatingSerializerType ).names[0];

      DelegatingSerializerEntry entry = new DelegatingSerializerEntry( fieldEntry.getFieldType(), delegatingSerializerType, paramName );
      delegatingSerializersMap.put( entry.getSerializedType(), entry );
    }

    return delegatingSerializersMap.values();
  }

}
