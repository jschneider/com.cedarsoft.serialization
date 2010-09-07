package com.cedarsoft.serialization.jackson;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.SerializingStrategy;
import com.cedarsoft.serialization.SerializingStrategySupport;
import com.cedarsoft.serialization.VersionMapping;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 * @param <T> the type
 */
public abstract class AbstractDelegatingJacksonSerializer<T> extends AbstractJacksonSerializer<T> {
  @NotNull
  @NonNls
  private static final String PROPERTY_TYPE = "@type";
  @NotNull
  protected final SerializingStrategySupport<T, JsonGenerator, JsonParser, JsonProcessingException> serializingStrategySupport;

  protected AbstractDelegatingJacksonSerializer( @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( nameSpaceUriBase, formatVersionRange );
    this.serializingStrategySupport = new SerializingStrategySupport<T, JsonGenerator, JsonParser, JsonProcessingException>( formatVersionRange );
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull T object, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    assert isVersionWritable( formatVersion );

    SerializingStrategy<T, JsonGenerator, JsonParser, JsonProcessingException> strategy = serializingStrategySupport.findStrategy( object );
    Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
    serializeTo.writeStringField( PROPERTY_TYPE, strategy.getId() );

    strategy.serialize( serializeTo, object, resolvedVersion );
  }

  @NotNull
  @Override
  public T deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, JsonProcessingException {
    assert isVersionReadable( formatVersion );

    nextField( deserializeFrom, PROPERTY_TYPE );
    String type = deserializeFrom.getText();

    if ( type == null ) {
      throw new JsonParseException( "No type attribute found. Cannot find strategy.", deserializeFrom.getCurrentLocation() );
    }

    SerializingStrategy<? extends T, JsonGenerator, JsonParser, JsonProcessingException> strategy = serializingStrategySupport.findStrategy( type );
    Version resolvedVersion = serializingStrategySupport.resolveVersion( strategy, formatVersion );
    return strategy.deserialize( deserializeFrom, resolvedVersion );
  }

  @NotNull
  public Collection<? extends SerializingStrategy<? extends T, JsonGenerator, JsonParser, JsonProcessingException>> getStrategies() {
    return serializingStrategySupport.getStrategies();
  }

  @NotNull
  public VersionMapping addStrategy( @NotNull SerializingStrategy<? extends T, JsonGenerator, JsonParser, JsonProcessingException> strategy ) {
    return serializingStrategySupport.addStrategy( strategy );
  }

  @NotNull
  public SerializingStrategySupport<T, JsonGenerator, JsonParser, JsonProcessingException> getSerializingStrategySupport() {
    return serializingStrategySupport;
  }
}
