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

package com.cedarsoft.serialization.stax;


import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class CollectionsMapping {
  @Nonnull
  private final Map<String, Entry<?>> entries = new HashMap<String, Entry<?>>();

  public Entry<?> getEntry( @Nonnull String tagName ) {
    Entry<?> resolved = entries.get( tagName );
    if ( resolved == null ) {
      throw new IllegalArgumentException( "No entry found for <" + tagName + ">" );
    }
    return resolved;
  }

  @Nonnull
  public <T> CollectionsMapping append( @Nonnull Class<T> type, @Nonnull List<T> targetCollection, @Nonnull String tagName ) {
    entries.put( tagName, new Entry<T>( type, targetCollection, tagName ) );
    return this;
  }

  public static class Entry<T> {
    @Nonnull
    private final Class<T> type;
    @Nonnull
    private final List<T> targetCollection;
    @Nonnull
    private final String tagName;

    public Entry( @Nonnull Class<T> type, @Nonnull List<T> targetCollection, @Nonnull String tagName ) {
      this.type = type;
      this.targetCollection = targetCollection;
      this.tagName = tagName;
    }

    @Nonnull
    public Class<T> getType() {
      return type;
    }

    @Nonnull
    public List<T> getTargetCollection() {
      return targetCollection;
    }

    @Nonnull
    public String getTagName() {
      return tagName;
    }
  }
}
