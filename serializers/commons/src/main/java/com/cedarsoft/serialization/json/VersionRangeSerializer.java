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

import java.io.IOException;

public class VersionRangeSerializer extends AbstractJacksonSerializer<VersionRange> {
  @NonNls
  public static final String PROPERTY_MIN = "min";
  @NonNls
  public static final String PROPERTY_MAX = "max";
  @NonNls
  public static final String PROPERTY_INCLUDELOWER = "includeLower";
  @NonNls
  public static final String PROPERTY_INCLUDEUPPER = "includeUpper";

  public VersionRangeSerializer() {
    super( "http://cedarsoft.com/version-range", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull VersionRange object, @NotNull Version formatVersion )
    throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );

    serializeTo.writeStringField( PROPERTY_MIN, object.getMin().format() );
    serializeTo.writeStringField( PROPERTY_MAX, object.getMax().format() );

    //includeLower
    serializeTo.writeBooleanField( PROPERTY_INCLUDELOWER, object.isIncludeLower() );
    //includeUpper
    serializeTo.writeBooleanField( PROPERTY_INCLUDEUPPER, object.isIncludeUpper() );
  }

  @NotNull
  @Override
  public VersionRange deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion )
    throws VersionException, IOException, JsonProcessingException {
    //min
    nextField( deserializeFrom, PROPERTY_MIN );
    Version min = Version.parse( deserializeFrom.getText() );
    //max
    nextField( deserializeFrom, PROPERTY_MAX );
    Version max = Version.parse( deserializeFrom.getText() );
    //includeLower
    nextField( deserializeFrom, PROPERTY_INCLUDELOWER );
    boolean includeLower = deserializeFrom.getBooleanValue();
    //includeUpper
    nextField( deserializeFrom, PROPERTY_INCLUDEUPPER );
    boolean includeUpper = deserializeFrom.getBooleanValue();
    //Finally closing element
    closeObject( deserializeFrom );
    //Constructing the deserialized object
    return new VersionRange( min, max, includeLower, includeUpper );
  }

}
