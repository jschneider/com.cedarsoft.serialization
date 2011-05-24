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

package com.cedarsoft.serialization.generator.output.serializer;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.id.NameSpaceSupport;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractNamespaceBasedGenerator extends AbstractGenerator<XmlDecisionCallback> {
  /**
   * The version the serializer supports
   */
  @Nonnull
  public static final Version VERSION = Version.valueOf( 1, 0, 0 );

  public static final String METHOD_NAME_FROM = "from";

  public static final String METHOD_NAME_TO = "to";

  public static final String METHOD_SUPER = "super";

  protected AbstractNamespaceBasedGenerator( @Nonnull CodeGenerator codeGenerator ) {
    super( codeGenerator );
  }

  /**
   * Returns the namespace that is used for the serialized documents
   *
   * @param domainObjectType the domain object type
   * @return the namespace
   */
  @Nonnull

  protected String getNamespace( @Nonnull final String domainObjectType ) {
    return NameSpaceSupport.createNameSpaceUriBase( domainObjectType );
  }

  /**
   * Creates the default version range invocation
   *
   * @param from the from version
   * @param to   the to version
   * @return the invocation creating the version range
   */
  @Nonnull
  protected JInvocation createDefaultVersionRangeInvocation( @Nonnull Version from, @Nonnull Version to ) {
    JClass versionRangeType = codeGenerator.ref( VersionRange.class );
    return versionRangeType.staticInvoke( METHOD_NAME_FROM ).arg( JExpr.lit( from.getMajor() ) ).arg( JExpr.lit( from.getMinor() ) ).arg( JExpr.lit( from.getBuild() ) )
      .invoke( METHOD_NAME_TO ).arg( JExpr.lit( to.getMajor() ) ).arg( JExpr.lit( to.getMinor() ) ).arg( JExpr.lit( to.getBuild() ) );
  }
}
