package com.cedarsoft.serialization;

import com.cedarsoft.AssertUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Abstract base class for XML based serializers.
 *
 * @param <T> the type of the serialized object
 */
public abstract class AbstractXmlSerializerTest<T> extends AbstractSerializerTest<T> {
  @Override
  protected void verifySerialized( @NotNull byte[] serialized ) throws Exception {
    String expectedWithNamespace = addNameSpace( getExpectedSerialized(), ( AbstractXmlSerializer<?, ?, ?, ?> ) getSerializer() );
    AssertUtils.assertXMLEqual( new String( serialized ), expectedWithNamespace );
  }

  @NotNull
  @NonNls
  public static String addNameSpace( @NotNull @NonNls String xml, @NotNull AbstractXmlSerializer<?, ?, ?, ?> serializer ) throws Exception {
    return addNameSpace( xml, serializer.createNameSpaceUri( serializer.getFormatVersion() ) );
  }

  public static String addNameSpace( @NotNull @NonNls String xml, @NotNull @NonNls String nameSpaceUri ) throws JDOMException, IOException {
    Document doc = new SAXBuilder().build( new ByteArrayInputStream( xml.getBytes() ) );

    Element root = doc.getRootElement();
    if ( root.getNamespaceURI().length() == 0 ) {
      Namespace namespace = Namespace.getNamespace( nameSpaceUri );

      addNameSpaceRecursively( root, namespace );
    }

    return new XMLOutputter( Format.getPrettyFormat() ).outputString( doc );
  }

  public static void addNameSpaceRecursively( @NotNull Element element, @NotNull Namespace namespace ) {
    element.setNamespace( namespace );
    for ( Element child : ( ( Iterable<? extends Element> ) element.getChildren() ) ) {
      addNameSpaceRecursively( child, namespace );
    }
  }

  /**
   * Returns the expected serialized string
   *
   * @return the expected serialized string
   */
  @NotNull
  @NonNls
  protected abstract String getExpectedSerialized();
}
