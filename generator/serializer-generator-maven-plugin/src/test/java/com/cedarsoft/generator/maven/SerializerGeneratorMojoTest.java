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

package com.cedarsoft.generator.maven;

import com.cedarsoft.AssertUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.*;

import java.io.File;
import java.util.Iterator;

/**
 *
 */
public class SerializerGeneratorMojoTest extends AbstractMojoTestCase {
  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public void testIt() throws Exception {
    File testPom = new File( getBasedir(), "src/test/resources/unit/basic/basic-plugin-config.xml" );

    SerializerGeneratorMojo mojo = ( SerializerGeneratorMojo ) lookupMojo( "generate", testPom );
    assertNotNull( mojo );

    assertNotNull( mojo.projectArtifact );
    assertNotNull( mojo.outputDirectory );
    assertNotNull( mojo.domainClassSourceFile );
    assertTrue( mojo.domainClassSourceFile.exists() );

    //Clean up
    FileUtils.deleteQuietly( mojo.outputDirectory );
    FileUtils.deleteQuietly( mojo.testOutputDirectory );
    assertFalse( mojo.outputDirectory.exists() );
    assertFalse( mojo.testOutputDirectory.exists() );

    assertTrue( mojo.outputDirectory.getAbsolutePath(), mojo.outputDirectory.getAbsolutePath().endsWith( "target/test/unit/basic/target/out" ) );
    assertTrue( mojo.testOutputDirectory.getAbsolutePath(), mojo.testOutputDirectory.getAbsolutePath().endsWith( "target/test/unit/basic/target/test-out" ) );
    mojo.execute();

    assertEquals( 1, mojo.outputDirectory.list().length );
    assertEquals( 1, mojo.testOutputDirectory.list().length );

    {
      Iterator<File> iter = FileUtils.iterateFiles( mojo.outputDirectory, new String[]{"java"}, true );
      assertTrue( iter.hasNext() );
      File file = iter.next();
      assertEquals( "TestDomainObjectSerializer.java", file.getName() );

      AssertUtils.assertEquals( getClass().getResource( "/unit/basic/TestDomainObjectSerializer.java" ), FileUtils.readFileToString( file ) );
    }
  }

}
