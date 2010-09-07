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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserSerializer extends AbstractJacksonSerializer<User> {
  @NonNls
  public static final String PROPERTY_NAME = "id";
  @NonNls
  public static final String PROPERTY_EMAILS = "emails";
  @NonNls
  public static final String PROPERTY_ROLES = "roles";

  public UserSerializer( @NotNull EmailSerializer emailSerializer, @NotNull RoleSerializer roleSerializer ) {
    super( "http://cedarsoft.com/test/user", VersionRange.from( 1, 0, 0 ).to() );

    getDelegatesMappings().add( emailSerializer ).responsibleFor( Email.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( roleSerializer ).responsibleFor( Role.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull User object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    serializeTo.writeStringField( PROPERTY_NAME, object.getName() );

    serializeTo.writeArrayFieldStart( PROPERTY_EMAILS );
    for ( Email email : object.getEmails() ) {
      serializeTo.writeStartObject();
      serialize( email, Email.class, serializeTo, formatVersion );
      serializeTo.writeEndObject();
    }
    serializeTo.writeEndArray();

    serializeTo.writeArrayFieldStart( PROPERTY_ROLES );
    for ( Role role : object.getRoles() ) {
      serializeTo.writeStartObject();
      serialize( role, Role.class, serializeTo, formatVersion );
      serializeTo.writeEndObject();
    }
    serializeTo.writeEndArray();
  }

  @NotNull
  @Override
  public User deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    nextField( deserializeFrom, PROPERTY_NAME );
    String name = deserializeFrom.getText();

    List<Email> mails = new ArrayList<Email>();
    nextField( deserializeFrom, PROPERTY_EMAILS );
    nextToken( deserializeFrom, JsonToken.START_OBJECT );
    mails.add( deserialize( Email.class, formatVersion, deserializeFrom ) );
    nextToken( deserializeFrom, JsonToken.START_OBJECT );
    mails.add( deserialize( Email.class, formatVersion, deserializeFrom ) );

    nextToken( deserializeFrom, JsonToken.END_ARRAY );

    List<Role> roles = new ArrayList<Role>();
    nextField( deserializeFrom, PROPERTY_ROLES );
    nextToken( deserializeFrom, JsonToken.START_OBJECT );
    roles.add( deserialize( Role.class, formatVersion, deserializeFrom ) );
    nextToken( deserializeFrom, JsonToken.START_OBJECT );
    roles.add( deserialize( Role.class, formatVersion, deserializeFrom ) );

    nextToken( deserializeFrom, JsonToken.END_ARRAY );

    nextToken( deserializeFrom, JsonToken.END_OBJECT );
    return new User( name, mails, roles );
  }
}
