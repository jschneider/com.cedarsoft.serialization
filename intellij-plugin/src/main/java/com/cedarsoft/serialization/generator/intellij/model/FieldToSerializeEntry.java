package com.cedarsoft.serialization.generator.intellij.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.codeStyle.VariableKind;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.TypeConversionUtil;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class FieldToSerializeEntry {
  @Nonnull
  private final PsiType fieldType;
  @Nonnull
  private final PsiField field;
  @Nonnull
  private final String fieldName;
  @Nonnull
  private final FieldSetter fieldSetter;
  @Nonnull
  private final String accessor;
  @Nonnull
  private final String propertyConstant;
  @Nonnull
  private final String defaultValue;


  public FieldToSerializeEntry( @Nonnull PsiType fieldType, @Nonnull PsiField field, @Nonnull FieldSetter fieldSetter ) {
    this.fieldType = fieldType;
    this.field = field;
    this.fieldName = field.getName();
    this.fieldSetter = fieldSetter;

    this.accessor = findGetter( field );
    this.propertyConstant = "PROPERTY_" + JavaCodeStyleManager.getInstance( getProject() ).suggestVariableName( VariableKind.STATIC_FINAL_FIELD, field.getName(), null, fieldType ).names[0];

    defaultValue = getDefaultValue( fieldType );
  }

  @Nonnull
  private String getDefaultValue( @Nonnull PsiType fieldType ) {
    if ( isPrimitive() ) {
      if ( TypeConversionUtil.isBooleanType( fieldType ) ) {
        return "false";
      }
      if ( PsiType.CHAR.equals( fieldType ) ) {
        return "(char)-1";
      }

      return "-1";
    } else {
      return "null";
    }
  }

  @Nonnull
  public PsiField getField() {
    return field;
  }

  @Nonnull
  public FieldSetter getFieldSetter() {
    return fieldSetter;
  }

  @Nonnull
  public PsiType getFieldType() {
    return fieldType;
  }

  @Nonnull
  public String getFieldName() {
    return fieldName;
  }

  @Nonnull
  public String getAccessor() {
    return accessor;
  }

  @Nonnull
  public String getPropertyConstantName() {
    return propertyConstant;
  }

  public final boolean isPrimitive() {
    return TypeConversionUtil.isPrimitiveAndNotNull( fieldType );
  }

  @Nonnull
  public String getDefaultValue() {
    return defaultValue;
  }

  public boolean shallVerifyDeserialized() {
    return !PsiType.BOOLEAN.equals( fieldType );
  }

  @Nonnull
  private Project getProject() {
    return field.getProject();
  }

  @Nonnull
  public String getFieldTypeBoxed() {
    return DelegatingSerializerEntry.box( getFieldType() );
  }

  @Nonnull
  static String findGetter( @Nonnull PsiField field ) {
    PsiMethod getter = PropertyUtil.findGetterForField( field );
    if ( getter != null ) {
      return getter.getName() + "()";

    }
    return "get" + StringUtil.capitalizeWithJavaBeanConvention( field.getName() ) + "()";
  }
}
