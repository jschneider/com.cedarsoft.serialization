package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxSerializer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserSerializer extends AbstractStaxSerializer<User> {
  public UserSerializer( @NotNull RoleSerializer roleSerializer, @NotNull EmailSerializer emailSerializer ) {
    super( "user", "http://test/user", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
    getDelegatesMappings().add( roleSerializer ).responsibleFor( Role.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( emailSerializer ).responsibleFor( Email.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @NotNull XMLStreamWriter serializeTo, @NotNull User object, @NotNull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    serializeTo.writeStartElement( "name" );
    serializeTo.writeCharacters( object.getName() );
    serializeTo.writeEndElement();

    serializeCollection( object.getEmails(), Email.class, serializeTo, formatVersion );
    serializeCollection( object.getRoles(), Role.class, serializeTo, formatVersion );
  }

  @NotNull
  @Override
  public User deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull final Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    String name = getChildText( deserializeFrom, "name" );

    final List<Email> mails = new ArrayList<Email>();
    final List<Role> roles = new ArrayList<Role>();

    visitChildren( deserializeFrom, new CB() {
      @Override
      public void tagEntered( @NotNull XMLStreamReader deserializeFrom, @NotNull @NonNls String tagName ) throws XMLStreamException, IOException {
        if ( tagName.equals( EmailSerializer.DEFAULT_ELEMENT_NAME ) ) {
          mails.add( deserialize( Email.class, formatVersion, deserializeFrom ) );
        } else if ( tagName.equals( RoleSerializer.DEFAULT_ELEMENT_NAME ) ) {
          roles.add( deserialize( Role.class, formatVersion, deserializeFrom ) );
        } else {
          throw new IllegalArgumentException( "Invalid tag <" + tagName + ">" );
        }
      }
    } );

    //    List<? extends Email> mails = deserializeCollection( deserializeFrom, Email.class, formatVersion );
    //    List<? extends Role> roles = deserializeCollection( deserializeFrom, Role.class, formatVersion );

    return new User( name, mails, roles );
  }
}
