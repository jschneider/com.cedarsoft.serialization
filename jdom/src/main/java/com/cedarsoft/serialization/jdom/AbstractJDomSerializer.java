package com.cedarsoft.serialization.jdom;

import com.cedarsoft.Version;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractSerializer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Parent;
import org.jdom.ProcessingInstruction;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Abstract serializer based on JDom
 *
 * @param <T> the type
 */
public abstract class AbstractJDomSerializer<T> extends AbstractSerializer<T, Element, Element, IOException> {
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
    //Add the format version
    document.addContent( new ProcessingInstruction( PI_TARGET_FORMAT, getFormatVersion().toString() ) );

    //Create the root
    Element root = new Element( getDefaultElementName() );
    document.setRootElement( root );

    serialize( root, object );
    new XMLOutputter( Format.getPrettyFormat().setLineSeparator( LINE_SEPARATOR ) ).output( document, out );
  }

  @Override
  @NotNull
  public T deserialize( @NotNull InputStream in ) throws IOException, VersionMismatchException {
    try {
      Document document = new SAXBuilder().build( in );

      ProcessingInstruction processingInstruction = getFormatInstruction( document );

      Version formatVersion = parseVersion( processingInstruction );

      Version.verifyMatch( getFormatVersion(), formatVersion );

      return deserialize( document.getRootElement() );
    } catch ( JDOMException e ) {
      throw new IOException( "Could not parse stream due to " + e.getMessage(), e );
    }
  }

  @NotNull
  private static ProcessingInstruction getFormatInstruction( @NotNull Parent document ) {
    List<? extends ProcessingInstruction> processingInstructions = document.getContent( new PiFormatFilter() );
    if ( processingInstructions.size() != 1 ) {
      throw new IllegalStateException( "No processing instructions found" );
    }
    return processingInstructions.get( 0 );
  }

  @NotNull
  private static Version parseVersion( @NotNull ProcessingInstruction processingInstruction ) {
    if ( !processingInstruction.getTarget().equals( PI_TARGET_FORMAT ) ) {
      throw new IllegalStateException( "Invalid target: <" + processingInstruction.getTarget() + "> but expected <" + PI_TARGET_FORMAT + ">" );
    }

    String data = processingInstruction.getData();
    return Version.parse( data );
  }

  /**
   * Filter that recognizes processing instructions containing the format version number
   */
  private static class PiFormatFilter implements Filter {
    @Override
    public boolean matches( Object obj ) {
      return obj instanceof ProcessingInstruction && ( ( ProcessingInstruction ) obj ).getTarget().equals( PI_TARGET_FORMAT );
    }
  }
}
