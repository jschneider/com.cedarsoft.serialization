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

package com.cedarsoft.serialization.generator;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.SystemOutRule;
import com.cedarsoft.codegen.GeneratorConfiguration;
import com.google.common.collect.ImmutableList;
import com.sun.codemodel.JClassAlreadyExistsException;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 *
 */
public class GeneratorTest {
  @Rule
  public SystemOutRule systemOutRule = new SystemOutRule();

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  private File destDir;
  private File testDestDir;
  private File destResDir;
  private File testResDestDir;

  @Before
  public void setUp() throws Exception {
    destDir = tmp.newFolder( "dest" );
    destResDir = tmp.newFolder( "resources-dest" );
    testDestDir = tmp.newFolder( "test-dest" );
    testResDestDir = tmp.newFolder( "test-resources-dest" );
  }

  @Test
  public void testHelp() throws Exception {
    StaxMateGenerator.main( new String[]{} );
    assertEquals( "Missing required options: d, t\n" +
      "usage: ser-gen -d <serializer dest dir> -t <test dest dir> path-to-class\n" +
      "-d,--destination <arg>               the output directory for the created\n" +
      "                                     classes\n" +
      "-h,--help                            display this use message\n" +
      "-r,--resources-destination <arg>     the output directory for the created\n" +
      "                                     resources\n" +
      "-s,--test-resources-destination <arg>the output directory for the created\n" +
      "                                     test resources\n" +
      "-t,--test-destination <arg>          the output directory for the created\n" +
      "                                     tests\n", systemOutRule.getOutAsString() );
    assertEquals( "", systemOutRule.getErrAsString() );
  }

  @Test
  public void testRun() throws Exception {
    File javaFile = new File( getClass().getResource( "/com/cedarsoft/serialization/generator/staxmate/test/Foo.java" ).toURI() );
    assertTrue( javaFile.exists() );
    assertTrue( javaFile.isFile() );

    StaxMateGenerator.main( new String[]{"-d", destDir.getAbsolutePath(), "-r", destResDir.getAbsolutePath(), "-t", testDestDir.getAbsolutePath(), "-s", testResDestDir.getAbsolutePath(), javaFile.getPath()} );

    assertTrue( systemOutRule.getOutAsString(), systemOutRule.getOutAsString().contains( "com/cedarsoft/serialization/generator/staxmate/test/FooSerializer.java" ) );
    assertTrue( systemOutRule.getOutAsString(), systemOutRule.getOutAsString().contains( "com/cedarsoft/serialization/generator/staxmate/test/FooSerializerTest.java" ) );
    assertTrue( systemOutRule.getOutAsString(), systemOutRule.getOutAsString().contains( "com/cedarsoft/serialization/generator/staxmate/test/FooSerializerTest.java" ) );
    assertTrue( systemOutRule.getOutAsString(), systemOutRule.getOutAsString().contains( "com/cedarsoft/serialization/generator/staxmate/test/Foo.1.xml" ) );

    assertEquals( "", systemOutRule.getErrAsString() );

    File serializerFile = new File( destDir, "com/cedarsoft/serialization/generator/staxmate/test/FooSerializer.java" );
    assertTrue( serializerFile.exists() );

    AssertUtils.assertEquals( getClass().getResource( "GeneratorTest.testIt_1.txt" ), FileUtils.readFileToString( serializerFile ).trim() );

    verifyGeneratedTests();
  }

  private void verifyGeneratedTests() throws IOException {
    File serializerTestFile = new File( testDestDir, "com/cedarsoft/serialization/generator/staxmate/test/FooSerializerTest.java" );
    assertTrue( serializerTestFile.exists() );
    AssertUtils.assertEquals( getClass().getResource( "GeneratorTest.testIt_2.txt" ), FileUtils.readFileToString( serializerTestFile ).trim() );

    File serializerVersionTestFile = new File( testDestDir, "com/cedarsoft/serialization/generator/staxmate/test/FooSerializerVersionTest.java" );
    assertTrue( serializerVersionTestFile.exists() );
    AssertUtils.assertEquals( getClass().getResource( "GeneratorTest.testIt_3.txt" ), FileUtils.readFileToString( serializerVersionTestFile ).trim() );


    File resFile = new File( testResDestDir, "com/cedarsoft/serialization/generator/staxmate/test/Foo.1.xml" );
    assertTrue( resFile.exists() );
    AssertUtils.assertEquals( getClass().getResource( "GeneratorTest.testIt_4.txt" ), FileUtils.readFileToString( resFile ).trim() );
  }

  @Test
  public void testIt() throws URISyntaxException, IOException, JClassAlreadyExistsException {
    File javaFile = new File( getClass().getResource( "/com/cedarsoft/serialization/generator/staxmate/test/Foo.java" ).toURI() );

    GeneratorConfiguration configuration = new GeneratorConfiguration( ImmutableList.of( javaFile ), destDir, destResDir, testDestDir, testResDestDir, new PrintWriter( new ByteArrayOutputStream() ) );
    Generator.AbstractGeneratorRunner<?> runner = new StaxMateGenerator.StaxGeneratorRunner();
    runner.generate( configuration );


    File serializerFile = new File( destDir, "com/cedarsoft/serialization/generator/staxmate/test/FooSerializer.java" );
    assertTrue( serializerFile.exists() );

    AssertUtils.assertEquals( getClass().getResource( "GeneratorTest.testIt_1.txt" ), FileUtils.readFileToString( serializerFile ).trim() );

    verifyGeneratedTests();
  }

  @Test
  public void testOnlyTests() throws Exception {
    File javaFile = new File( getClass().getResource( "/com/cedarsoft/serialization/generator/staxmate/test/Foo.java" ).toURI() );

    GeneratorConfiguration configuration = new GeneratorConfiguration( ImmutableList.of( javaFile ), destDir, destResDir, testDestDir, testResDestDir, new PrintWriter( new ByteArrayOutputStream() ), GeneratorConfiguration.CreationMode.TESTS_ONLY );
    Generator.AbstractGeneratorRunner<?> runner = new StaxMateGenerator.StaxGeneratorRunner();
    runner.generate( configuration );

    File serializerFile = new File( destDir, "com/cedarsoft/serialization/generator/staxmate/test/FooSerializer.java" );
    assertFalse( serializerFile.exists() );

    verifyGeneratedTests();
  }
}
