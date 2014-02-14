package com.cedarsoft.serialization.generator.intellij.model;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PropertyUtil;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class FieldAccessProvider {
  @Nonnull
  private final PsiClass classToSerialize;
  @Nullable
  private final PsiMethod constructor;

  public FieldAccessProvider( @Nonnull PsiClass classToSerialize ) {
    this.classToSerialize = classToSerialize;
    constructor = findLongestConstructor( classToSerialize );
  }

  @Nullable
  public PsiMethod getConstructor() {
    return constructor;
  }

  @Nonnull
  public PsiClass getClassToSerialize() {
    return classToSerialize;
  }

  @Nonnull
  public FieldSetter getFieldAccess( @Nonnull PsiField field ) {
    @Nullable FieldSetter.ConstructorFieldSetter constructorFieldAccess = getConstructorAccess( field );
    if ( constructorFieldAccess != null ) {
      return constructorFieldAccess;
    }

    return findSetter( field );
  }

  @Nullable
  private FieldSetter.ConstructorFieldSetter getConstructorAccess( @Nonnull PsiField field ) {
    if ( constructor == null ) {
      return null;
    }
    for ( PsiParameter psiParameter : constructor.getParameterList().getParameters() ) {
      PsiType type = psiParameter.getType();
      String name = psiParameter.getName();

      if ( !field.getName().equals( name ) ) {
        continue;
      }

      if ( !field.getType().equals( type ) ) {
        continue;
      }

      return new FieldSetter.ConstructorFieldSetter( constructor.getParameterList().getParameterIndex( psiParameter ) );
    }

    return null;
  }

  @Nonnull
  private static FieldSetter findSetter( @Nonnull PsiField field ) {
    @Nullable PsiMethod setter = PropertyUtil.findSetterForField( field );
    if ( setter != null ) {
      return new FieldSetter.SetterFieldSetter( setter.getName() );
    }
    return new FieldSetter.SetterFieldSetter( PropertyUtil.suggestSetterName( field ) );
  }

  @Nonnull
  private Project getProject() {
    return classToSerialize.getProject();
  }

  @javax.annotation.Nullable
  private static PsiMethod findLongestConstructor( @Nonnull PsiClass classToSerialize ) {
    PsiMethod bestConstructor = null;

    for ( PsiMethod constructor : classToSerialize.getConstructors() ) {
      if ( bestConstructor == null ) {
        bestConstructor = constructor;
        continue;
      }

      if ( constructor.getParameterList().getParameters().length > bestConstructor.getParameterList().getParameters().length ) {
        bestConstructor = constructor;
      }
    }

    return bestConstructor;
  }
}
