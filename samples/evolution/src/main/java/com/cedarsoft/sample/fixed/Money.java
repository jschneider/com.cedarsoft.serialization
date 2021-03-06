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

package com.cedarsoft.sample.fixed;

/**
 * Represents a money object
 */
//START SNIPPET: main
public class Money {
  private final long cents;

  public Money( long cents ) {
    this.cents = cents;
  }

  @Deprecated
  public Money( double amount ) {
    this( convertValueToCents( amount ) );
  }

  @Deprecated
  public double getAmount() {
    return cents / 100.0;
  }

  public long getCents() {
    return cents;
  }

  public static long convertValueToCents( double amount ) {
    return Math.round( amount * 100 );
  }
  //END SNIPPET: main

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Money ) ) return false;

    Money money = ( Money ) o;

    if ( cents != money.cents ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return ( int ) ( cents ^ ( cents >>> 32 ) );
  }
}
