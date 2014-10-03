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

package com.cedarsoft.test.io;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import com.cedarsoft.test.Model;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 * This is a very simple serializer that serializes a Model.
 * <p>
 * Results in:<br>
 * <code>
 * &lt;model&gt;Toyota&lt;/model&gt;</code><br>
 * for<br>
 * <code>new Model("Toyota")</code>
 */
public class ModelSerializer extends AbstractStaxMateSerializer<Model> {
  //START SNIPPET: constructor

  public ModelSerializer() {
    super( "model", "http://thecompany.com/test/model", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
  }
  //END SNIPPET: constructor


  //START SNIPPET: serialize

  @Override
  public void serialize( SMOutputElement serializeTo, Model object, Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionWritable( formatVersion );
    serializeTo.addCharacters( object.getName() );
  }
  //END SNIPPET: serialize


  //START SNIPPET: deserialize

  @Override
  public Model deserialize( XMLStreamReader deserializeFrom, Version formatVersion ) throws IOException, XMLStreamException {
    assert isVersionReadable( formatVersion );
    return new Model( getText( deserializeFrom ) );
    //getText automatically closes the tag
  }
  //START SNIPPET: deserialize
}
