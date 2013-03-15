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

package com.cedarsoft.serialization.test.performance.jaxb;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 *
 */
@Root
@XmlRootElement( namespace = "http://test.cedarsoft.com/fileType" )
@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "fileType", propOrder = {
  "dependent",
  "extension",
  "id"
} )
public class FileType implements Serializable {
  @Attribute( name = "dependent" )
  private boolean dependent;
  @Element( name = "id" )
  @Nonnull

  private String id;
  @Element( name = "extension" )
  @Nonnull
  private Extension extension;

  /**
   * JAXB constructor
   */
  public FileType() {
    this.dependent = false;
    this.id = null;
    this.extension = null;
  }

  public FileType( @Element( name = "id" ) @Nonnull String id, @Element( name = "extension" ) @Nonnull Extension extension, @Attribute( name = "dependent" ) boolean dependent ) {
    this.dependent = dependent;
    this.id = id;
    this.extension = extension;
  }

  @Nonnull
  public Extension getExtension() {
    return extension;
  }

  public boolean isDependent() {
    return dependent;
  }

  @Nonnull
  public String getId() {
    return id;
  }

  public void setDependent( boolean dependent ) {
    this.dependent = dependent;
  }

  public void setId( @Nonnull String id ) {
    this.id = id;
  }

  public void setExtension( @Nonnull Extension extension ) {
    this.extension = extension;
  }
}
