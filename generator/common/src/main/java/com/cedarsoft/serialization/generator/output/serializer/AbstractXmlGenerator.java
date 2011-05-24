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

import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.NamingSupport;
import com.cedarsoft.codegen.model.DomainObjectDescriptor;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import javax.annotation.Nonnull;

/**
 *
 */
public abstract class AbstractXmlGenerator extends AbstractNamespaceBasedGenerator {

  /**
   * Creates a new generator
   *
   * @param codeGenerator the used code generator
   */
  protected AbstractXmlGenerator( @Nonnull CodeGenerator codeGenerator ) {
    super( codeGenerator );
  }

  @Override
  @Nonnull
  protected JMethod createConstructor( @Nonnull JDefinedClass serializerClass, @Nonnull DomainObjectDescriptor domainObjectDescriptor ) {
    JMethod constructor = serializerClass.constructor( JMod.PUBLIC );
    constructor.body()
      .invoke( METHOD_SUPER ).arg( getDefaultElementName( domainObjectDescriptor ) ).arg( getNamespace( domainObjectDescriptor.getQualifiedName() ) )
      .arg( createDefaultVersionRangeInvocation( AbstractXmlGenerator.VERSION, AbstractXmlGenerator.VERSION ) );
    return constructor;
  }

  /**
   * Returns the default element name
   *
   * @param domainObjectDescriptor the descriptor
   * @return the default element name
   */
  @Nonnull

  protected String getDefaultElementName( @Nonnull DomainObjectDescriptor domainObjectDescriptor ) {
    return NamingSupport.createXmlElementName( domainObjectDescriptor.getClassDeclaration().getSimpleName() );
  }

}
