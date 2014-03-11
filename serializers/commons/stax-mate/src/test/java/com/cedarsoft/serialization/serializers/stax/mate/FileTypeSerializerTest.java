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

import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileType;
import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;

import static org.junit.Assert.*;

/**
 *
 */
public class FileTypeSerializerTest extends AbstractXmlSerializerTest2<FileType> {
  @Nonnull
  @Override
  protected StreamSerializer<FileType> getSerializer() {
    return new FileTypeSerializer( new ExtensionSerializer() );
  }

  @Override
  protected void verifyDeserialized( @Nonnull FileType deserialized, @Nonnull FileType original ) {
    super.verifyDeserialized( deserialized, original );

    assertEquals( "application/special", deserialized.getContentType() );
    assertEquals( "TheId", deserialized.getId() );
    assertEquals( 2, deserialized.getExtensions().size() );
    assertEquals( ",ext", deserialized.getDefaultExtension().getCombined() );
  }

  @DataPoint
  public static final Entry<?> entry1 = create(
    new FileType( "TheId", "application/special", true, new Extension( ",", "ext" ), new Extension( ".", "_ext2" ) ),
    "<fileType dependent=\"true\">\n" +
      "  <id>TheId</id>\n" +
      "  <contentType>application/special</contentType>" +
      "  <extension default=\"true\" delimiter=\",\">ext</extension>\n" +
      "  <extension delimiter=\".\">_ext2</extension>\n" +
      "</fileType>" );
}
