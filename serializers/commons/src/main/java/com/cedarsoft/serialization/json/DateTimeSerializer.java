package com.cedarsoft.serialization.json;

import com.cedarsoft.UnsupportedVersionException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

/**
 *
 */
public class DateTimeSerializer extends AbstractJacksonSerializer<DateTime> {
  public DateTimeSerializer() {
    super( "dateTime", new VersionRange( new Version( 0, 9, 0 ), new Version( 1, 0, 0 ) ) );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull DateTime object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeString( createFormatter().print( object ) );
  }

  @NotNull
  @Override
  public DateTime deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    assert isVersionReadable( formatVersion );
    String text = deserializeFrom.getText();

    if ( formatVersion.equals( Version.valueOf( 0, 9, 0 ) ) ) {
      return new DateTime( Long.parseLong( text ) );
    }

    if ( formatVersion.equals( Version.valueOf( 1, 0, 0 ) ) ) {
      return createFormatter().withOffsetParsed().parseDateTime( text );
    }

    throw new UnsupportedVersionException( formatVersion, getFormatVersionRange() );
  }

  @Override
  public boolean isObjectType() {
    return false;
  }

  @NotNull
  static DateTimeFormatter createFormatter() {
    return ISODateTimeFormat.basicDateTime();
  }
}
