package com.cedarsoft.serialization.stax;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.SerializingStrategySupport;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Abstract base class for a serializers that uses strategies to serialize objects.
 *
 * @param <T> the type
 */
public class AbstractDelegatingStaxMateSerializer<T> extends AbstractStaxMateSerializer<T> {
  @NotNull
  @NonNls
  private static final String ATTRIBUTE_TYPE = "type";
  @NotNull
  private final SerializingStrategySupport<T, StaxMateSerializingStrategy<T>> serializingStrategySupport;

  /**
   * Creates a new serializer
   *
   * @param defaultElementName the default element name
   * @param nameSpaceUriBase   the name space uri base
   * @param formatVersionRange the format version range
   * @param strategies         the strategies
   */
  public AbstractDelegatingStaxMateSerializer( @NotNull String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange, @NotNull StaxMateSerializingStrategy<? extends T>... strategies ) {
    this( defaultElementName, nameSpaceUriBase, formatVersionRange, Arrays.asList( strategies ) );
  }

  /**
   * Creates a new serializer
   *
   * @param defaultElementName the default element name
   * @param nameSpaceUriBase   the name space uri base
   * @param formatVersionRange the format version name
   * @param strategies         the strategies
   */
  public AbstractDelegatingStaxMateSerializer( @NotNull String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange, @NotNull Collection<? extends StaxMateSerializingStrategy<? extends T>> strategies ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
    serializingStrategySupport = new SerializingStrategySupport<T, StaxMateSerializingStrategy<T>>( strategies );
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull T object ) throws IOException {
    try {
      StaxMateSerializingStrategy<T> strategy = serializingStrategySupport.findStrategy( object );
      serializeTo.addAttribute( ATTRIBUTE_TYPE, strategy.getId() );
      strategy.serialize( serializeTo, object );
    } catch ( XMLStreamException e ) {
      throw new IOException( e );
    }
  }

  @Override
  @NotNull
  public T deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
    String type = deserializeFrom.getAttributeValue( null, ATTRIBUTE_TYPE );

    StaxMateSerializingStrategy<? extends T> strategy = serializingStrategySupport.findStrategy( type );
    return strategy.deserialize( deserializeFrom, formatVersion );
  }

  /**
   * Returns the strategies
   *
   * @return the strategies
   */
  @NotNull
  public Collection<? extends StaxMateSerializingStrategy<T>> getStrategies() {
    return serializingStrategySupport.getStrategies();
  }
}
