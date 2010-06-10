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

package com.cedarsoft.serialization.codemodel;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import com.sun.tools.jxc.SchemaGenerator;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.EventListener;

/**
 *
 */
public class CodemodelTest {
  @Test
  public void testIt() throws IOException, JClassAlreadyExistsException, InterruptedException {
    CodeWriter codeWriter = new SingleStreamCodeWriter( System.out );

    JCodeModel codeModel = new JCodeModel();
    {
      JDefinedClass fooClass = codeModel._class( "com.cedarsoft.generator.test.Foo" );
      fooClass._implements( EventListener.class );
      fooClass.field( JMod.PRIVATE, String.class, "id" );
    }
    {
      JDefinedClass barClass = codeModel._class( "com.cedarsoft.generator.test.bar.Bar" );
      barClass._implements( EventListener.class );
      barClass.field( Modifier.PRIVATE | Modifier.FINAL, Integer.TYPE, "id" );
      barClass.field( JMod.PRIVATE | JMod.FINAL, Integer.TYPE, "id2" );
    }

    codeModel.build( codeWriter );
    Thread.sleep( 100 );
  }


//  @Test
//  public void testSchmeaGenerator() throws Exception {
//    SchemaGenerator.main( new String[]{"/home/johannes/projects/com.cedarsoft.serialization/generator/common/src/test/java/com/cedarsoft/serialization/codemodel/JavaClassToParse.java",
//    } );
//  }
}
