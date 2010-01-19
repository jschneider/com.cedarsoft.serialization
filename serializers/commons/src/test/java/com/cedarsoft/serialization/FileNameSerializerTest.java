/**
 * Copyright (C) 2010 cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce.txt
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

import com.cedarsoft.file.FileName;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.xml.XmlCommons;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class FileNameSerializerTest extends AbstractXmlSerializerTest<FileName> {
  @NotNull
  @Override
  protected AbstractStaxMateSerializer<FileName> getSerializer() {
    return new FileNameSerializer( new BaseNameSerializer(), new ExtensionSerializer() );
  }

  @NotNull
  @Override
  protected FileName createObjectToSerialize() {
    return new FileName( "a", ",", "pdf" );
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<fileName>\n" +
      "  <baseName>a</baseName>\n" +
      "  <extension delimiter=\",\">pdf</extension>\n" +
      "</fileName>";
  }

  @Override
  protected void verifyDeserialized( @NotNull FileName fileName ) {
    assertEquals( fileName, createObjectToSerialize() );
  }

  @Override
  protected void verifySerialized( @NotNull byte[] serialized ) throws Exception, IOException {
    super.verifySerialized( serialized );
    assertTrue( new String( serialized ).contains( "xmlns=\"http://www.cedarsoft.com/file/fileName/" + getSerializer().getFormatVersion() + "\"" ), XmlCommons.format( new String( serialized ) ) );
  }
}
