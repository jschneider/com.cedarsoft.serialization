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

package com.cedarsoft.serialization.test.performance;

import com.cedarsoft.serialization.test.performance.jaxb.Extension;
import com.cedarsoft.serialization.test.performance.jaxb.FileType;
import com.google.gson.Gson;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class GsonTest {

  public static final String FILE_TYPE = "{\"dependent\":false,\"id\":\"jpg\",\"extension\":{\"isDefault\":true,\"delimiter\":\".\",\"extension\":\"jpg\"}}";

  @Test
  public void testIt() {
    Gson gson = new Gson();
    assertEquals( "1", gson.toJson( 1 ) );
  }

  @Test
  public void testBag() throws Exception {
    BagOfPrimitives obj = new BagOfPrimitives();
    Gson gson = new Gson();
    assertEquals( "{\"value1\":1,\"value2\":\"abc\"}", gson.toJson( obj ) );
  }

  @Test
  public void testFileType() throws Exception {
    FileType type = new FileType( "jpg", new Extension( ".", "jpg", true ), false );
    assertEquals( FILE_TYPE, new Gson().toJson( type ) );

    FileType deserialized = new Gson().fromJson( FILE_TYPE, FileType.class );
    assertEquals( "jpg", deserialized.getId() );
    assertEquals( "jpg", deserialized.getExtension().getExtension() );
    assertEquals( ".", deserialized.getExtension().getDelimiter() );
    assertTrue( deserialized.getExtension().isDefault() );
    assertFalse( deserialized.isDependent() );
  }

  static class BagOfPrimitives {
    private int value1 = 1;
    private String value2 = "abc";
    private transient int value3 = 3;

    BagOfPrimitives() {
      // no-args constructor
    }
  }
}
