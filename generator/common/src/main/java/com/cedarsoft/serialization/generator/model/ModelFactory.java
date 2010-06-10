/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.serialization.generator.model;

import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.type.TypeMirror;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class ModelFactory {
  @NotNull
  private final ClassDeclaration classDeclaration;

  public ModelFactory( @NotNull ClassDeclaration classDeclaration ) {
    this.classDeclaration = classDeclaration;
  }

  @NotNull
  public ClassToSerialize create() {
    ClassToSerialize classToSerialize = new ClassToSerialize( classDeclaration.getQualifiedName() );

    for ( FieldDeclaration fieldDeclaration : classDeclaration.getFields() ) {
      FieldInitializedInConstructorInfo info = getFieldInitializeInConstructorInfo( fieldDeclaration );
      classToSerialize.addField( info );
    }

    return classToSerialize;
  }


  @NotNull
  public FieldDeclaration findFieldDeclaration( @NotNull @NonNls String fieldName ) {
    for ( FieldDeclaration fieldDeclaration : classDeclaration.getFields() ) {
      if ( fieldDeclaration.getSimpleName().equals( fieldName ) ) {
        return fieldDeclaration;
      }
    }

    throw new IllegalArgumentException( "No field delaration found for <" + fieldName + ">" );
  }

  @NotNull
  public ConstructorCallInfo findConstructorParamDeclarationForField( @NotNull FieldDeclaration fieldDeclaration ) {
    return findConstructorParamDeclaration( fieldDeclaration.getSimpleName(), fieldDeclaration.getType() );
  }

  public ConstructorCallInfo findConstructorParamDeclaration( @NotNull @NonNls String simpleName, @NotNull TypeMirror type ) {
    ConstructorDeclaration constructorDeclaration = findBestConstructor();

    int index = 0;

    for ( ParameterDeclaration parameterDeclaration : constructorDeclaration.getParameters() ) {
      if ( parameterDeclaration.getSimpleName().equals( simpleName ) ) {
        //Found a fitting type
        if ( parameterDeclaration.getType().equals( type ) ) {
          return new ConstructorCallInfo( constructorDeclaration, index, parameterDeclaration );
        } else {
          throw new IllegalArgumentException( "Type mismatch for <" + simpleName + ">. Was <" + parameterDeclaration.getType() + "> but expected <" + type + ">" );
        }
      }
      index++;
    }

    throw new IllegalArgumentException( "No parameter found that fits! " + simpleName );
  }

  @NotNull
  public ConstructorDeclaration findBestConstructor() {
    ConstructorDeclaration currentlyBest = null;
    for ( ConstructorDeclaration constructorDeclaration : classDeclaration.getConstructors() ) {
      if ( currentlyBest == null || constructorDeclaration.getParameters().size() > currentlyBest.getParameters().size() ) {
        currentlyBest = constructorDeclaration;
      }
    }

    if ( currentlyBest == null ) {
      throw new IllegalStateException( "No constructor found in " + classDeclaration.getSimpleName() );
    }
    return currentlyBest;
  }

  @NotNull
  public FieldInitializedInConstructorInfo findFieldInitializedInConstructor( @NotNull @NonNls String simpleName ) {
    FieldDeclaration fieldDeclaration = findFieldDeclaration( simpleName );
    return getFieldInitializeInConstructorInfo( fieldDeclaration );
  }

  @NotNull
  public FieldInitializedInConstructorInfo getFieldInitializeInConstructorInfo( @NotNull FieldDeclaration fieldDeclaration ) {
    ConstructorCallInfo constructorCallInfo = findConstructorParamDeclarationForField( fieldDeclaration );
    return new FieldInitializedInConstructorInfo( fieldDeclaration, constructorCallInfo );
  }

  public static class ConstructorCallInfo {
    @NotNull
    private final ConstructorDeclaration constructorDeclaration;
    private final int index;
    @NotNull
    private final ParameterDeclaration parameterDeclaration;

    public ConstructorCallInfo( @NotNull ConstructorDeclaration constructorDeclaration, int index, @NotNull ParameterDeclaration parameterDeclaration ) {
      this.constructorDeclaration = constructorDeclaration;
      this.index = index;
      this.parameterDeclaration = parameterDeclaration;
    }

    @NotNull
    public ConstructorDeclaration getConstructorDeclaration() {
      return constructorDeclaration;
    }

    public int getIndex() {
      return index;
    }

    @NotNull
    public ParameterDeclaration getParameterDeclaration() {
      return parameterDeclaration;
    }
  }
}
