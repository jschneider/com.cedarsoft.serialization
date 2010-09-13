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

public class VersionSerializer extends AbstractJacksonSerializer<Version> {
  @NonNls
  public static final String PROPERTY_MAJOR = "major";
  @NonNls
  public static final String PROPERTY_MINOR = "minor";
  @NonNls
  public static final String PROPERTY_BUILD = "build";
  @NonNls
  public static final String PROPERTY_SUFFIX = "suffix";

  public VersionSerializer() {
    super( "http://cedarsoft.com/version", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull Version object, @NotNull Version formatVersion )
    throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );
    serializeTo.writeStringField( FIELD_NAME_DEFAULT_TEXT, object.format() );
  }

  @Override
  public Version deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion )
    throws VersionException, IOException, JsonProcessingException {
    //major
    nextField( deserializeFrom, FIELD_NAME_DEFAULT_TEXT );
    String version = deserializeFrom.getText();
    closeObject( deserializeFrom );
    return Version.parse( version );
  }

}
