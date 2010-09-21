package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.io.IOException;

/**
 *
 */
public class ColorSerializer extends AbstractJacksonSerializer<Color> {
  @NotNull
  @NonNls
  public static final String PROPERTY_RED = "red";
  @NotNull
  @NonNls
  public static final String PROPERTY_GREEN = "green";
  @NotNull
  @NonNls
  public static final String PROPERTY_BLUE = "blue";

  public ColorSerializer() {
    super( "color", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull Color object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeNumberField( PROPERTY_RED, object.getRed() );
    serializeTo.writeNumberField( PROPERTY_GREEN, object.getGreen() );
    serializeTo.writeNumberField( PROPERTY_BLUE, object.getBlue() );
  }

  @NotNull
  @Override
  public Color deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    assert isVersionReadable( formatVersion );
    //red
    nextFieldValue( deserializeFrom, PROPERTY_RED );
    int red = deserializeFrom.getIntValue();

    nextFieldValue( deserializeFrom, PROPERTY_GREEN );
    int green = deserializeFrom.getIntValue();

    nextFieldValue( deserializeFrom, PROPERTY_BLUE );
    int blue = deserializeFrom.getIntValue();

    closeObject( deserializeFrom );

    return new Color( red, green, blue );
  }
}
