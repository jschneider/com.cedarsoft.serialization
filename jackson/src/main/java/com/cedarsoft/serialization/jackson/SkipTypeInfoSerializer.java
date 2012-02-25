package com.cedarsoft.serialization.jackson;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SkipTypeInfoSerializer<T> extends AbstractJacksonSerializer<T> {
  @Nonnull
  private final AbstractJacksonSerializer<T> delegate;

  public SkipTypeInfoSerializer( @Nonnull AbstractJacksonSerializer<T> delegate ) {
    super( delegate.getType(), delegate.getFormatVersionRange() );
    if ( !delegate.isObjectType() ) {
      throw new IllegalStateException( "Not supported for object type serializer: " + delegate.getClass().getName() );
    }

    this.delegate = delegate;
  }

  @Override
  public void serialize( @Nonnull JsonGenerator serializeTo, @Nonnull T object, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    delegate.serialize( serializeTo, object, formatVersion );
  }

  @Nonnull
  @Override
  public T deserialize( @Nonnull JsonParser deserializeFrom, @Nonnull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    return delegate.deserialize( deserializeFrom, formatVersion );
  }


  /**
   * @noinspection RefusedBequest
   */
  @Override
  protected void writeTypeAndVersion( @Nonnull JsonGenerator generator ) throws IOException {
    //Do *not* write type and version!
  }

  /** @noinspection RefusedBequest*/
  @Nonnull
  @Override
  protected Version prepareDeserialization( @Nonnull JacksonParserWrapper wrapper, @Nullable Version formatVersionOverride ) throws IOException, InvalidTypeException {
    //We do *not* read the type information and version here!
    wrapper.nextToken( JsonToken.START_OBJECT );
    return getFormatVersion();
  }
}
