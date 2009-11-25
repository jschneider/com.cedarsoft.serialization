package com.cedarsoft.serialization.jdom;

import com.cedarsoft.serialization.SerializingStrategy;
import org.jdom.Element;

import java.io.IOException;

/**
 * @param <T> the type
 */
public interface JDomSerializingStrategy<T> extends SerializingStrategy<T, Element, Element, IOException> {
}