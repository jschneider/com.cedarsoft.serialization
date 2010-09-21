package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.app.Application;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ApplicationSerializer extends AbstractJacksonSerializer<Application> {
  @NonNls
  public static final String PROPERTY_NAME = "name";
  @NonNls
  public static final String PROPERTY_VERSION = "version";

  public ApplicationSerializer( @NotNull VersionSerializer versionSerializer ) {
    super( "application", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
    add( versionSerializer ).responsibleFor( Version.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull Application object, @NotNull Version formatVersion ) throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );
    //name
    serializeTo.writeStringField( PROPERTY_NAME, object.getName() );
    //version
    serialize( object.getVersion(), Version.class, PROPERTY_VERSION, serializeTo, formatVersion );
  }

  @NotNull
  @Override
  public Application deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws VersionException, IOException, JsonProcessingException {
    //name
    nextFieldValue( deserializeFrom, PROPERTY_NAME );
    String name = deserializeFrom.getText();
    //version
    Version version = deserialize( Version.class, PROPERTY_VERSION, formatVersion, deserializeFrom );
    //Finally closing element
    closeObject( deserializeFrom );
    //Constructing the deserialized object
    return new Application( name, version );
  }

}
