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


    String expected = "asdf";
    assertGeneratedCode( expected );
  }
}
