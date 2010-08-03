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

import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.NamingSupport;
import com.cedarsoft.serialization.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.AbstractXmlVersionTest2;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.fmt.JTextFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class XmlGenerator extends AbstractGenerator<XmlDecisionCallback> {
  @NonNls
  @NotNull
  public static final String METHOD_GET_RESOURCE = "getResource";

  public XmlGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  @NotNull
  @Override
  protected JClass createExtendsClass( @NotNull JClass domainType, @NotNull JClass serializerClass ) {
    return codeGenerator.ref( AbstractXmlSerializerTest2.class ).narrow( domainType );
  }

  @NotNull
  @Override
  protected JClass createVersionExtendsClass( @NotNull JClass domainType, @NotNull JClass serializerClass ) {
    return codeGenerator.ref( AbstractXmlVersionTest2.class ).narrow( domainType );
  }

  @NotNull
  @Override
  protected JExpression createExpectedExpression( @NotNull JClass testClass, @NotNull JClass domainType ) {
    String resourceName = testClass.name() + ".1.xml";

    JTextFile resource = new JTextFile( resourceName );
    testClass._package().addResourceFile( resource );

    resource.setContents( createSampleContent( domainType ) );

    return testClass.dotclass().invoke( METHOD_GET_RESOURCE ).arg( resourceName );
  }

  private String createSampleContent( @NotNull JClass domainType ) {
    String simpleName = NamingSupport.createVarName( domainType.name() );

    return "<?xml version=\"1.0\"?>\n" +
      "<" + simpleName + ">\n" +
      "</" + simpleName + ">\n"
      ;
  }
}
