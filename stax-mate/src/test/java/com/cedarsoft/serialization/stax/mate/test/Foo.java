package com.cedarsoft.serialization.stax.mate.test;

import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Foo {
  private final String description;
  private final Direction direction;

  public Foo( String description, Direction direction ) {
    this.description = description;
    this.direction = direction;
  }

  public String getDescription() {
    return description;
  }

  public Direction getDirection() {
    return direction;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Foo ) ) return false;

    Foo foo = ( Foo ) o;

    if ( description != null ? !description.equals( foo.description ) : foo.description != null ) return false;
    if ( direction != foo.direction ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = description != null ? description.hashCode() : 0;
    result = 31 * result + ( direction != null ? direction.hashCode() : 0 );
    return result;
  }

  public static class Serializer extends AbstractStaxMateSerializer<Foo> {
    public Serializer() {
      super( "foo", "foo", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
    }

    @Override
    public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Foo object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionWritable( formatVersion );
      serializeTo.addAttribute( "description", object.getDescription() );
      serializeEnum( object.getDirection(), "direction", serializeTo );
    }

    private void serializeEnum( @Nonnull Enum<?> enumValue, @Nonnull String propertyName, @Nonnull SMOutputElement serializeTo ) throws XMLStreamException {
      serializeTo.addAttribute( propertyName, enumValue.name() );
    }

    @Nonnull
    @Override
    public Foo deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      String description = deserializeFrom.getAttributeValue( null, "description" );
      Direction direction = deserializeEnum( Direction.class, "direction", deserializeFrom );
      closeTag( deserializeFrom );
      return new Foo( description, direction );
    }

    @Nonnull
    private <T extends Enum<T>> T deserializeEnum( @Nonnull Class<T> enumType, @Nonnull String propertyName, @Nonnull XMLStreamReader deserializeFrom ) {
      String enumValue = deserializeFrom.getAttributeValue( null, propertyName );
      return Enum.valueOf( enumType, enumValue );
    }
  }
}
