package com.cedarsoft.serialization.stax;

import com.cedarsoft.serialization.SerializingStrategy;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @param <T> the type this strategy serializes
 */
public interface StaxMateSerializingStrategy<T> extends SerializingStrategy<T, SMOutputElement, XMLStreamReader, XMLStreamException> {
}