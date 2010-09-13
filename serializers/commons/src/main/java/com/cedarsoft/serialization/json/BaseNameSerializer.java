package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.file.BaseName;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BaseNameSerializer extends AbstractJacksonSerializer<BaseName> {

  public static final String PROPERTY_NAME = "name";

  public BaseNameSerializer() {
    super( "http://cedarsoft.com/file/base-name", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull BaseName object, @NotNull Version formatVersion )
    throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );
    //name
    serializeTo.writeStringField( PROPERTY_NAME, object.getName() );
  }

  @NotNull
  @Override
  public BaseName deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion )
    throws VersionException, IOException, JsonProcessingException {
    //name
    nextField( deserializeFrom, PROPERTY_NAME );
    String name = deserializeFrom.getText();
    //Finally closing element
    closeObject( deserializeFrom );
    //Constructing the deserialized object
    BaseName object = new BaseName( name );
    return object;
  }

}
