package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class RoleSerializer extends AbstractJacksonSerializer<Role> {
  @NonNls
  public static final String PROPERTY_ID = "id";
  @NonNls
  public static final String PROPERTY_DESCRIPTION = "description";

  public RoleSerializer() {
    super( "http://cedarsoft.com/test/role", VersionRange.from( 1, 0, 0 ).to() );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull Role object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeNumberField( PROPERTY_ID, object.getId() );
    serializeTo.writeStringField( PROPERTY_DESCRIPTION, object.getDescription() );
  }

  @NotNull
  @Override
  public Role deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    nextField( deserializeFrom, PROPERTY_ID );
    int id = deserializeFrom.getIntValue();

    nextField( deserializeFrom, PROPERTY_DESCRIPTION );
    String description = deserializeFrom.getText();

    nextToken( deserializeFrom, JsonToken.END_OBJECT );
    return new Role( id, description );
  }
}
