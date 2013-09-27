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

package com.cedarsoft.serialization.serializers.stax.mate;

import com.cedarsoft.file.FileName;
import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import com.cedarsoft.xml.XmlCommons;
import org.apache.commons.io.Charsets;
import org.junit.*;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 *
 */
public class FileNameSerializerTest extends AbstractXmlSerializerTest2<FileName> {
  @Nonnull
  @Override
  protected StreamSerializer<FileName> getSerializer() {
    return new FileNameSerializer( new BaseNameSerializer(), new ExtensionSerializer() );
  }

  @DataPoint
  public static final Entry<?> entry1 = create( new FileName( "a", ",", "pdf" ), "<fileName>\n" +
    "  <baseName>a</baseName>\n" +
    "  <extension delimiter=\",\">pdf</extension>\n" +
    "</fileName>" );

  @Override
  protected void verifySerialized( @Nonnull Entry<FileName> entry, @Nonnull byte[] serialized ) throws Exception {
    super.verifySerialized( entry, serialized );
    assertTrue( XmlCommons.format( new String( serialized, Charsets.UTF_8 ) ), new String( serialized ).contains( "xmlns=\"http://www.cedarsoft.com/file/fileName/" + getSerializer().getFormatVersion() + "\"" ) );
  }

  @Test
  public void testDelimiter() throws IOException {
    FileName fileName = deserialize(
      "<fileName xmlns=\"http://www.cedarsoft.com/file/fileName/1.0.0\">\n" +
        "  <baseName>baseName</baseName>\n" +
        "  <extension delimiter=\".\">jpg</extension>\n" +
        "</fileName>" );

    assertEquals( "baseName", fileName.getBaseName().getName() );
    assertEquals( ".", fileName.getExtension().getDelimiter() );
    assertEquals( "jpg", fileName.getExtension().getExtension() );
  }

  @Test
  public void testMissingDelimiter() throws IOException {
    FileName fileName = deserialize(
      "<fileName xmlns=\"http://www.cedarsoft.com/file/fileName/1.0.0\">\n" +
        "  <baseName>baseName</baseName>\n" +
        "  <extension>jpg</extension>\n" +
        "</fileName>" );

    assertEquals( "baseName", fileName.getBaseName().getName() );
    assertEquals( ".", fileName.getExtension().getDelimiter() );
    assertEquals( "jpg", fileName.getExtension().getExtension() );
  }

  private FileName deserialize( String xml ) throws IOException {
    FileNameSerializer serializer = new FileNameSerializer( new BaseNameSerializer(), new ExtensionSerializer() );
    return serializer.deserialize( new ByteArrayInputStream( xml.getBytes() ) );
  }
}
