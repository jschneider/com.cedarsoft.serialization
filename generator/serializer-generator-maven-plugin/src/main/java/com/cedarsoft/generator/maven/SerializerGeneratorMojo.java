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

package com.cedarsoft.generator.maven;

import com.cedarsoft.codegen.AbstractGenerator;
import com.cedarsoft.serialization.generator.StaxMateGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Generate a Serializer and the corresponding unit tests.
 * <p/>
 * All files are generated within <i>target/generated-sources</i>.
 * So no source files are overwritten by this goal.
 *
 * @goal generate
 */
public class SerializerGeneratorMojo extends AbstractGenerateMojo {
  public SerializerGeneratorMojo() {
    super( Arrays.asList( "**/*Serializer.java", "**/*Test*.java" ) );
  }

  @NotNull
  @Override
  protected AbstractGenerator createGenerator() {
    return new StaxMateGenerator();
  }

  /**
   * A list of exclusion filters for the generator.
   * The default excludes contain:
   * <ul>
   * <li>**&#47;*Serializer.java</li>
   * <li>**&#47;*Test.java</li>
   * </ul>
   * <p/>
   * Those excludes are useful to avoid recursive creation of serializers and tests.
   *
   * @parameter
   */
  private Set<String> excludes = new HashSet<String>( Arrays.asList( "**/*Serializer.java", "**/*Test*.java" ) );
}
