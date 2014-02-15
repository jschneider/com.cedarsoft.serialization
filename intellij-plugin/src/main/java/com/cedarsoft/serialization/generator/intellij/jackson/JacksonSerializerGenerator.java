package com.cedarsoft.serialization.generator.intellij.jackson;

import com.cedarsoft.serialization.generator.intellij.model.FieldSetter;
import com.cedarsoft.serialization.generator.intellij.model.FieldToSerialize;
import com.cedarsoft.serialization.generator.intellij.model.SerializerModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A simple class that generates a jackson serializer
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonSerializerGenerator extends AbstractSerializerGenerator {
  public JacksonSerializerGenerator( @Nonnull Project project ) {
    super( project, "com.cedarsoft.serialization.jackson.AbstractJacksonSerializer", "com.fasterxml.jackson.core.JsonGenerator", "com.fasterxml.jackson.core.JsonParser" );
  }

  @Override
  @Nonnull
  protected PsiElement generateSerializeMethod( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    @Nonnull PsiClass classToSerialize = serializerModel.getClassToSerialize();
    @Nonnull Collection<? extends FieldToSerialize> fields = serializerModel.getFieldToSerializeEntries();

    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( "@Override public void serialize (" )
      .append( notNull() ).append( serializeToType ).append( " serializeTo, " )
      .append( notNull() )
      .append( classToSerialize.getQualifiedName() ).append( " object," )
      .append( notNull() )
      .append( "com.cedarsoft.version.Version formatVersion" )
      .append( ")throws java.io.IOException, com.cedarsoft.version.VersionException{" );

    methodBuilder.append( "verifyVersionWritable( formatVersion );" );

    for ( FieldToSerialize field : fields ) {
      methodBuilder.append( "serialize(object." ).append( field.getAccessor() ).append( "," ).append( field.getFieldTypeBoxed() ).append( ".class, " ).append( field.getPropertyConstantName() ).append( " , serializeTo, formatVersion);" );
    }

    methodBuilder.append( "}" );

    return elementFactory.createMethodFromText( methodBuilder.toString(), serializerClass );
  }

  @Override
  @Nonnull
  protected PsiElement generateDeserializeMethod( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    @Nonnull PsiClass classToSerialize = serializerModel.getClassToSerialize();
    @Nonnull Collection<? extends FieldToSerialize> fields = serializerModel.getFieldToSerializeEntries();

    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( "@Override public " ).append( notNull() ).append( classToSerialize.getQualifiedName() ).append( " deserialize(" )
      .append( notNull() ).append( deserializeFromType ).append( " deserializeFrom, " )
      .append( notNull() )
      .append( "com.cedarsoft.version.Version formatVersion" )
      .append( ") throws java.io.IOException, com.cedarsoft.version.VersionException {" );

    methodBuilder.append( "verifyVersionWritable( formatVersion );" );
    methodBuilder.append( "\n\n" );

    //Declare the fields
    for ( FieldToSerialize field : fields ) {
      methodBuilder.append( field.getFieldType().getCanonicalText() ).append( " " ).append( field.getFieldName() ).append( "=" ).append( field.getDefaultValue() ).append( ";" );

    }

    methodBuilder.append( "\n\n" );

    {
      //While for fields
      methodBuilder.append( "com.cedarsoft.serialization.jackson.JacksonParserWrapper parser = new com.cedarsoft.serialization.jackson.JacksonParserWrapper( deserializeFrom );" +
                              "while ( parser.nextToken() == com.fasterxml.jackson.core.JsonToken.FIELD_NAME ) {" +
                              "String currentName = parser.getCurrentName();\n\n" );

      //add the ifs for the field names
      for ( FieldToSerialize field : fields ) {
        methodBuilder.append( "if ( currentName.equals( " ).append( field.getPropertyConstantName() ).append( " ) ) {" )
          .append( "parser.nextToken();" )

          .append( field.getFieldName() ).append( "=deserialize(" )
          .append( field.getFieldTypeBoxed() ).append( ".class" )
          .append( ", formatVersion, deserializeFrom" )
          .append( ");" )

          .append( "continue;" )
          .append( "}" )
        ;
      }

      methodBuilder.append( "throw new IllegalStateException( \"Unexpected field reached <\" + currentName + \">\" );" );
      methodBuilder.append( "}" );
    }

    methodBuilder.append( "\n\n" );

    //Verify deserialization
    for ( FieldToSerialize field : fields ) {
      if ( !field.shallVerifyDeserialized() ) {
        continue;
      }

      methodBuilder.append( "parser.verifyDeserialized(" ).append( field.getFieldName() ).append( "," ).append( field.getPropertyConstantName() ).append( ");" );
      if ( !field.isPrimitive() ) {
        methodBuilder.append( "assert " ).append( field.getFieldName() ).append( " !=" ).append( field.getDefaultValue() ).append( ";" );
      }
    }

    methodBuilder.append( "\n\n" );

    //clean up
    methodBuilder.append( "parser.ensureObjectClosed();" );
    methodBuilder.append( "\n\n" );

    //Create the deserialized object


    methodBuilder.append( classToSerialize.getQualifiedName() ).append( " object = new " ).append( classToSerialize.getQualifiedName() ).append( "(" );

    List<FieldToSerialize> constructorArguments = findConstructorArgs( fields );

    for ( Iterator<FieldToSerialize> iterator = constructorArguments.iterator(); iterator.hasNext(); ) {
      FieldToSerialize constructorArgument = iterator.next();
      methodBuilder.append( constructorArgument.getFieldName() );

      if ( iterator.hasNext() ) {
        methodBuilder.append( "," );
      }
    }

    methodBuilder.append( ");" );

    //Adding the setters
    for ( FieldToSerialize field : fields ) {
      FieldSetter fieldSetter = field.getFieldSetter();
      if ( !fieldSetter.isSetterAccess() ) {
        continue;
      }

      methodBuilder.append( "object." ).append( ( ( FieldSetter.SetterFieldSetter ) fieldSetter ).getSetter() ).append( "(" ).append( field.getFieldName() ).append( ");" );
    }

    methodBuilder.append( " return object;" );
    methodBuilder.append( "}" );
    return elementFactory.createMethodFromText( methodBuilder.toString(), serializerClass );
  }
}
