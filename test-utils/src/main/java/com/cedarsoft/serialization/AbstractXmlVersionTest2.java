/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import org.apache.commons.io.IOUtils;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

/**
 * It is necessary to define at least one DataPoint
 * <pre>&#064;DataPoint<br/>public static final VersionEntry ENTRY1 = create(<br/> Version.valueOf( 1, 0, 0 ),<br/> &quot;&lt;xml/&gt;&quot; );</pre>
 *
 * @param <T> the type
 */
public abstract class AbstractXmlVersionTest2<T> extends AbstractVersionTest2<T> {
  /**
   * Converts the xml string to a byte array used to deserialize.
   * This method automatically adds the namespace containing the version.
   *
   * @param xml        the xml
   * @param version    the version
   * @param serializer the serializer
   * @return the byte array using the xml string
   */
  @NotNull
  protected static byte[] processXml( @NotNull @NonNls String xml, @NotNull Version version, @NotNull AbstractXmlSerializer<?, ?, ?, ?> serializer ) throws Exception {
    return processXml( xml, serializer.createNameSpaceUri( version ) );
  }

  @NotNull
  protected static byte[] processXml( @NotNull @NonNls byte[] xml, @NotNull Version version, @NotNull AbstractXmlSerializer<?, ?, ?, ?> serializer ) throws Exception {
    return processXml( xml, serializer.createNameSpaceUri( version ) );
  }

  @NotNull
  protected static byte[] processXml( @NotNull @NonNls String xml, @NotNull @NonNls String nameSpace ) throws JDOMException, IOException {
    return AbstractXmlSerializerTest2.addNameSpace( nameSpace, xml.getBytes() ).getBytes();
  }

  @NotNull
  protected static byte[] processXml( @NotNull @NonNls byte[] xml, @NotNull @NonNls String nameSpace ) throws JDOMException, IOException {
    return AbstractXmlSerializerTest2.addNameSpace( nameSpace, xml ).getBytes();
  }

  @NotNull
  protected static VersionEntry create( @NotNull Version version, @NotNull @NonNls String xml ) {
    return new XmlVersionEntry( version, xml );
  }

  @NotNull
  protected static VersionEntry create( @NotNull Version version, @NotNull @NonNls URL expected ) {
    try {
      return new XmlVersionEntry( version, IOUtils.toByteArray( expected.openStream() ) );
    } catch ( IOException e ) {
      throw new RuntimeException( e );
    }
  }

  public static class XmlVersionEntry implements VersionEntry {
    @NotNull
    private final Version version;
    @NotNull
    @NonNls
    private final byte[] xml;

    public XmlVersionEntry( @NotNull Version version, @NotNull @NonNls byte[] xml ) {
      this.version = version;
      this.xml = xml;
    }

    public XmlVersionEntry( @NotNull Version version, @NotNull @NonNls String xml ) {
      this( version, xml.getBytes() );
    }

    @NotNull
    @Override
    public Version getVersion() {
      return version;
    }

    @NotNull
    @Override
    public byte[] getSerialized( @NotNull Serializer<?> serializer ) throws Exception {
      return processXml( xml, version, ( AbstractXmlSerializer<?, ?, ?, ?> ) serializer );
    }
  }
}
