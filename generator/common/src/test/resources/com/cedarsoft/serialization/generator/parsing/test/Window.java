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

package com.cedarsoft.serialization.generator.parsing.test;


/**
 *
 */
public class Window {
  /**
   * the comment for field width
   */
  private final double width;
  private final double height;
  private final String description;

  /**
   * the constructor
   *
   * @param description the descri
   * @param width       the width
   * @param height      the height
   */
  public Window( String description, double width, double height ) {
    this.width = width;
    this.height = height;
    this.description = description;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Window ) ) return false;

    Window window = ( Window ) o;

    if ( Double.compare( window.height, height ) != 0 ) return false;
    if ( Double.compare( window.width, width ) != 0 ) return false;
    if ( description != null ? !description.equals( window.description ) : window.description != null ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = width != +0.0d ? Double.doubleToLongBits( width ) : 0L;
    result = ( int ) ( temp ^ ( temp >>> 32 ) );
    temp = height != +0.0d ? Double.doubleToLongBits( height ) : 0L;
    result = 31 * result + ( int ) ( temp ^ ( temp >>> 32 ) );
    result = 31 * result + ( description != null ? description.hashCode() : 0 );
    return result;
  }
}
