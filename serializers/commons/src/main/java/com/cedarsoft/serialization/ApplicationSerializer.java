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
  @NonNls
  private static final String ELEMENT_VERSION = "version";

  @NotNull
  @NonNls
  private static final String ELEMENT_NAME = "name";

  @Inject
  public ApplicationSerializer( @NotNull VersionSerializer versionSerializer ) {
    super( "application", "http://www.cedarsoft.com/app/appliaction", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );

    add( versionSerializer ).responsibleFor( Version.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );

    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Application object ) throws IOException, XMLStreamException {
    serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_NAME ).addCharacters( object.getName() );

    SMOutputElement versionElement = serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_VERSION );
    serialize( object.getVersion(), Version.class, versionElement );
  }

  @Override
  @NotNull
  public Application deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
    String name = getChildText( deserializeFrom, ELEMENT_NAME );

    nextTag( deserializeFrom, ELEMENT_VERSION );
    Version applicationVersion = deserialize( Version.class, formatVersion, deserializeFrom );
    closeTag( deserializeFrom );

    return new Application( name, applicationVersion );
  }
}
