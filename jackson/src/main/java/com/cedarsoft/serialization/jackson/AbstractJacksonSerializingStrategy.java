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

package com.cedarsoft.serialization.jackson;

import com.cedarsoft.version.VersionRange;

import javax.annotation.Nonnull;

/**
 * @param <T> the type
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractJacksonSerializingStrategy<T> extends AbstractJacksonSerializer<T> implements JacksonSerializingStrategy<T> {
  @Nonnull
  private final String id;
  @Nonnull
  private final Class<? extends T> supportedType;

  protected AbstractJacksonSerializingStrategy( @Nonnull String id, @Nonnull String type, @Nonnull Class<? extends T> supportedType, @Nonnull VersionRange formatVersionRange ) {
    super( type, formatVersionRange );
    this.id = id;
    this.supportedType = supportedType;
  }

  @Override
  @Nonnull
  public String getId() {
    return id;
  }

  @Override
  public boolean supports( @Nonnull Object object ) {
    return supportedType.isAssignableFrom( object.getClass() );
  }

  @Nonnull
  public Class<? extends T> getSupportedType() {
    return supportedType;
  }

  @Override
  public final boolean isObjectType() {
    return super.isObjectType();
  }
}
