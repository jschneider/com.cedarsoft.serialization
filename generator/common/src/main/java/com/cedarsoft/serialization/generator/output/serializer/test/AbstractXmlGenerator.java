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
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class AbstractXmlGenerator extends AbstractGenerator<XmlDecisionCallback> {
  @NonNls
  public static final String METHOD_NAME_GET_EXPECTED_SERIALIZED = "getExpectedSerialized";
  @NonNls
  public static final String METHOD_NAME_GET_SERIALIZED_XML = "getSerializedXml";

  protected AbstractXmlGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  @Override
  protected void createGetSerializedMethod( @NotNull JDefinedClass testClass, @NotNull JClass serializerClass, @NotNull JClass domainType ) {
    JClass versionRef = codeModel.ref( Version.class );

    JClass returnType = codeModel.ref( Map.class ).narrow( versionRef.wildcard(), codeModel.ref( String.class ).wildcard() );

    JMethod method = testClass.method( JMod.PROTECTED, returnType, METHOD_NAME_GET_SERIALIZED_XML );
    method.annotate( Override.class );

    JClass mapType = codeModel.ref( Map.class ).narrow( Version.class, String.class );
    JClass hashMapType = codeModel.ref( HashMap.class ).narrow( Version.class, String.class );

    JVar map = method.body().decl( mapType, "map", JExpr._new( hashMapType ) );

    JInvocation invocation = map.invoke( "put" ).arg( versionRef.staticInvoke( "valueOf" ).arg( JExpr.lit( 1 ) ).arg( JExpr.lit( 0 ) ).arg( JExpr.lit( 0 ) ) ).arg( "<todo/>" );

    method.body().add( invocation );
    method.body()._return( map );
  }

  @Override
  @NotNull
  protected JMethod createVerifyMethod( @NotNull JDefinedClass serializerTestClass, @NotNull JClass serializerClass, @NotNull JClass domainType ) {
    JClass returnType = codeModel.ref( List.class ).narrow( codeModel.ref( String.class ).wildcard() );
    JMethod method = serializerTestClass.method( JMod.PROTECTED, returnType, METHOD_NAME_GET_EXPECTED_SERIALIZED );
    method.annotate( Override.class );


    JInvocation asListInvocation = codeModel.ref( Arrays.class ).staticInvoke( "asList" );
    for ( int i = 0; i < NUMBER_OF_OBJECTS; i++ ) {
      asListInvocation.arg( JExpr.lit( "<implementMe/>" ) );
    }

    method.body()._return( asListInvocation );
    return method;
  }
}
