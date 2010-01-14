package com.cedarsoft.file;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.AbstractSerializer;
import com.cedarsoft.serialization.ExtensionSerializer;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.google.inject.Inject;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FileTypeSerializer extends AbstractStaxMateSerializer<FileType> {
  @NotNull
  private static final Version EXTENSION_FORMAT_VERSION = new Version( 1, 0, 0 );
  @NotNull
  @NonNls
  private static final String ATTRIBUTE_DEPENDENT = "dependent";
  @NotNull
  @NonNls
  private static final String ELEMENT_ID = "id";
  @NotNull
  @NonNls
  private static final String ELEMENT_EXTENSION = "extension";

  @NotNull
  @NonNls
  private static final String ATTRIBUTE_DEFAULT = "default";
  @NotNull
  private final ExtensionSerializer extensionSerializer;

  @Inject
  public FileTypeSerializer( @NotNull ExtensionSerializer extensionSerializer ) {
    super( "fileType", "http://collustra.cedarsoft.com/fileType", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
    this.extensionSerializer = extensionSerializer;
    AbstractSerializer.verifyDelegatingSerializerVersion( extensionSerializer, EXTENSION_FORMAT_VERSION );
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull FileType object ) throws IOException, XMLStreamException {
    serializeTo.addAttribute( ATTRIBUTE_DEPENDENT, String.valueOf( object.isDependentType() ) );
    serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_ID ).addCharacters( object.getId() );

    for ( Extension extension : object.getExtensions() ) {
      SMOutputElement extensionElement = serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_EXTENSION );

      if ( object.isDefaultExtension( extension ) ) {
        extensionElement.addAttribute( ATTRIBUTE_DEFAULT, String.valueOf( true ) );
      }

      extensionSerializer.serialize( extensionElement, extension );
    }
  }

  @NotNull
  @Override
  public FileType deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
    boolean dependent = Boolean.parseBoolean( deserializeFrom.getAttributeValue( null, ATTRIBUTE_DEPENDENT ) );
    String id = getChildText( deserializeFrom, ELEMENT_ID );

    final List<Extension> extensions = new ArrayList<Extension>();

    visitChildren( deserializeFrom, new CB() {
      @Override
      public void tagEntered( @NotNull XMLStreamReader deserializeFrom, @NotNull String tagName ) throws XMLStreamException, IOException {
        extensions.add( extensionSerializer.deserialize( deserializeFrom, EXTENSION_FORMAT_VERSION ) );
      }
    } );

    return new FileType( id, dependent, extensions );
  }
}
