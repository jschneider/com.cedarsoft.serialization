package com.cedarsoft.serialization.stax;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractXmlSerializer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract base class for stax based serializers.
 * <p/>
 * ATTENTION:
 * Serializers based on stax must consume all events for their tag (including END_ELEMENT).<br/>
 * This is especially true for {@link com.cedarsoft.serialization.PluggableSerializer}s.
 *
 * @param <T> the type
 * @param <S> the object to serialize to
 */
public abstract class AbstractStaxBasedSerializer<T, S> extends AbstractXmlSerializer<T, S, XMLStreamReader, XMLStreamException> {
  /**
   * Creates a new serializer
   *
   * @param defaultElementName the name for the root element, if this serializers is not used as delegate. For delegating serializers that value is not used.
   * @param nameSpaceUriBase   the name space uri base
   * @param formatVersionRange the supported format version range. The upper bound represents the format that is written. All Versions within the range can be read.
   */
  protected AbstractStaxBasedSerializer( @NotNull @NonNls String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
  }

  @Override
  @NotNull
  public T deserialize( @NotNull InputStream in ) throws IOException, VersionException {
    try {
      XMLStreamReader reader = StaxSupport.getXmlInputFactory().createXMLStreamReader( in );

      int result = reader.nextTag();
      if ( result != XMLStreamReader.START_ELEMENT ) {
        throw new IllegalStateException( "Expected START_ELEMENT but was <" + result + ">" );
      }

      //Now get the namespace and verify the version
      String namespaceURI = reader.getNamespaceURI();
      if ( namespaceURI == null ) {
        throw new VersionException( "Version information is missing for <" + reader.getName().getLocalPart() + ">" );
      }

      Version version = parseVersionFromNamespaceUri( namespaceURI );
      if ( !getFormatVersionRange().contains( version ) ) {
        throw new VersionMismatchException( getFormatVersion(), version );
      }

      T deserialized = deserialize( reader, version );

      if ( !reader.isEndElement() ) {
        throw new IllegalStateException( "Not consumed everything in <" + getClass().getName() + ">" );
      }

      if ( reader.next() != XMLStreamReader.END_DOCUMENT ) {
        throw new IllegalStateException( "Not consumed everything in <" + getClass().getName() + ">" );
      }

      return deserialized;
    } catch ( XMLStreamException e ) {
      throw new IOException( "Could not parse stream due to " + e.getMessage(), e );
    }
  }

  /**
   * Returns the processing instruction data for the given reader and target
   *
   * @param reader   the reader
   * @param piTarget the target
   * @return the data
   *
   * @throws XMLStreamException
   */
  @NotNull
  @NonNls
  protected String getProcessingInstructionData( @NotNull XMLStreamReader reader, @NotNull @NonNls String piTarget ) throws XMLStreamException {
    int result = reader.next();
    if ( result != XMLStreamReader.PROCESSING_INSTRUCTION ) {
      throw new IllegalArgumentException( "No processing instruction found. Was <" + StaxSupport.getEventName( result ) + ">" );
    }

    String foundTarget = reader.getPITarget();
    if ( !foundTarget.equals( piTarget ) ) {
      throw new IllegalArgumentException( "Invalid processing instruction. Expected <" + piTarget + "> but was <" + foundTarget + ">" );
    }

    return reader.getPIData();
  }

  /**
   * Ensures that the current tag equals the given tag name
   *
   * @param streamReader the stream reader
   * @param tagName      the tag name
   */
  protected void ensureTag( @NotNull XMLStreamReader streamReader, @NotNull @NonNls String tagName ) {
    String current = streamReader.getName().getLocalPart();
    if ( !current.equals( tagName ) ) {
      throw new IllegalStateException( "Invalid tag. Was <" + current + "> but expected <" + tagName + ">" );
    }
  }

  /**
   * Returns the text and closes the tag
   *
   * @param reader the reader
   * @return the text
   *
   * @throws XMLStreamException
   */
  @NotNull
  protected String getText( @NotNull XMLStreamReader reader ) throws XMLStreamException {
    StringBuilder content = new StringBuilder();

    int result;
    while ( ( result = reader.next() ) != XMLStreamReader.END_ELEMENT ) {
      if ( result != XMLStreamReader.CHARACTERS ) {
        throw new IllegalStateException( "Invalid state: " + result );
      }
      content.append( reader.getText() );
    }

    return content.toString();
  }

  /**
   * Returns the child text
   *
   * @param reader  the reader
   * @param tagName the tag name
   * @return the text of the child with the given tag name
   *
   * @throws XMLStreamException
   */
  @NotNull
  protected String getChildText( @NotNull XMLStreamReader reader, @NotNull @NonNls String tagName ) throws XMLStreamException {
    reader.nextTag();
    ensureTag( reader, tagName );
    return getText( reader );
  }

  /**
   * Closes the current tag
   *
   * @param reader the reader
   * @throws XMLStreamException
   */
  protected void closeTag( @NotNull XMLStreamReader reader ) throws XMLStreamException {
    int result = reader.nextTag();
    if ( result != XMLStreamReader.END_ELEMENT ) {
      throw new IllegalStateException( "Invalid result. Expected <END_ELEMENT> but was <" + StaxSupport.getEventName( result ) + ">" );
    }
  }

  /**
   * Opens the next tag
   *
   * @param reader  the reader
   * @param tagName the tag name
   * @throws XMLStreamException
   */
  protected void nextTag( @NotNull XMLStreamReader reader, @NotNull @NonNls String tagName ) throws XMLStreamException {
    int result = reader.nextTag();
    if ( result != XMLStreamReader.START_ELEMENT ) {
      throw new IllegalStateException( "Invalid result. Expected <START_ELEMENT> but was <" + StaxSupport.getEventName( result ) + ">" );
    }
    ensureTag( reader, tagName );
  }

  /**
   * Attention! The current element will be closed!
   *
   * @param streamReader the stream reader
   * @param callback     the callback
   * @throws XMLStreamException
   * @throws IOException
   */
  protected void visitChildren( @NotNull XMLStreamReader streamReader, @NotNull CB callback ) throws XMLStreamException, IOException {
    while ( streamReader.nextTag() != XMLStreamReader.END_ELEMENT ) {
      String tagName = streamReader.getName().getLocalPart();
      callback.tagEntered( streamReader, tagName );
    }
  }

  /**
   * Callback interface used when visiting the children ({@link com.cedarsoft.serialization.stax.AbstractStaxBasedSerializer#visitChildren(XMLStreamReader, com.cedarsoft.serialization.stax.AbstractStaxBasedSerializer.CB)})
   */
  public interface CB {
    /**
     * Is called for each child.
     * ATTENTION: This method *must* close the tag
     *
     * @param deserializeFrom the reader
     * @param tagName         the tag name
     * @throws XMLStreamException
     * @throws IOException
     */
    void tagEntered( @NotNull XMLStreamReader deserializeFrom, @NotNull @NonNls String tagName ) throws XMLStreamException, IOException;
  }
}
