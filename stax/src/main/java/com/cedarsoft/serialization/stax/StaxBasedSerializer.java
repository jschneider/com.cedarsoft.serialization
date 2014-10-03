package com.cedarsoft.serialization.stax;

import com.cedarsoft.serialization.PluggableSerializer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Interface for stax based serializers.
 * <p>
 * ATTENTION:
 * Serializers based on stax must consume all events for their tag (including END_ELEMENT).<br>
 * This is especially true for PluggableSerializers.
 *
 * @param <T> the type
 * @param <S> the object to serialize to
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface StaxBasedSerializer<T, S> extends PluggableSerializer<T, S, XMLStreamReader, XMLStreamException, OutputStream, InputStream> {
}
