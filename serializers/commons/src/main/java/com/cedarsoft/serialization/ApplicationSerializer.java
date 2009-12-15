package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.app.Application;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.google.inject.Inject;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class ApplicationSerializer extends AbstractStaxMateSerializer<Application> {
  @NotNull
  private static final Version VERSION_VERSION_SERIALIZER = new Version( 1, 0, 0 );
  @NotNull
  @NonNls
  private static final String ELEMENT_VERSION = "version";

  @NotNull
  @NonNls
  private static final String ELEMENT_NAME = "name";
  @NotNull
  private final VersionSerializer versionSerializer;

  @Inject
  public ApplicationSerializer( @NotNull VersionSerializer versionSerializer ) {
    super( "application", new VersionRange( VERSION_VERSION_SERIALIZER, VERSION_VERSION_SERIALIZER ) );
    this.versionSerializer = versionSerializer;

    verifyDelegatingSerializerVersion( versionSerializer, VERSION_VERSION_SERIALIZER );
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Application object ) throws IOException, XMLStreamException {
    serializeTo.addElement( ELEMENT_NAME ).addCharacters( object.getName() );

    SMOutputElement versionElement = serializeTo.addElement( ELEMENT_VERSION );
    versionSerializer.serialize( versionElement, object.getVersion() );


  }

  @Override
  @NotNull
  public Application deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
    String name = getChildText( deserializeFrom, ELEMENT_NAME );

    nextTag( deserializeFrom, ELEMENT_VERSION );
    Version applicationVersion = versionSerializer.deserialize( deserializeFrom, VERSION_VERSION_SERIALIZER );
    closeTag( deserializeFrom );

    return new Application( name, applicationVersion );
  }
}
