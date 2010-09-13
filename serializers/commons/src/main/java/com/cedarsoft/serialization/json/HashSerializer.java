package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class HashSerializer extends AbstractJacksonSerializer<Hash> {

  @NonNls
  public static final String PROPERTY_ALGORITHM = "algorithm";
  @NonNls
  public static final String PROPERTY_VALUE = "hex";

  public HashSerializer() {
    super( "http://cedarsoft.com/crypt/hash", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull Hash object, @NotNull Version formatVersion ) throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );

    serializeTo.writeStringField( PROPERTY_ALGORITHM, object.getAlgorithm().name() );
    serializeTo.writeStringField( PROPERTY_VALUE, object.getValueAsHex() );
  }

  @NotNull
  @Override
  public Hash deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws VersionException, IOException, JsonProcessingException {
    nextField( deserializeFrom, PROPERTY_ALGORITHM );
    Algorithm algorithm = Algorithm.getAlgorithm( deserializeFrom.getText() );

    nextField( deserializeFrom, PROPERTY_VALUE );
    String hex = deserializeFrom.getText();

    closeObject( deserializeFrom );

    return Hash.fromHex( algorithm, hex );
  }
}
