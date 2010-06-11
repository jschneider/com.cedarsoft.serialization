package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.decision.DefaultXmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptorFactory;
import com.cedarsoft.serialization.generator.parsing.Parser;
import com.cedarsoft.serialization.generator.parsing.Result;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.testng.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.testng.Assert.*;

/**
 *
 */
public class StaxMateGeneratorTest {
  private DomainObjectDescriptor domainObjectDescriptor;
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

    DomainObjectDescriptorFactory factory = new DomainObjectDescriptorFactory( parsed.getClassDeclarations().get( 0 ) );
    domainObjectDescriptor = factory.create();
    assertNotNull( domainObjectDescriptor );

    assertEquals( domainObjectDescriptor.getFieldsToSerialize().size(), 5 );
    generator = new StaxMateGenerator( new DefaultXmlDecisionCallback( "width", "height" ) );
    model = generator.getCodeGenerator().getModel();
  }

  @Test
  public void testIt() throws IOException, JClassAlreadyExistsException {
    generator.generate( domainObjectDescriptor );

    JPackage thePackage = model._package( "com.cedarsoft.serialization.generator.staxmate.test" );
    JDefinedClass definedClass = thePackage._getClass( "WindowSerializer" );
    assertNotNull( definedClass );

    assertEquals( definedClass.name(), "WindowSerializer" );


    ByteArrayOutputStream out = new ByteArrayOutputStream();
    model.build( new SingleStreamCodeWriter( out ) );

    assertEquals( out.toString(), "asdf" );
  }
}
