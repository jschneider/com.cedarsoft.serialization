package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.decision.DefaultXmlDecisionCallback;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptorFactory;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.decorators.NotNullMethodDecorator;
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
    final DefaultXmlDecisionCallback decisionCallback = new DefaultXmlDecisionCallback( "width", "height" );
    CodeGenerator<XmlDecisionCallback> codeGenerator = new CodeGenerator<XmlDecisionCallback>( decisionCallback );
    codeGenerator.addMethodDecorator( new NotNullMethodDecorator() );
    generator = new StaxMateGenerator( codeGenerator );
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

    assertEquals( out.toString().trim(), "-----------------------------------com.cedarsoft.serialization.generator.staxmate.test.WindowSerializer.java-----------------------------------\n" +
      "\n" +
      "package com.cedarsoft.serialization.generator.staxmate.test;\n" +
      "\n" +
      "import java.io.IOException;\n" +
      "import javax.xml.stream.XMLStreamException;\n" +
      "import javax.xml.stream.XMLStreamReader;\n" +
      "import com.cedarsoft.Version;\n" +
      "import com.cedarsoft.VersionException;\n" +
      "import com.cedarsoft.VersionRange;\n" +
      "import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;\n" +
      "import org.codehaus.staxmate.out.SMOutputElement;\n" +
      "import org.jetbrains.annotations.NotNull;\n" +
      "\n" +
      "public class WindowSerializer\n" +
      "    extends AbstractStaxMateSerializer<Window>\n" +
      "{\n" +
      "\n" +
      "\n" +
      "    public WindowSerializer() {\n" +
      "        super(\"window\", \"http://www.cedarsoft.com/serialization/generator/staxmate/test/Window/1.0.0\", VersionRange.from(1, 0, 0).to(1, 0, 0));\n" +
      "    }\n" +
      "\n" +
      "    @Override\n" +
      "    public void serialize(\n" +
      "        @NotNull\n" +
      "        SMOutputElement serializeTo,\n" +
      "        @NotNull\n" +
      "        Window object)\n" +
      "        throws IOException, XMLStreamException\n" +
      "    {\n" +
      "        //width\n" +
      "        serializeTo.addAttribute(\"width\", String.valueOf(object.getWidth()));\n" +
      "        //height\n" +
      "        serializeTo.addAttribute(\"height\", String.valueOf(object.getHeight()));\n" +
      "        //description\n" +
      "        serializeTo.addElementWithCharacters(serializeTo.getNamespace(), \"description\", object.getDescription());\n" +
      "        //anInt\n" +
      "        serializeTo.addElementWithCharacters(serializeTo.getNamespace(), \"anInt\", String.valueOf(object.getAnInt()));\n" +
      "        //floatField\n" +
      "        serializeTo.addElementWithCharacters(serializeTo.getNamespace(), \"floatField\", String.valueOf(object.getFloatField()));\n" +
      "    }\n" +
      "\n" +
      "    @Override\n" +
      "    @NotNull\n" +
      "    public Window deserialize(\n" +
      "        @NotNull\n" +
      "        XMLStreamReader deserializeFrom,\n" +
      "        @NotNull\n" +
      "        Version formatVersion)\n" +
      "        throws VersionException, IOException, XMLStreamException\n" +
      "    {\n" +
      "        //width\n" +
      "        double width = Double.parseDouble(deserializeFrom.getAttributeValue(null, \"width\"));\n" +
      "        //height\n" +
      "        int height = Integer.parseInt(deserializeFrom.getAttributeValue(null, \"height\"));\n" +
      "        //description\n" +
      "        String description = getChildText(deserializeFrom, \"description\");\n" +
      "        //anInt\n" +
      "        Integer anInt = Integer.parseInt(getChildText(deserializeFrom, \"anInt\"));\n" +
      "        //floatField\n" +
      "        float floatField = Float.parseFloat(getChildText(deserializeFrom, \"floatField\"));\n" +
      "        closeTag(deserializeFrom);\n" +
      "        //Constructing the deserialized object\n" +
      "        Window object = new Window(description, width, height, anInt);\n" +
      "        object.setFloatField(floatField);\n" +
      "        return object;\n" +
      "    }\n" +
      "\n" +
      "}" );
  }
}
