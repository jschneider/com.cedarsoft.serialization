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

package com.cedarsoft.serialization.generator.output.staxmate.serializer.test;

import com.cedarsoft.serialization.generator.output.staxmate.serializer.AbstractGeneratorTest;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class SerializerTestGeneratorTest extends AbstractGeneratorTest {
  private StaxMateGenerator generator;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    generator = new StaxMateGenerator( codeGenerator );
  }

  @Test
  public void testNames() {
    assertEquals( generator.createSerializerTestName( "com.test.Serializer" ), "com.test.SerializerTest" );
  }

  @Test
  public void testGenerateVersionsTest() throws JClassAlreadyExistsException, IOException {
    JClass serializerClass = model.ref( "com.cedarsoft.serialization.generator.staxmate.test.WindowSerializer" );
    JDefinedClass serializerVersionTestClass = generator.generateSerializerVersionTest( serializerClass, domainObjectDescriptor );

    assertEquals( serializerVersionTestClass.name(), "WindowSerializerVersionTest" );
    assertEquals( serializerVersionTestClass.getPackage().name(), "com.cedarsoft.serialization.generator.staxmate.test" );

    assertGeneratedCode( "-----------------------------------com.cedarsoft.serialization.generator.staxmate.test.WindowSerializerVersionTest.java-----------------------------------\n" +
      "\n" +
      "package com.cedarsoft.serialization.generator.staxmate.test;\n" +
      "\n" +
      "import java.util.HashMap;\n" +
      "import java.util.Map;\n" +
      "import com.cedarsoft.Version;\n" +
      "import com.cedarsoft.serialization.AbstractXmlVersionTest;\n" +
      "import com.cedarsoft.serialization.Serializer;\n" +
      "import org.testng.Assert;\n" +
      "\n" +
      "public class WindowSerializerVersionTest\n" +
      "    extends AbstractXmlVersionTest<Window>\n" +
      "{\n" +
      "\n" +
      "\n" +
      "    @Override\n" +
      "    protected Serializer<Window> getSerializer()\n" +
      "        throws Exception\n" +
      "    {\n" +
      "        return new WindowSerializer();\n" +
      "    }\n" +
      "\n" +
      "    @Override\n" +
      "    protected Map<? extends Version, ? extends String> getSerializedXml() {\n" +
      "        Map<Version, String> map = new HashMap<Version, String>();\n" +
      "        map.put(Version.valueOf(1, 0, 0), \"<todo/>\");\n" +
      "        return map;\n" +
      "    }\n" +
      "\n" +
      "    @Override\n" +
      "    protected void verifyDeserialized(Window deserialized, Version version)\n" +
      "        throws Exception\n" +
      "    {\n" +
      "        Assert.assertEquals(deserialized.getWidth(), \"daValue\");\n" +
      "        Assert.assertEquals(deserialized.getHeight(), \"daValue\");\n" +
      "        Assert.assertEquals(deserialized.getDescription(), \"daValue\");\n" +
      "        Assert.assertEquals(deserialized.getAnInt(), \"daValue\");\n" +
      "        Assert.assertEquals(deserialized.getFloatField(), \"daValue\");\n" +
      "    }\n" +
      "\n" +
      "}" );
  }

  @Test
  public void testIt() throws JClassAlreadyExistsException, IOException {
    JClass serializerClass = model.ref( "com.cedarsoft.serialization.generator.staxmate.test.WindowSerializer" );

    JDefinedClass serializerTestClass = generator.generateSerializerTest( serializerClass.fullName(), domainObjectDescriptor );
    assertEquals( serializerTestClass.name(), "WindowSerializerTest" );
    assertEquals( serializerTestClass.getPackage().name(), "com.cedarsoft.serialization.generator.staxmate.test" );

    JPackage thePackage = model._package( "com.cedarsoft.serialization.generator.staxmate.test" );
    JDefinedClass definedClass = thePackage._getClass( "WindowSerializerTest" );
    assertNotNull( definedClass );
    assertEquals( definedClass.name(), "WindowSerializerTest" );


    assertGeneratedCode( "-----------------------------------com.cedarsoft.serialization.generator.staxmate.test.WindowSerializerTest.java-----------------------------------\n" +
      "\n" +
      "package com.cedarsoft.serialization.generator.staxmate.test;\n" +
      "\n" +
      "import java.util.Arrays;\n" +
      "import java.util.List;\n" +
      "import com.cedarsoft.serialization.AbstractXmlSerializerMultiTest;\n" +
      "import com.cedarsoft.serialization.Serializer;\n" +
      "\n" +
      "public class WindowSerializerTest\n" +
      "    extends AbstractXmlSerializerMultiTest<Window>\n" +
      "{\n" +
      "\n" +
      "\n" +
      "    @Override\n" +
      "    protected Serializer<Window> getSerializer()\n" +
      "        throws Exception\n" +
      "    {\n" +
      "        return new WindowSerializer();\n" +
      "    }\n" +
      "\n" +
      "    @Override\n" +
      "    protected Iterable<? extends Window> createObjectsToSerialize()\n" +
      "        throws Exception\n" +
      "    {\n" +
      "        return Arrays.asList(new Window(\"description\", 12.5D, 42, Integer.valueOf(42)), new Window(\"description\", 12.5D, 42, Integer.valueOf(42)), new Window(\"description\", 12.5D, 42, Integer.valueOf(42)));\n" +
      "    }\n" +
      "\n" +
      "    @Override\n" +
      "    protected List<? extends String> getExpectedSerialized() {\n" +
      "        return Arrays.asList(\"<implementMe/>\", \"<implementMe/>\", \"<implementMe/>\");\n" +
      "    }\n" +
      "\n" +
      "}" );
  }
}
