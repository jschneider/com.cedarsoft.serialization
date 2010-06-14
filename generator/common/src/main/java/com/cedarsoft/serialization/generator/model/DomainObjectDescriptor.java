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

import com.google.common.collect.Lists;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.type.TypeMirror;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class DomainObjectDescriptor {
  @NotNull
  private final List<FieldWithInitializationInfo> fieldsToSerialize = Lists.newArrayList();
  @NotNull
  private final ClassDeclaration classDeclaration;

  public DomainObjectDescriptor( @NotNull @NonNls ClassDeclaration classDeclaration ) {
    this.classDeclaration = classDeclaration;
  }

  @NotNull
  @NonNls
  public String getQualifiedName() {
    return classDeclaration.getQualifiedName();
  }

  @NotNull
  public ClassDeclaration getClassDeclaration() {
    return classDeclaration;
  }

  public void addField( @NotNull FieldWithInitializationInfo fieldToSerialize ) {
    this.fieldsToSerialize.add( fieldToSerialize );
  }

  @NotNull
  public List<? extends FieldWithInitializationInfo> getFieldsToSerialize() {
    return Collections.unmodifiableList( fieldsToSerialize );
  }

  /**
   * Returns only the field infos that are initialized using the constructor
   *
   * @return the field infos initialized within the constructor
   */
  @NotNull
  public List<? extends FieldInitializedInConstructorInfo> getFieldsInitializedInConstructor() {
    List<FieldInitializedInConstructorInfo> found = new ArrayList<FieldInitializedInConstructorInfo>();
    for ( FieldWithInitializationInfo info : fieldsToSerialize ) {
      if ( info instanceof FieldInitializedInConstructorInfo ) {
        found.add( ( FieldInitializedInConstructorInfo ) info );
      }
    }

    Collections.sort( found, new FieldWithInitializationInfoComparator() );

    return found;
  }

  @NotNull
  public List<? extends FieldInitializedInSetterInfo> getFieldsInitializedInSetter() {
    List<FieldInitializedInSetterInfo> found = new ArrayList<FieldInitializedInSetterInfo>();
    for ( FieldWithInitializationInfo info : fieldsToSerialize ) {
      if ( info instanceof FieldInitializedInSetterInfo ) {
        found.add( ( FieldInitializedInSetterInfo ) info );
      }
    }

    return found;
  }

  @NotNull
  public ConstructorDeclaration findSimplestConstructor() {
    return findSimplestConstructor( classDeclaration );
  }

  @NotNull
  public static ConstructorDeclaration findSimplestConstructor( @NotNull ClassDeclaration classDeclaration ) {
    ConstructorDeclaration currentlyBest = null;
    for ( ConstructorDeclaration constructorDeclaration : classDeclaration.getConstructors() ) {
      if ( currentlyBest == null || constructorDeclaration.getParameters().size() < currentlyBest.getParameters().size() ) {
        currentlyBest = constructorDeclaration;
      }
    }

    if ( currentlyBest == null ) {
      throw new IllegalStateException( "No constructor found in " + classDeclaration.getSimpleName() );
    }
    return currentlyBest;
  }

  @NotNull
  public ConstructorDeclaration findBestConstructor() {
    return findBestConstructor( classDeclaration );
  }

  @NotNull
  public static ConstructorDeclaration findBestConstructor( @NotNull ClassDeclaration classDeclaration ) {
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
  public MethodDeclaration findSetter( @NotNull @NonNls String simpleName, @NotNull TypeMirror type ) {
    return findSetter( classDeclaration, simpleName, type );
  }

  /**
   * @param classDeclaration the class declaration
   * @param simpleName       the simple name
   * @param type             the type
   * @return the method declaration for the setter
   *
   * @noinspection TypeMayBeWeakened
   */
  @NotNull
  public static MethodDeclaration findSetter( @NotNull ClassDeclaration classDeclaration, @NotNull @NonNls String simpleName, @NotNull TypeMirror type ) throws IllegalArgumentException {
    String expectedName = "set" + simpleName.substring( 0, 1 ).toUpperCase() + simpleName.substring( 1 );

    for ( MethodDeclaration methodDeclaration : classDeclaration.getMethods() ) {
      if ( !methodDeclaration.getSimpleName().equals( expectedName ) ) {
        continue;
      }

      if ( methodDeclaration.getParameters().size() != 1 ) {
        throw new IllegalArgumentException( "Expected one parameter. But was <" + methodDeclaration.getParameters() + ">" );
      }

      ParameterDeclaration parameterDeclaration = methodDeclaration.getParameters().iterator().next();
      if ( !parameterDeclaration.getType().equals( type ) ) {
        throw new IllegalArgumentException( "Invalid parameter type for <" + expectedName + ">. Was <" + parameterDeclaration.getType() + "> but expected <" + type + ">" );
      }

      return methodDeclaration;
    }

    throw new IllegalArgumentException( "No method declaration found for <" + expectedName + ">" );
  }

  @NotNull
  public MethodDeclaration findSetter( @NotNull FieldDeclaration fieldDeclaration ) {
    return findSetter( classDeclaration, fieldDeclaration );
  }

  @NotNull
  public static MethodDeclaration findSetter( @NotNull ClassDeclaration classDeclaration, @NotNull FieldDeclaration fieldDeclaration ) {
    return findSetter( classDeclaration, fieldDeclaration.getSimpleName(), fieldDeclaration.getType() );
  }

  @NotNull
  public MethodDeclaration findGetterForField( @NotNull FieldDeclaration fieldDeclaration ) {
    return findGetterForField( classDeclaration, fieldDeclaration );
  }

  public static MethodDeclaration findGetterForField( ClassDeclaration classDeclaration, @NotNull FieldDeclaration fieldDeclaration ) {
    return findGetterForField( classDeclaration, fieldDeclaration.getSimpleName(), fieldDeclaration.getType() );
  }

  @NotNull
  public MethodDeclaration findGetterForField( @NotNull @NonNls String simpleName, @NotNull TypeMirror type ) {
    return findGetterForField( classDeclaration, simpleName, type );
  }

  /**
   * @param classDeclaration the class declaration
   * @param simpleName       the simple name
   * @param type             the type
   * @return the getter declaration
   *
   * @noinspection TypeMayBeWeakened
   */
  public static MethodDeclaration findGetterForField( @NotNull ClassDeclaration classDeclaration, @NotNull @NonNls String simpleName, @NotNull TypeMirror type ) {
    String expectedName = "get" + simpleName.substring( 0, 1 ).toUpperCase() + simpleName.substring( 1 );

    for ( MethodDeclaration methodDeclaration : classDeclaration.getMethods() ) {
      if ( methodDeclaration.getSimpleName().equals( expectedName ) ) {
        if ( methodDeclaration.getReturnType().equals( type ) ) {
          return methodDeclaration;
        } else {
          throw new IllegalArgumentException( "Invalid return types for <" + expectedName + ">. Was <" + methodDeclaration.getReturnType() + "> but expected <" + type + ">" );
        }
      }
    }

    throw new IllegalArgumentException( "No method declaration found for <" + expectedName + ">" );
  }

  @NotNull
  public FieldDeclaration findFieldDeclaration( @NotNull @NonNls String fieldName ) {
    return findFieldDeclaration( classDeclaration, fieldName );
  }

  /**
   * @param classDeclaration the class declaration
   * @param fieldName        the field name
   * @return the field declaration
   *
   * @noinspection TypeMayBeWeakened
   */
  @NotNull
  public static FieldDeclaration findFieldDeclaration( @NotNull ClassDeclaration classDeclaration, @NotNull @NonNls String fieldName ) {
    for ( FieldDeclaration fieldDeclaration : classDeclaration.getFields() ) {
      if ( fieldDeclaration.getSimpleName().equals( fieldName ) ) {
        return fieldDeclaration;
      }
    }

    throw new IllegalArgumentException( "No field delaration found for <" + fieldName + ">" );
  }

  private static class FieldWithInitializationInfoComparator implements Comparator<FieldInitializedInConstructorInfo>, Serializable {
    @Override
    public int compare( FieldInitializedInConstructorInfo o1, FieldInitializedInConstructorInfo o2 ) {
      return Integer.valueOf( o1.getConstructorCallInfo().getIndex() ).compareTo( o2.getConstructorCallInfo().getIndex() );
    }
  }
}
