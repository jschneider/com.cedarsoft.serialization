package com.cedarsoft.serialization.jdom;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstract serializer based on JDom
 *
 * @param <T> the type
 */
public abstract class AbstractJDomSerializer<T> extends AbstractXmlSerializer<T, Element, Element, IOException> {
  @NotNull
  @NonNls
  protected static final String LINE_SEPARATOR = "\n";

  protected AbstractJDomSerializer( @NotNull @NonNls String defaultElementName, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, formatVersionRange );
  }

  @NotNull
  public Element serializeToElement( @NotNull T object ) throws IOException {
    Element element = new Element( getDefaultElementName() );
    serialize( element, object );
    return element;
  }

  @Override
  public void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException {
    Document document = new Document();

    //The name space
    Namespace namespace = Namespace.getNamespace( createNameSpaceUri( getFormatVersion() ) );

    //Create the root
    Element root = new Element( getDefaultElementName(), namespace );
    document.setRootElement( root );

    serialize( root, object );
    new XMLOutputter( Format.getPrettyFormat().setLineSeparator( LINE_SEPARATOR ) ).output( document, out );
  }

  @Override
  @NotNull
  public T deserialize( @NotNull InputStream in ) throws IOException, VersionException {
    try {
      Document document = new SAXBuilder().build( in );

      String namespaceURI = document.getRootElement().getNamespaceURI();
      Version formatVersion = parseVersionFromNamespaceUri( namespaceURI );

      Version.verifyMatch( getFormatVersion(), formatVersion );

      return deserialize( document.getRootElement(), formatVersion );
    } catch ( JDOMException e ) {
      throw new IOException( "Could not parse stream due to " + e.getMessage(), e );
    }
  }
}
