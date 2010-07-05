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

package com.cedarsoft.test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
//START SNIPPET: fieldsAndConstructors
public class Car {
  private final Model model;
  private final Color color;
  private final Money basePrice;
  private final List<Extra> extras = new ArrayList<Extra>();

  public Car( Model model, Color color, Money basePrice ) {
    this( model, color, basePrice, null );
  }

  public Car( Model model, Color color, Money basePrice, Collection<? extends Extra> extras ) {
    this.model = model;
    this.color = color;
    this.basePrice = basePrice;

    if ( extras != null ) {
      this.extras.addAll( extras );
    }
  }
  //End SNIPPET: fieldsAndConstructors

  public Money getBasePrice() {
    return basePrice;
  }

  public void addExtra( Extra extra ) {
    this.extras.add( extra );
  }

  public Color getColor() {
    return color;
  }

  public List<? extends Extra> getExtras() {
    return Collections.unmodifiableList( extras );
  }

  public Model getModel() {
    return model;
  }
}
