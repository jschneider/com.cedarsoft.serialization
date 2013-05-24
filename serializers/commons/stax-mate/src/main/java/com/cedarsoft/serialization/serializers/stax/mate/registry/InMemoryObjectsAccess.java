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

package com.cedarsoft.serialization.serializers.stax.mate.registry;

import com.cedarsoft.exceptions.StillContainedException;
import com.cedarsoft.serialization.NotFoundException;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class InMemoryObjectsAccess implements StreamBasedObjectsAccess {
  @Nonnull

  private final Map<String, byte[]> serialized = new HashMap<String, byte[]>();

  @Override
  @Nonnull
  public InputStream getInputStream(@Nonnull String id) {
    byte[] found = serialized.get(id);
    if (found == null) {
      throw new IllegalArgumentException("No stored data found for <" + id + ">");
    }
    return new ByteArrayInputStream(found);
  }

  @Nonnull
  @Override
  public Set<? extends String> getIds() throws IOException {
    return serialized.keySet();
  }

  @Override
  @Nonnull
  public OutputStream openOut(@Nonnull final String id) {
    byte[] stored = serialized.get(id);
    if (stored != null) {
      throw new StillContainedException(id);
    }

    return new ByteArrayOutputStream() {
      @Override
      public void close() throws IOException {
        super.close();
        serialized.put(id, toByteArray());
      }
    };
  }

  @Override
  public OutputStream openOutForUpdate(@Nonnull final String id) throws NotFoundException, FileNotFoundException {
    byte[] stored = serialized.get(id);
    if (stored == null) {
      throw new NotFoundException(id);
    }

    return new ByteArrayOutputStream() {
      @Override
      public void close() throws IOException {
        super.close();
        serialized.put(id, toByteArray());
      }
    };
  }

  @Override
  public void delete(@Nonnull String id) throws NotFoundException {
    byte[] bytes = serialized.remove(id);
    if (bytes == null) {
      throw new NotFoundException("id");
    }
  }

  public void clear() {
    serialized.clear();
  }
}
