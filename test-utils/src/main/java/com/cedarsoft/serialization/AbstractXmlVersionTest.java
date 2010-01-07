package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @param <T> the type
 */
public abstract class AbstractXmlVersionTest<T> extends AbstractVersionTest<T> {
  @NotNull
  @Override
  protected final Map<? extends Version, ? extends byte[]> getSerialized() throws Exception {
    Map<Version, byte[]> serializedMap = new HashMap<Version, byte[]>();
    for ( Map.Entry<? extends Version, ? extends String> entry : getSerializedXml().entrySet() ) {
      byte[] xml = processXml( entry.getValue(), entry.getKey() );
      serializedMap.put( entry.getKey(), xml );
    }

    return serializedMap;
  }

  /**
   * Converts the xml string to a byte array used to deserialize.
   * This method automatically adds the namespace containing the version.
   *
   * @param xml     the xml
   * @param version the version
   * @return the byte array using the xml string
   */
  @NotNull
  protected byte[] processXml( @NotNull @NonNls final String xml, @NotNull Version version ) throws Exception {
    String nameSpace = ( ( AbstractXmlSerializer<?, ?, ?, ?> ) getSerializer() ).createNameSpaceUri( version );
    return AbstractXmlSerializerTest.addNameSpace( xml, nameSpace ).getBytes();
  }

  /**
   * Returns a map containing the serialized xmls
   *
   * @return a map containing the serialized xmls
   */
  @NotNull
  protected abstract Map<? extends Version, ? extends String> getSerializedXml();
}
