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

package com.cedarsoft.sample.fixed.xstream;

import com.cedarsoft.sample.fixed.Money;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *
 */
//START SNIPPET: marshal
public class MoneyConverter implements Converter {
  @Override
  public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
    writer.startNode( "cents" );
    writer.setValue( String.valueOf( ( ( Money ) source ).getCents() ) );
    writer.endNode();
  }
  //END SNIPPET: marshal

  //START SNIPPET: unmarshal

  @Override
  public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
    reader.moveDown();
    long cents;

    //We have to guess which kind of XML we have
    //This might become very difficult and complicated for complex scenarios
    if ( reader.getNodeName().equals( "amount" ) ) {
      //Legacy!
      cents = Money.convertValueToCents( Double.parseDouble( reader.getValue() ) );
    } else {
      cents = Long.parseLong( reader.getValue() );
    }
    reader.getValue();
    reader.moveUp();

    return new Money( cents );
  }
  //END SNIPPET: unmarshal

  @Override
  public boolean canConvert( Class type ) {
    return type.equals( Money.class );
  }
}
