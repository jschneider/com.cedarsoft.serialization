package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.license.CreativeCommonsLicense;
import com.cedarsoft.license.License;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

public class LicenseSerializer extends AbstractJacksonSerializer<License> {
  @NonNls
  public static final String PROPERTY_ID = "id";
  @NonNls
  public static final String PROPERTY_NAME = "name";
  @NonNls
  public static final String PROPERTY_URL = "url";
  @NonNls
  public static final String SUB_TYPE_CC = "cc";

  public LicenseSerializer() {
    super( "license", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull License object, @NotNull Version formatVersion ) throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );

    if ( object instanceof CreativeCommonsLicense ) {
      serializeTo.writeStringField( PROPERTY_SUB_TYPE, SUB_TYPE_CC );
    }

    //id
    serializeTo.writeStringField( PROPERTY_ID, object.getId() );
    //name
    serializeTo.writeStringField( PROPERTY_NAME, object.getName() );

    //URL
    serializeTo.writeFieldName( PROPERTY_URL );
    URL url = object.getUrl();
    if ( url == null ) {
      serializeTo.writeNull();
    } else {
      serializeTo.writeString( url.toString() );
    }
  }

  @NotNull
  @Override
  public License deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws VersionException, IOException, JsonProcessingException {
    //If there is a subtype it *must* be cc
    nextToken( deserializeFrom, JsonToken.FIELD_NAME );
    if ( deserializeFrom.getCurrentName().equals( PROPERTY_SUB_TYPE ) ) {
      nextToken( deserializeFrom, JsonToken.VALUE_STRING );
      String subType = deserializeFrom.getText();

      if ( !subType.equals( SUB_TYPE_CC ) ) {
        throw new IllegalStateException( "Invalid sub type: " + subType );
      }
      nextField( deserializeFrom, PROPERTY_ID );
    }

    //id
    assert deserializeFrom.getCurrentName().equals( PROPERTY_ID );
    nextToken( deserializeFrom, JsonToken.VALUE_STRING );
    String id = deserializeFrom.getText();
    //name
    nextFieldValue( deserializeFrom, PROPERTY_NAME );
    String name = deserializeFrom.getText();
    //url
    nextField( deserializeFrom, PROPERTY_URL );
    JsonToken token = deserializeFrom.nextToken();
    @Nullable URL url;
    if ( token == JsonToken.VALUE_NULL ) {
      url = null;
    } else {
      url = new URL( deserializeFrom.getText() );
    }
    //Finally closing element
    closeObject( deserializeFrom );

    //Constructing the deserialized object
    try {
      return License.get( id );
    } catch ( IllegalArgumentException ignore ) {
      return new License( id, name, url );
    }
  }
}
