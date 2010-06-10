package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.generator.model.ClassToSerialize;
import com.cedarsoft.serialization.generator.model.ModelFactory;
import com.cedarsoft.serialization.generator.parsing.Parser;
import com.cedarsoft.serialization.generator.parsing.Result;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.testng.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import static org.testng.Assert.*;

/**
 *
 */
public class StaxMateGeneratorTest {
  private ClassToSerialize classToSerialize;
  private StaxMateGenerator generator;
  private JCodeModel model;

  @BeforeMethod
  protected void setUp() throws Exception {
    URL resource = getClass().getResource( "/com/cedarsoft/serialization/generator/staxmate/test/Window.java" );
    assertNotNull( resource );
    File javaFile = new File( resource.toURI() );
    assertTrue( javaFile.exists() );
    Result parsed = Parser.parse( javaFile );
    assertNotNull( parsed );

    ModelFactory factory = new ModelFactory( parsed.getClassDeclarations().get( 0 ) );
    classToSerialize = factory.create();
    assertNotNull( classToSerialize );

    assertEquals( classToSerialize.getFieldsToSerialize().size(), 3 );
    generator = new StaxMateGenerator( new SerializingEntryCreators() );
    model = generator.getModel();
  }

  @Test
  public void testName() {
    assertEquals( generator.createSerializerClassName( "com.cedarsoft.serialization.generator.staxmate.StaxMateGenerator" ), "com.cedarsoft.serialization.generator.staxmate.StaxMateGeneratorSerializer" );
    assertEquals( generator.createSerializerClassName( "java.lang.String" ), "java.lang.StringSerializer" );
  }

  @Test
  public void testVersionRangeInvo() {
    StringWriter out = new StringWriter();
    generator.createDefaultVersionRangeInvocation( Version.valueOf( 1, 0, 0 ), Version.valueOf( 1, 0, 0 ) ).state( new JFormatter( out ) );
    assertEquals( out.toString().trim(), "com.cedarsoft.VersionRange.from(1, 0, 0).to(1, 0, 0);" );
  }

  @Test
  public void testNameSpace() {
    assertEquals( generator.getNamespace( classToSerialize ), "http://www.cedarsoft.com/serialization/generator/staxmate/test/Window/1.0.0" );
  }

  @Test
  public void testIt() throws IOException, JClassAlreadyExistsException {
    generator.generate( classToSerialize );

    JPackage thePackage = model._package( "com.cedarsoft.serialization.generator.staxmate.test" );
    JDefinedClass definedClass = thePackage._getClass( "WindowSerializer" );
    assertNotNull( definedClass );

    assertEquals( definedClass.name(), "WindowSerializer" );


    ByteArrayOutputStream out = new ByteArrayOutputStream();
    model.build( new SingleStreamCodeWriter( out ) );

    assertEquals( out.toString(), "asdf" );
  }
}
