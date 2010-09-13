package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.file.Extension;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExtensionSerializer extends AbstractJacksonSerializer<Extension> {
  @NonNls
  public static final String PROPERTY_DELIMITER = "delimiter";
  @NonNls
  public static final String PROPERTY_EXTENSION = "extension";

  public ExtensionSerializer() {
    super( "http://cedarsoft.com/file/extension", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull Extension object, @NotNull Version formatVersion )
    throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );
    //delimiter
    serializeTo.writeStringField( PROPERTY_DELIMITER, object.getDelimiter() );
    //extension
    serializeTo.writeStringField( PROPERTY_EXTENSION, object.getExtension() );
  }

  @NotNull
  @Override
  public Extension deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion )
    throws VersionException, IOException, JsonProcessingException {
    //delimiter
    nextField( deserializeFrom, PROPERTY_DELIMITER );
    String delimiter = deserializeFrom.getText();
    //extension
    nextField( deserializeFrom, PROPERTY_EXTENSION );
    String extension = deserializeFrom.getText();
    //Finally closing element
    closeObject( deserializeFrom );
    //Constructing the deserialized object
    Extension object = new Extension( delimiter, extension );
    return object;
  }

}
