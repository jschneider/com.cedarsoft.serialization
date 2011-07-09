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

package com.cedarsoft.serialization.generator.output.serializer.test;

import com.cedarsoft.Version;
import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.DecisionCallback;
import com.cedarsoft.codegen.model.DomainObjectDescriptor;
import com.cedarsoft.codegen.model.FieldWithInitializationInfo;
import com.cedarsoft.serialization.test.utils.Entry;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.test.utils.VersionEntry;
import com.cedarsoft.serialization.generator.output.GeneratorBase;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;

import javax.annotation.Nonnull;

/**
 * @param <T>
 */
public abstract class AbstractGenerator<T extends DecisionCallback> extends GeneratorBase<T> {

  @Nonnull
  public static final String SERIALIZER_TEST_NAME_SUFFIX = "Test";

  @Nonnull
  public static final String SERIALIZER_VERSION_TEST_NAME_SUFFIX = "VersionTest";

  public static final String METHOD_NAME_GET_SERIALIZER = "getSerializer";

  public static final String METHOD_NAME_VERIFY_DESERIALIZED = "verifyDeserialized";

  public static final String PARAM_NAME_DESERIALIZED = "deserialized";

  public static final String PARAM_NAME_VERSION = "version";

  public static final String METHOD_NAME_ASSERT_EQUALS = "assertEquals";

  public static final String METHOD_NAME_CREATE = "create";

  public static final String DATA_POINT_FIELD_NAME = "ENTRY1";

  public static final String METHOD_NAME_VALUE_OF = "valueOf";

  public static final String CLASS_NAME_ASSERT = "org.junit.Assert";

  protected AbstractGenerator( @Nonnull CodeGenerator codeGenerator ) {
    super( codeGenerator );
  }

  @Nonnull
  public JDefinedClass generateSerializerVersionTest( @Nonnull String serializerClassName, @Nonnull DomainObjectDescriptor domainObjectDescriptor ) throws JClassAlreadyExistsException {
    JClass domainType = codeGenerator.ref( domainObjectDescriptor.getQualifiedName() );
    JClass serializerClass = codeGenerator.ref( serializerClassName );

    //the class
    JDefinedClass testClass = codeModel._class( createSerializerVersionTestName( serializerClass.fullName() ) )._extends( createVersionExtendsClass( domainType, serializerClass ) );

    //getSerializer
    createGetSerializerMethod( testClass, serializerClass, domainType );
    createVersionVerifyMethod( testClass, serializerClass, domainObjectDescriptor );
    createVersionsDataPoint( testClass, serializerClass, domainType, domainObjectDescriptor );

    return testClass;
  }

  protected void createVersionsDataPoint( @Nonnull JDefinedClass testClass, @Nonnull JClass serializerClass, @Nonnull JClass domainType, @Nonnull DomainObjectDescriptor domainObjectDescriptor ) {
    JFieldVar field = testClass.field( JMod.STATIC | JMod.PUBLIC | JMod.FINAL, codeGenerator.ref( VersionEntry.class ), DATA_POINT_FIELD_NAME );
    field.annotate( codeGenerator.ref( "org.junit.experimental.theories.DataPoint" ) );

    JInvocation versionInvocation = codeGenerator.ref( Version.class ).staticInvoke( METHOD_NAME_VALUE_OF ).arg( JExpr.lit( 1 ) ).arg( JExpr.lit( 0 ) ).arg( JExpr.lit( 0 ) );
    JExpression expected = createExpectedExpression( testClass, domainType );

    field.init( testClass.staticInvoke( METHOD_NAME_CREATE ).arg( versionInvocation ).arg( expected ) );
  }

  protected void createVersionVerifyMethod( @Nonnull JDefinedClass testClass, @Nonnull JClass serializerClass, @Nonnull DomainObjectDescriptor domainObjectDescriptor ) {
    JClass domainType = codeGenerator.ref( domainObjectDescriptor.getQualifiedName() );

    JMethod method = testClass.method( JMod.PROTECTED, Void.TYPE, METHOD_NAME_VERIFY_DESERIALIZED )._throws( Exception.class );
    method.annotate( Override.class );
    JVar deserialized = method.param( domainType, PARAM_NAME_DESERIALIZED );
    method.param( Version.class, PARAM_NAME_VERSION );

    JClass assertClass = codeGenerator.ref( CLASS_NAME_ASSERT );

    for ( FieldWithInitializationInfo fieldInfo : domainObjectDescriptor.getFieldInfos() ) {
      method.body().add( assertClass.staticInvoke( METHOD_NAME_ASSERT_EQUALS ).arg( "daValue" ).arg( deserialized.invoke( fieldInfo.getGetterDeclaration().getSimpleName() ) ) );
    }
  }

  @Nonnull
  public JDefinedClass generateSerializerTest( @Nonnull String serializerClassName, @Nonnull DomainObjectDescriptor domainObjectDescriptor ) throws JClassAlreadyExistsException {
    JClass domainType = codeGenerator.ref( domainObjectDescriptor.getQualifiedName() );
    JClass serializerClass = codeGenerator.ref( serializerClassName );

    //the class
    JDefinedClass testClass = codeModel._class( createSerializerTestName( serializerClassName ) )._extends( createExtendsClass( domainType, serializerClass ) );

    //getSerializer
    createGetSerializerMethod( testClass, serializerClass, domainType );
    createDataPoint( testClass, serializerClass, domainType, domainObjectDescriptor );

    return testClass;
  }

  protected void createDataPoint( @Nonnull JDefinedClass testClass, @Nonnull JClass serializerClass, @Nonnull JClass domainType, @Nonnull DomainObjectDescriptor domainObjectDescriptor ) {
    JFieldVar field = testClass.field( JMod.STATIC | JMod.PUBLIC | JMod.FINAL, codeGenerator.ref( Entry.class ).narrow( domainType.wildcard() ), DATA_POINT_FIELD_NAME );
    field.annotate( codeModel.ref( "org.junit.experimental.theories.DataPoint" ) );

    JInvocation domainObjectCreation = createDomainObjectCreationExpression( domainObjectDescriptor );
    JExpression expected = createExpectedExpression( testClass, domainType );

    field.init( testClass.staticInvoke( METHOD_NAME_CREATE ).arg( domainObjectCreation ).arg( expected ) );
  }

  @Nonnull
  protected abstract JExpression createExpectedExpression( @Nonnull JClass testClass, @Nonnull JClass domainType );

  @Nonnull
  protected JInvocation createDomainObjectCreationExpression( @Nonnull DomainObjectDescriptor domainObjectDescriptor ) {
    JInvocation invocation = JExpr._new( codeGenerator.ref( domainObjectDescriptor.getQualifiedName() ) );

    ConstructorDeclaration constructor = domainObjectDescriptor.findBestConstructor();
    for ( ParameterDeclaration parameterDeclaration : constructor.getParameters() ) {
      invocation.arg( codeGenerator.getNewInstanceFactory().create( parameterDeclaration.getType(), parameterDeclaration.getSimpleName() ) );
    }

    return invocation;
  }

  @Nonnull
  protected JMethod createGetSerializerMethod( @Nonnull JDefinedClass serializerTestClass, @Nonnull JClass serializerClass, @Nonnull JClass domainType ) {
    JType returnType = codeGenerator.ref( Serializer.class ).narrow( domainType );
    JMethod createSerializerMethod = serializerTestClass.method( JMod.PROTECTED, returnType, METHOD_NAME_GET_SERIALIZER )._throws( Exception.class );
    createSerializerMethod.annotate( Override.class );

    //Return the serializer
    createSerializerMethod.body()._return( JExpr._new( serializerClass ) );

    return createSerializerMethod;
  }

  @Nonnull
  protected abstract JClass createExtendsClass( @Nonnull JClass domainType, @Nonnull JClass serializerClass );

  @Nonnull
  protected abstract JClass createVersionExtendsClass( @Nonnull JClass domainType, @Nonnull JClass serializerClass );

  @Nonnull

  public String createSerializerTestName( @Nonnull String serializerClassName ) {
    return serializerClassName + SERIALIZER_TEST_NAME_SUFFIX;
  }

  @Nonnull

  public String createSerializerVersionTestName( @Nonnull String serializerClassName ) {
    return serializerClassName + SERIALIZER_VERSION_TEST_NAME_SUFFIX;
  }
}
