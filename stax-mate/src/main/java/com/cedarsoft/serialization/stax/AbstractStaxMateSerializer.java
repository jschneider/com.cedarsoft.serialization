package com.cedarsoft.serialization.stax;

import com.cedarsoft.VersionRange;
import org.codehaus.staxmate.out.SMNamespace;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract base class for stax mate based serializers
 *
 * @param <T> the type
 */
public abstract class AbstractStaxMateSerializer<T> extends AbstractStaxBasedSerializer<T, SMOutputElement> {
  protected AbstractStaxMateSerializer( @NotNull @NonNls String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
  }

  @Override
  public void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException {
    try {
      SMOutputDocument doc = StaxMateSupport.getSmOutputFactory().createOutputDocument( out );

      String nameSpaceUri = createNameSpaceUri( getFormatVersion() );
      SMNamespace nameSpace = doc.getNamespace( nameSpaceUri );

      SMOutputElement root = doc.addElement( nameSpace, getDefaultElementName() );
      serialize( root, object );
      doc.closeRoot();
    } catch ( XMLStreamException e ) {
      throw new IOException( e );
    }
  }

  /**
   * Serializes the elements of a collection
   *
   * @param type        the type
   * @param objects     the objects that are serialized
   * @param elementName the element name
   * @param serializeTo the object the elements are serialized to
   * @param <T>         the type
   * @throws XMLStreamException
   * @throws IOException
   */
  protected <T> void serializeCollection( @NotNull Class<T> type, @NotNull Iterable<? extends T> objects, @NotNull @NonNls String elementName, @NotNull SMOutputElement serializeTo ) throws XMLStreamException, IOException {
    for ( T object : objects ) {
      SMOutputElement doorElement = serializeTo.addElement( serializeTo.getNamespace(), elementName );
      getSerializer( type ).serialize( doorElement, object );
    }
  }

  /**
   * Serializes the elements of the collection to a own sub element
   *
   * @param objects               the objects that are serialized
   * @param type                  the type
   * @param collectionElementName the collection element name
   * @param elementName           the element name
   * @param serializeTo           the object the elements are serialized to
   * @throws XMLStreamException
   * @throws IOException
   */
  protected <T> void serializeCollectionToElement( @NotNull Iterable<? extends T> objects, @NotNull Class<T> type, @NotNull @NonNls String collectionElementName, @NotNull @NonNls String elementName, @NotNull SMOutputElement serializeTo ) throws XMLStreamException, IOException {
    SMOutputElement collectionElement = serializeTo.addElement( serializeTo.getNamespace(), collectionElementName );
    serializeCollection( type, objects, elementName, collectionElement );
  }

  protected void serializeToElementWithCharacters( @NotNull @NonNls String elementName, @NotNull String characters, @NotNull SMOutputElement serializeTo ) throws XMLStreamException {
    serializeTo.addElementWithCharacters( serializeTo.getNamespace(), elementName, characters );
  }
}
