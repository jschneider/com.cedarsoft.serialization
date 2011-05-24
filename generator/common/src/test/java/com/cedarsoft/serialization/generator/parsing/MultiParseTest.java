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

package com.cedarsoft.serialization.generator.parsing;

import com.cedarsoft.codegen.parser.Parser;
import com.cedarsoft.codegen.parser.Result;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sun.mirror.declaration.ClassDeclaration;
import org.junit.*;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class MultiParseTest {
  @Nonnull
  public static final List<URL> classes = ImmutableList.of(
    MultiParseTest.class.getResource( "test/Door.java" ),
    MultiParseTest.class.getResource( "test/House.java" ),
    MultiParseTest.class.getResource( "test/Room.java" ),
    MultiParseTest.class.getResource( "test/Window.java" )
  );

  public static final List<File> files = Lists.transform( classes, new Function<URL, File>() {
    @Override
    public File apply( URL from ) {
      try {
        File javaFile = new File( from.toURI() );
        assertTrue( javaFile.exists() );
        return javaFile;
      } catch ( URISyntaxException e ) {
        throw new RuntimeException( e );
      }
    }
  } );

  @Test
  public void testIt() {
    Result parsed = Parser.parse( null, files );
    assertEquals( 4, parsed.getClassDeclarations().size() );
    ClassDeclaration doorDeclaration = parsed.getClassDeclaration( "com.cedarsoft.serialization.generator.parsing.test.Door" );
    assertEquals( 2, doorDeclaration.getFields().size() );
  }
}
