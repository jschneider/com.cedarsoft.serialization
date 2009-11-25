package com.cedarsoft.serialization.stax;

import com.cedarsoft.serialization.SerializingStrategy;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * @param <T> the type this strategy serializes
 */
public interface StaxSerializingStrategy<T> extends SerializingStrategy<T, XMLStreamWriter, XMLStreamReader, XMLStreamException> {
}