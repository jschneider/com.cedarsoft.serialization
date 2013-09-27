package com.cedarsoft.serialization.neo4j;

import com.cedarsoft.serialization.AbstractSerializer;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import org.neo4j.graphdb.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractNeo4jSerializer<T> extends AbstractSerializer<T, Node, Node, IOException, Node, Node> implements Serializer<T, Node, Node> {
  @Nonnull
  public static final String PROPERTY_FORMAT_VERSION = "formatVersion";
  public static final String PROPERTY_TYPE = "type";

  @Nonnull
  private final String type; //$NON-NLS-1$

  protected AbstractNeo4jSerializer( @Nonnull String type, @Nonnull VersionRange formatVersionRange ) {
    super( formatVersionRange );
    this.type = type;
  }

  @Override
  public void serialize( @Nonnull T object, @Nonnull Node out ) throws IOException {
    out.setProperty( PROPERTY_TYPE, type );
    out.setProperty( PROPERTY_FORMAT_VERSION, getFormatVersion().toString() );
    serialize( out, object, getFormatVersion() );
  }

  @Nonnull
  @Override
  public T deserialize( @Nonnull Node in ) throws IOException, VersionException {
    String readType = ( String ) in.getProperty( PROPERTY_TYPE );

    try {
      verifyType( readType );
    } catch ( InvalidTypeException e ) {
      throw new IOException( "Could not parse due to " + e.getMessage(), e );
    }

    Version version = Version.parse( ( String ) in.getProperty( PROPERTY_FORMAT_VERSION ) );
    verifyVersionReadable( version );

    return deserialize( in, version );
  }

  protected void verifyType( @Nonnull String readType ) throws InvalidTypeException {
    if ( !this.type.equals( readType ) ) {//$NON-NLS-1$
      throw new InvalidTypeException( readType, this.type );
    }
  }

  public static class InvalidTypeException extends Exception {
    @Nullable
    private final String type;
    @Nonnull
    private final String expected;

    public InvalidTypeException( @Nullable String type, @Nonnull String expected ) {
      super( "Invalid type. Was <" + type + "> but expected <" + expected + ">" );
      this.type = type;
      this.expected = expected;
    }

    @Nullable
    public String getType() {
      return type;
    }

    @Nonnull
    public String getExpected() {
      return expected;
    }
  }
}
