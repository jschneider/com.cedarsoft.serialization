package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import org.jdom.Document;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
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
   * This method automatically adds the processing instruction containing the version.
   *
   * @param xml     the xml
   * @param version the version
   * @return the byte array using the xml string
   */
  @NotNull
  protected byte[] processXml( @NotNull @NonNls final String xml, @NotNull Version version ) throws Exception {
    String nameSpace = ( ( AbstractXmlSerializer<?, ?, ?, ?> ) getSerializer() ).createNameSpaceUri( version );

    Document doc = new SAXBuilder().build( new ByteArrayInputStream( xml.getBytes() ) );
    doc.getRootElement().setNamespace( Namespace.getNamespace( nameSpace ) );

    return new XMLOutputter( Format.getPrettyFormat() ).outputString( doc ).getBytes();
    //    return ( "<?" + AbstractXmlSerializer.PI_TARGET_FORMAT + " " + version.toString() + "?>" + xml ).getBytes();
  }


  /**
   * Returns a map containing the serialized xmls
   *
   * @return a map containing the serialized xmls
   */
  @NotNull
  protected abstract Map<? extends Version, ? extends String> getSerializedXml();
}
