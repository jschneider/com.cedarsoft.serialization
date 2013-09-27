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

package com.cedarsoft.serialization.ui;

import com.cedarsoft.serialization.DelegatesMappings;
import com.cedarsoft.serialization.ToString;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Comparator;

/**
 *
 */
public class DelegatesMappingVisualizer {
  @Nonnull
  private final DelegatesMappings<?, ?, ?, ?, ?> mappings;

  public DelegatesMappingVisualizer( @Nonnull DelegatesMappings<?, ?, ?, ?, ?> mappings ) {
    this.mappings = mappings;
  }

  @Nonnull

  public String visualize() throws IOException {
    VersionMappingsVisualizer<Class<?>> visualizer = new VersionMappingsVisualizer<Class<?>>( mappings.getVersionMappings(), new Comparator<Class<?>>() {
      @Override
      public int compare( Class<?> o1, Class<?> o2 ) {
        return o1.getName().compareTo( o2.getName() );
      }
    }, new ToString<Class<?>>() {
      @Nonnull
      @Override
      public String convert( @Nonnull Class<?> object ) {
        String[] parts = object.getName().split( "\\." );
        return parts[parts.length - 1];
      }
    }
    );
    return visualizer.visualize();
  }

  @Nonnull
  public static DelegatesMappingVisualizer create( @Nonnull DelegatesMappings<?, ?, ?, ?, ?> mappings ) {
    return new DelegatesMappingVisualizer( mappings );
  }

  @Nonnull
  public static String toString( @Nonnull DelegatesMappings<?, ?, ?, ?, ?> mappings ) throws IOException {
    return new DelegatesMappingVisualizer( mappings ).visualize();
  }
}
