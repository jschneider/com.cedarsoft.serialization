package com.cedarsoft.serialization.jdom;

import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.SerializingStrategySupport;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @param <T> the type
 */
public class AbstractDelegatingJDomSerializer<T> extends AbstractJDomSerializer<T> {
  @NotNull
  @NonNls
  private static final String ATTRIBUTE_TYPE = "type";
  @NotNull
  private final SerializingStrategySupport<T, JDomSerializingStrategy<T>> serializingStrategySupport;

  public AbstractDelegatingJDomSerializer( @NotNull String defaultElementName, @NotNull VersionRange formatVersionRange, @NotNull JDomSerializingStrategy<? extends T>... strategies ) {
    this( defaultElementName, formatVersionRange, Arrays.asList( strategies ) );
  }

  public AbstractDelegatingJDomSerializer( @NotNull String defaultElementName, @NotNull VersionRange formatVersionRange, @NotNull Collection<? extends JDomSerializingStrategy<? extends T>> strategies ) {
    super( defaultElementName, formatVersionRange );
    this.serializingStrategySupport = new SerializingStrategySupport<T, JDomSerializingStrategy<T>>( strategies );
  }

  @Override
  @NotNull
  public Element serialize( @NotNull Element serializeTo, @NotNull T object ) throws IOException {
    JDomSerializingStrategy<T> strategy = serializingStrategySupport.findStrategy( object );
    serializeTo.setAttribute( ATTRIBUTE_TYPE, strategy.getId() );
    strategy.serialize( serializeTo, object );

    return serializeTo;
  }

  @Override
  @NotNull
  public T deserialize( @NotNull Element deserializeFrom ) throws IOException {
    String type = deserializeFrom.getAttributeValue( ATTRIBUTE_TYPE );

    JDomSerializingStrategy<T> strategy = serializingStrategySupport.findStrategy( type );
    return strategy.deserialize( deserializeFrom );
  }

  @NotNull
  public Collection<? extends JDomSerializingStrategy<T>> getStrategies() {
    return serializingStrategySupport.getStrategies();
  }
}
