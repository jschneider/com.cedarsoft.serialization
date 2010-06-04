package com.cedarsoft.serialization.bench;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.serialization.bench.jaxb.Extension;
import com.cedarsoft.serialization.bench.jaxb.FileType;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringWriter;

import static org.testng.Assert.*;

/**
 *
 */
public class JibxTest {
  @Test
  public void testIt() throws JiBXException, IOException, SAXException {
    IBindingFactory bindingFactory = BindingDirectory.getFactory( Extension.class );
    assertNotNull( bindingFactory );

    IMarshallingContext context = bindingFactory.createMarshallingContext();


    FileType type = new FileType( "jpg", new Extension( ".", "jpg", true ), false );

    StringWriter out = new StringWriter();
    context.marshalDocument( type, "UTF-8", null, out );

    AssertUtils.assertXMLEqual( out.toString(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<fileType xmlns=\"http://cedarsoft.com/serialization/bench/jaxb\" dependent=\"false\">\n" +
      " <id>jpg</id>\n" +
      " <extension isDefault=\"true\">\n" +
      "  <delimiter>.</delimiter>\n" +
      "  <extension>jpg</extension>\n" +
      " </extension>\n" +
      "</fileType>" );
  }
}
