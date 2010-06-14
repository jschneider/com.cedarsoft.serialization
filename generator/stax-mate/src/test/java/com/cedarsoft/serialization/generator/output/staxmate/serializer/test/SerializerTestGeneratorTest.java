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
    assertEquals( generator.createSerializerClassTestName( "com.test.Serializer" ), "com.test.SerializerTest" );
  }

  @Test
  public void testIt() throws JClassAlreadyExistsException, IOException {
    JClass serializerClass = model.ref( "com.cedarsoft.serialization.generator.staxmate.test.WindowSerializer" );

    JDefinedClass serializerTestClass = generator.generateSerializerTest( serializerClass, domainObjectDescriptor );
    assertEquals( serializerTestClass.name(), "WindowSerializerTest" );
    assertEquals( serializerTestClass.getPackage().name(), "com.cedarsoft.serialization.generator.staxmate.test" );

    JPackage thePackage = model._package( "com.cedarsoft.serialization.generator.staxmate.test" );
    JDefinedClass definedClass = thePackage._getClass( "WindowSerializerTest" );
    assertNotNull( definedClass );
    assertEquals( definedClass.name(), "WindowSerializerTest" );


    String expected = "-----------------------------------com.cedarsoft.serialization.generator.staxmate.test.WindowSerializerTest.java-----------------------------------\n" +
      "\n" +
      "package com.cedarsoft.serialization.generator.staxmate.test;\n" +
      "\n" +
      "import com.cedarsoft.serialization.AbstractXmlSerializerTest;\n" +
      "import com.cedarsoft.serialization.Serializer;\n" +
      "\n" +
      "public class WindowSerializerTest\n" +
      "    extends AbstractXmlSerializerTest<Window>\n" +
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
      "    protected Window createObjectToSerialize()\n" +
      "        throws Exception\n" +
      "    {\n" +
      "        return new Window(\"description\", 12.5D, 42, Integer.valueOf(42));\n" +
      "    }\n" +
      "\n" +
      "    @Override\n" +
      "    protected String getExpectedSerialized() {\n" +
      "        return \"<implementMe/>\";\n" +
      "    }\n" +
      "\n" +
      "}";
    assertGeneratedCode( expected );
  }
}
