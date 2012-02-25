package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;

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

  public static class Serializer extends AbstractJacksonSerializer<Foo> {
    public Serializer() {
      super( "foo", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
    }

    @Override
    public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull Foo object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
      assert isVersionWritable( formatVersion );
      serializeTo.writeStringField( "description", object.getDescription() );
      serializeEnum( object.getDirection(), "direction", serializeTo );
    }

    @Nonnull
    @Override
    public Foo deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
      assert isVersionReadable( formatVersion );

      JacksonParserWrapper parser = new JacksonParserWrapper( deserializeFrom );

      parser.nextFieldValue( "description" );
      String description = parser.getText();
      Direction direction = deserializeEnum( Direction.class, "direction", parser );
      parser.closeObject();

      return new Foo( description, direction );
    }

  }
}
