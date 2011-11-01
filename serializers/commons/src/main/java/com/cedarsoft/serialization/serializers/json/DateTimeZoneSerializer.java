package com.cedarsoft.serialization.serializers.json;

import java.io.IOException;

import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.cedarsoft.serialization.jackson.test.compatible.JacksonParserWrapper;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.joda.time.DateTimeZone;

import javax.annotation.Nonnull;

public class DateTimeZoneSerializer extends AbstractJacksonSerializer<DateTimeZone> {

  public static final String ID = "id";

  public DateTimeZoneSerializer() {
    super( "dateTimeZone", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public boolean isObjectType() {
    return false;
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull DateTimeZone object, @Nonnull Version formatVersion ) throws IOException, JsonProcessingException {
    verifyVersionWritable( formatVersion );

    serializeTo.writeString( object.getID() );
  }

  @Nonnull
  @Override
  public DateTimeZone deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws VersionException, IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );

    String id = deserializeFrom.getText();
    //Constructing the deserialized object
    return DateTimeZone.forID( id );
  }

}
