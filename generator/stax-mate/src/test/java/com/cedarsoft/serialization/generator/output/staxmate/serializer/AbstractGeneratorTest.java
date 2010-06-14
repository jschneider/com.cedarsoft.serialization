package com.cedarsoft.serialization.generator.output.staxmate.serializer;

import com.cedarsoft.serialization.generator.decision.DefaultXmlDecisionCallback;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptorFactory;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.serializer.I18nAnnotationsDecorator;
import com.cedarsoft.serialization.generator.output.serializer.NotNullDecorator;
import com.cedarsoft.serialization.generator.parsing.Parser;
import com.cedarsoft.serialization.generator.parsing.Result;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.testng.Assert.*;

/**
 *
 */
public class AbstractGeneratorTest {
  protected DomainObjectDescriptor domainObjectDescriptor;
  protected CodeGenerator<XmlDecisionCallback> codeGenerator;

  protected JCodeModel model;

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
    final DefaultXmlDecisionCallback decisionCallback = new DefaultXmlDecisionCallback( "width", "height" );
    CodeGenerator<XmlDecisionCallback> codeGenerator = new CodeGenerator<XmlDecisionCallback>( decisionCallback );
    this.codeGenerator = codeGenerator;
    this.codeGenerator.addMethodDecorator( new NotNullDecorator( NotNull.class ) );
    codeGenerator.addMethodDecorator( new I18nAnnotationsDecorator( NonNls.class ) );
    model = codeGenerator.getModel();
  }

  protected void assertGeneratedCode( @NotNull @NonNls String expected ) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    model.build( new SingleStreamCodeWriter( out ) );

    assertEquals( out.toString().trim(), expected.trim() );
  }
}
