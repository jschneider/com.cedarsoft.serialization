package com.cedarsoft.serialization.jdom;

import com.cedarsoft.AssertUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.testng.annotations.*;

/**
 *
 */
public class JDomTest {
  @Test
  public void testNameSpace() throws Exception {
    Document doc = new Document();

    Namespace namespace = Namespace.getNamespace( "theNamespace" );
    doc.addContent( new Element( "root", namespace ).addContent( new Element( "child", namespace ).setText( "the Text" ) ) );
    AssertUtils.assertXMLEqual( new XMLOutputter( Format.getPrettyFormat() ).outputString( doc ), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<root xmlns=\"theNamespace\">\n" +
      "  <child>the Text</child>\n" +
      "</root>" );
  }
}
