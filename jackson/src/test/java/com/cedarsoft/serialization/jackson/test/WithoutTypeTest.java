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
package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.test.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonParseException;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class WithoutTypeTest {
  @Test
  public void testWithoutType() throws Exception {
    Foo.Serializer serializer = new Foo.Serializer();


    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( new Foo( "descri", Direction.NORTH ), out );

    JsonUtils.assertJsonEquals( "{\n" +
                                  "  \"@type\" : \"foo\",\n" +
                                  "  \"@version\" : \"1.0.0\",\n" +
                                  "  \"description\" : \"descri\",\n" +
                                  "  \"direction\" : \"NORTH\"\n" +
                                  "}", out.toString() );

    String withoutType = "{\n" +
      "  \"description\" : \"descri\",\n" +
      "  \"direction\" : \"NORTH\"\n" +
      "}";

    try {
      serializer.deserialize( new ByteArrayInputStream( withoutType.getBytes() ) );
      fail( "Where is the Exception" );
    } catch ( JsonParseException e ) {
      assertThat( e.getMessage() ).startsWith( "Invalid field. Expected <@type> but was <description>" );
    }

    Foo foo = serializer.deserialize( new ByteArrayInputStream( withoutType.getBytes() ), serializer.getFormatVersion() );
    assertThat( foo.getDescription() ).isEqualTo( "descri" );
    assertThat( foo.getDirection() ).isEqualTo( Direction.NORTH );
  }


  @Test
  public void testNonObjectType() throws Exception {
    EmailSerializer serializer =new EmailSerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( new Email( "asdf@test.de" ), out );

    JsonUtils.assertJsonEquals( "\"asdf@test.de\"", out.toString() );

    Email mail = serializer.deserialize( new ByteArrayInputStream( "\"asdf@test.de\"".getBytes() ), serializer.getFormatVersion() );
    assertThat( mail.getMail() ).isEqualTo( "asdf@test.de" );
  }
}
