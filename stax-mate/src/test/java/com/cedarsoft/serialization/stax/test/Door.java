package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class Door {
  private final String description;

  public Door( String description ) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Door ) ) return false;

    Door door = ( Door ) o;

    if ( description != null ? !description.equals( door.description ) : door.description != null ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return description != null ? description.hashCode() : 0;
  }

  public static class Serializer extends AbstractStaxMateSerializer<Door> {
    public Serializer() {
      super( "door", "door", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Door object ) throws IOException, XMLStreamException {
      serializeTo.addElementWithCharacters( serializeTo.getNamespace(), "description", object.getDescription() );
    }

    @NotNull
    @Override
    public Door deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      String description = getChildText( deserializeFrom, "description" );
      closeTag( deserializeFrom );
      return new Door( description );
    }
  }
}
