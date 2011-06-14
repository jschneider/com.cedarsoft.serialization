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

package com.cedarsoft.serialization.generator.output.staxmate.serializer;

import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.Expressions;
import com.cedarsoft.codegen.model.DomainObjectDescriptor;
import com.cedarsoft.codegen.model.FieldDeclarationInfo;
import com.cedarsoft.codegen.model.FieldWithInitializationInfo;
import com.cedarsoft.serialization.generator.output.serializer.AbstractXmlGenerator;
import com.cedarsoft.serialization.generator.output.serializer.SerializeToGenerator;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generator for stax mate based parsers
 */
public class StaxMateGenerator extends AbstractXmlGenerator {

  public static final String METHOD_NAME_CLOSE_TAG = "closeTag";

  @Nonnull
  private final List<SerializeToGenerator> generators = new ArrayList<SerializeToGenerator>();

  /**
   * Creates a new generator
   *
   * @param codeGenerator the code generator
   */
  public StaxMateGenerator( @Nonnull CodeGenerator codeGenerator ) {
    super( codeGenerator );
    generators.add( new AsAttributeGenerator( codeGenerator ) );
    generators.add( new AsElementGenerator( codeGenerator ) );
    generators.add( new CollectionElementGenerator( codeGenerator ) );
    generators.add( new DelegateGenerator( codeGenerator ) );
  }

  @Nonnull
  @Override
  protected Map<FieldWithInitializationInfo, JVar> fillDeSerializationMethods( @Nonnull DomainObjectDescriptor domainObjectDescriptor, @Nonnull JDefinedClass serializerClass, @Nonnull JMethod serializeMethod, @Nonnull JMethod deserializeMethod ) {
    try {
      return super.fillDeSerializationMethods( domainObjectDescriptor, serializerClass, serializeMethod, deserializeMethod );
    } finally {
      //Call closeTag( deserializeFrom ); on deserialize
      JVar deserializeFrom = deserializeMethod.listParams()[0];
      deserializeMethod.body().directStatement( "//Finally closing element" );
      deserializeMethod.body().invoke( StaxMateGenerator.METHOD_NAME_CLOSE_TAG ).arg( deserializeFrom );
    }
  }

  @Override
  @Nonnull
  protected JVar appendDeserializeStatement( @Nonnull JDefinedClass serializerClass, @Nonnull JMethod deserializeMethod, @Nonnull JVar deserializeFrom, JVar wrapper, @Nonnull JVar formatVersion, @Nonnull FieldWithInitializationInfo fieldInfo ) {
    deserializeMethod.body().directStatement( "//" + fieldInfo.getSimpleName() );
    SerializeToGenerator serializeToHandler = getGenerator( fieldInfo );

    Expressions readExpressions = serializeToHandler.createReadFromDeserializeFromExpression( this, serializerClass, deserializeFrom, wrapper, formatVersion, fieldInfo );

    //Add the (optional) statements before
    for ( JStatement expression : readExpressions.getBefore() ) {
      deserializeMethod.body().add( expression );
    }

    //The field
    JVar field = deserializeMethod.body().decl( serializeToHandler.generateFieldType( fieldInfo ), fieldInfo.getSimpleName(), readExpressions.getExpression() );

    //Add the optional statements after
    for ( JStatement expression : readExpressions.getAfter() ) {
      deserializeMethod.body().add( expression );
    }
    return field;
  }

  @Override
  protected void appendSerializeStatement( @Nonnull JDefinedClass serializerClass, @Nonnull JMethod serializeMethod, @Nonnull JVar serializeTo, @Nonnull JVar object, @Nonnull JVar formatVersion, @Nonnull FieldWithInitializationInfo fieldInfo ) {
    serializeMethod.body().directStatement( "//" + fieldInfo.getSimpleName() );

    SerializeToGenerator serializeToHandler = getGenerator( fieldInfo );
    serializeMethod.body().add( serializeToHandler.createAddToSerializeToExpression( this, serializerClass, serializeTo, fieldInfo, object, formatVersion ) );
  }

  @Nonnull
  @Override
  protected JClass createSerializerExtendsExpression( @Nonnull JClass domainType ) {
    return codeGenerator.ref( AbstractStaxMateSerializer.class ).narrow( domainType );
  }

  @Override
  @Nonnull
  protected Class<?> getExceptionType() {
    return XMLStreamException.class;
  }

  @Override
  @Nonnull
  protected Class<?> getSerializeFromType() {
    return XMLStreamReader.class;
  }

  @Override
  @Nonnull
  protected Class<?> getSerializeToType() {
    return SMOutputElement.class;
  }

  @Nonnull
  protected SerializeToGenerator getGenerator( @Nonnull FieldDeclarationInfo fieldInfo ) {
    for ( SerializeToGenerator generator : generators ) {
      if ( generator.canHandle( fieldInfo ) ) {
        return generator;
      }
    }

    throw new IllegalStateException( "No generator found for " + fieldInfo );
  }
}
