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

package com.cedarsoft.serialization.generator.jackson.output.serializer;

import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.Expressions;
import com.cedarsoft.codegen.NamingSupport;
import com.cedarsoft.codegen.model.DomainObjectDescriptor;
import com.cedarsoft.codegen.model.FieldDeclarationInfo;
import com.cedarsoft.codegen.model.FieldWithInitializationInfo;
import com.cedarsoft.serialization.generator.common.output.serializer.AbstractNamespaceBasedGenerator;
import com.cedarsoft.serialization.generator.common.output.serializer.AbstractXmlGenerator;
import com.cedarsoft.serialization.generator.common.output.serializer.SerializeToGenerator;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.cedarsoft.serialization.jackson.JacksonParserWrapper;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generator for stax mate based parsers
 */
public class JacksonGenerator extends AbstractNamespaceBasedGenerator {

  public static final String METHOD_NAME_CLOSE_OBJECT = "closeObject";

  @Nonnull
  private final List<SerializeToGenerator> generators = new ArrayList<SerializeToGenerator>();

  /**
   * Creates a new generator
   *
   * @param codeGenerator the code generator
   */
  public JacksonGenerator( @Nonnull CodeGenerator codeGenerator ) {
    super( codeGenerator );
    generators.add( new AsFieldGenerator( codeGenerator ) );
    generators.add( new ArrayElementGenerator( codeGenerator ) );
    generators.add( new DelegateGenerator( codeGenerator ) );
  }

  @Nonnull
  @Override
  protected String getNamespace( @Nonnull String domainObjectType ) {
    int lastIndex = domainObjectType.lastIndexOf( '.' );

    String simpleName;
    if ( lastIndex == -1 ) {
      simpleName = domainObjectType;
    } else {
      simpleName = domainObjectType.substring( lastIndex + 1 );
    }

    return NamingSupport.createXmlElementName( simpleName );
  }

  @Nonnull
  @Override
  protected JMethod createConstructor( @Nonnull JDefinedClass serializerClass, @Nonnull DomainObjectDescriptor domainObjectDescriptor ) {
    JMethod constructor = serializerClass.constructor( JMod.PUBLIC );
    constructor.body()
      .invoke( METHOD_SUPER ).arg( getNamespace( domainObjectDescriptor.getQualifiedName() ) )
      .arg( createDefaultVersionRangeInvocation( AbstractXmlGenerator.VERSION, AbstractXmlGenerator.VERSION ) );
    return constructor;
  }

  @Nonnull
  @Override
  protected Map<FieldWithInitializationInfo, JVar> fillDeSerializationMethods( @Nonnull DomainObjectDescriptor domainObjectDescriptor, @Nonnull JDefinedClass serializerClass, @Nonnull JMethod serializeMethod, @Nonnull JMethod deserializeMethod ) {
    try {
      return super.fillDeSerializationMethods( domainObjectDescriptor, serializerClass, serializeMethod, deserializeMethod );
    } finally {
      //Call closeTag( deserializeFrom ); on deserialize
      deserializeMethod.body().directStatement( "//Finally closing element" );
      deserializeMethod.body().directStatement( "parser.closeObject();" ); //wrapper is somewhere hidden...
    }
  }

  @Override
  protected JVar createDeserializeWrapper( @Nonnull JMethod deserializeMethod, @Nonnull JVar deserializeFrom ) {
    JClass wrapperType = codeGenerator.ref( JacksonParserWrapper.class );
    return deserializeMethod.body().decl( wrapperType, "parser", JExpr._new( wrapperType ).arg( deserializeFrom ) );
  }

  @Override
  @Nonnull
  protected JVar appendDeserializeStatement( @Nonnull JDefinedClass serializerClass, @Nonnull JMethod deserializeMethod, @Nonnull JVar deserializeFrom, @Nullable JVar wrapper, @Nonnull JVar formatVersion, @Nonnull FieldWithInitializationInfo fieldInfo ) {
    assert wrapper != null;

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
    return codeGenerator.ref( AbstractJacksonSerializer.class ).narrow( domainType );
  }

  @Override
  @Nonnull
  protected Class<?> getExceptionType() {
    return JsonProcessingException.class;
  }

  @Override
  @Nonnull
  protected Class<?> getSerializeFromType() {
    return JsonParser.class;
  }

  @Override
  @Nonnull
  protected Class<?> getSerializeToType() {
    return JsonGenerator.class;
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
