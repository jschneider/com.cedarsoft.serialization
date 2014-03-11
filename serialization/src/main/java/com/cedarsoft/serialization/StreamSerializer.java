package com.cedarsoft.serialization;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface StreamSerializer<T> extends Serializer<T, OutputStream, InputStream> {
}