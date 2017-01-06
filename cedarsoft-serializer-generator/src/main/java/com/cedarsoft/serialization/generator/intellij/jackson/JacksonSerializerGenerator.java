package com.cedarsoft.serialization.generator.intellij.jackson;

import com.cedarsoft.serialization.generator.intellij.AbstractSerializerGenerator;
import com.cedarsoft.serialization.generator.intellij.model.FieldToSerialize;
import com.cedarsoft.serialization.generator.intellij.model.SerializerModel;
import com.intellij.openapi.project.Project;

import javax.annotation.Nonnull;

/**
 * A simple class that generates a jackson serializer
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonSerializerGenerator extends AbstractSerializerGenerator {
  public JacksonSerializerGenerator( @Nonnull Project project ) {
    super( project, "com.cedarsoft.serialization.jackson.AbstractJacksonSerializer", "com.fasterxml.jackson.core.JsonGenerator", "com.fasterxml.jackson.core.JsonParser", "com.fasterxml.jackson.core.JsonProcessingException" );
  }

  @Override
  protected void callSuperConstructor( @Nonnull SerializerModel serializerModel, @Nonnull StringBuilder constructorBuilder ) {
    constructorBuilder.append( "){" )
      .append( "super(\"" ).append( createType( serializerModel.getClassToSerializeQualifiedName() ) ).append( "\", com.cedarsoft.version.VersionRange.from(1,0,0).to());" );
  }

  @Override
  protected void appendDeserializeFieldStatements( @Nonnull SerializerModel serializerModel, @Nonnull StringBuilder methodBody ) {
    //Declare the fields
    for ( FieldToSerialize field : serializerModel.getFieldToSerializeEntries() ) {
      if (field.isCollection()) {
        methodBody.append("java.util.List<? extends ").append(field.getFieldType().getCanonicalText()).append(">");
      }
      else {
        methodBody.append(field.getFieldType().getCanonicalText());
      }

      methodBody.append(" ").append(field.getFieldName()).append("=").append(field.getDefaultValue()).append(";");
    }

    methodBody.append( "\n\n" );

    {
      //While for fields
      methodBody.append( "com.cedarsoft.serialization.jackson.JacksonParserWrapper parser = new com.cedarsoft.serialization.jackson.JacksonParserWrapper( deserializeFrom );" +
                           "while ( parser.nextToken() == com.fasterxml.jackson.core.JsonToken.FIELD_NAME ) {" +
                           "String currentName = parser.getCurrentName();\n\n" );

      //add the ifs for the field names
      for ( FieldToSerialize field : serializerModel.getFieldToSerializeEntries() ) {
        String deserializeMethodName;
        if (field.isCollection()) {
          deserializeMethodName = "deserializeArray";
        }
        else {
          deserializeMethodName = "deserialize";
        }

        methodBody.append("if ( currentName.equals( ").append(field.getPropertyConstantName()).append(" ) ) {")
          .append( "parser.nextToken();" )

          .append(field.getFieldName()).append("=" + deserializeMethodName + "(")
          .append( field.getFieldTypeBoxed() ).append( ".class" )
          .append( ", formatVersion, deserializeFrom" )
          .append( ");" )

          .append( "continue;" )
          .append( "}" )
        ;
      }

      methodBody.append( "throw new IllegalStateException( \"Unexpected field reached <\" + currentName + \">\" );" );
      methodBody.append( "}" );
    }

    methodBody.append( "\n\n" );

    //Verify deserialization
    for ( FieldToSerialize field : serializerModel.getFieldToSerializeEntries() ) {
      if ( !field.shallVerifyDeserialized() ) {
        continue;
      }

      methodBody.append( "parser.verifyDeserialized(" ).append( field.getFieldName() ).append( "," ).append( field.getPropertyConstantName() ).append( ");" );
      if ( !field.isPrimitive() ) {
        methodBody.append( "assert " ).append( field.getFieldName() ).append( " !=" ).append( field.getDefaultValue() ).append( ";" );
      }
    }

    //clean up
    methodBody.append( "\n\n" );
    methodBody.append( "parser.ensureObjectClosed();" );
    methodBody.append( "\n\n" );
  }
}
