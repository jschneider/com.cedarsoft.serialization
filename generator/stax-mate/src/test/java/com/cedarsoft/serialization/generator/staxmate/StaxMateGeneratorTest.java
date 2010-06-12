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

package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.decision.DefaultXmlDecisionCallback;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptorFactory;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.decorators.I18nAnnotationsDecorator;
import com.cedarsoft.serialization.generator.output.decorators.NotNullDecorator;
import com.cedarsoft.serialization.generator.parsing.Parser;
import com.cedarsoft.serialization.generator.parsing.Result;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
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
    codeGenerator.addMethodDecorator( new NotNullDecorator( NotNull.class ) );
    codeGenerator.addMethodDecorator( new I18nAnnotationsDecorator( NonNls.class ) );
    generator = new StaxMateGenerator( codeGenerator );
    model = generator.getCodeGenerator().getModel();
  }

  @Test
  public void testSerializer() throws IOException, JClassAlreadyExistsException {
    generator.generateSerializer( domainObjectDescriptor );

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
      "import org.jetbrains.annotations.NonNls;\n" +
      "import org.jetbrains.annotations.NotNull;\n" +
      "\n" +
      "public class WindowSerializer\n" +
      "    extends AbstractStaxMateSerializer<Window>\n" +
      "{\n" +
      "\n" +
      "    @NotNull\n" +
      "    @NonNls\n" +
      "    public final static String ATTRIBUTE_WIDTH = \"width\";\n" +
      "    @NotNull\n" +
      "    @NonNls\n" +
      "    public final static String ATTRIBUTE_HEIGHT = \"height\";\n" +
      "    @NotNull\n" +
      "    @NonNls\n" +
      "    public final static String ELEMENT_DESCRIPTION = \"description\";\n" +
      "    @NotNull\n" +
      "    @NonNls\n" +
      "    public final static String ELEMENT_ANINT = \"anInt\";\n" +
      "    @NotNull\n" +
      "    @NonNls\n" +
      "    public final static String ELEMENT_FLOATFIELD = \"floatField\";\n" +
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
      "        serializeTo.addAttribute(ATTRIBUTE_WIDTH, String.valueOf(object.getWidth()));\n" +
      "        //height\n" +
      "        serializeTo.addAttribute(ATTRIBUTE_HEIGHT, String.valueOf(object.getHeight()));\n" +
      "        //description\n" +
      "        serializeTo.addElementWithCharacters(serializeTo.getNamespace(), ELEMENT_DESCRIPTION, object.getDescription());\n" +
      "        //anInt\n" +
      "        serializeTo.addElementWithCharacters(serializeTo.getNamespace(), ELEMENT_ANINT, String.valueOf(object.getAnInt()));\n" +
      "        //floatField\n" +
      "        serializeTo.addElementWithCharacters(serializeTo.getNamespace(), ELEMENT_FLOATFIELD, String.valueOf(object.getFloatField()));\n" +
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
      "        double width = Double.parseDouble(deserializeFrom.getAttributeValue(null, ATTRIBUTE_WIDTH));\n" +
      "        //height\n" +
      "        int height = Integer.parseInt(deserializeFrom.getAttributeValue(null, ATTRIBUTE_HEIGHT));\n" +
      "        //description\n" +
      "        String description = getChildText(deserializeFrom, ELEMENT_DESCRIPTION);\n" +
      "        //anInt\n" +
      "        Integer anInt = Integer.parseInt(getChildText(deserializeFrom, ELEMENT_ANINT));\n" +
      "        //floatField\n" +
      "        float floatField = Float.parseFloat(getChildText(deserializeFrom, ELEMENT_FLOATFIELD));\n" +
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
