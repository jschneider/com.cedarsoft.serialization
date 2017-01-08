package com.cedarsoft.serialization.generator.intellij.stax.mate;

import com.cedarsoft.serialization.generator.intellij.AbstractSerializerGenerator;
import com.cedarsoft.serialization.generator.intellij.model.FieldToSerialize;
import com.cedarsoft.serialization.generator.intellij.model.SerializerModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;

import javax.annotation.Nonnull;

/**
 * A simple class that generates a jackson serializer
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class StaxMateSerializerGenerator extends AbstractSerializerGenerator {
  public StaxMateSerializerGenerator( @Nonnull Project project ) {
    super( project, "com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer", "org.codehaus.staxmate.out.SMOutputElement", "javax.xml.stream.XMLStreamReader", "javax.xml.stream.XMLStreamException" );
  }

  @Override
  protected void callSuperConstructor( @Nonnull SerializerModel serializerModel, @Nonnull StringBuilder constructorBuilder ) {
    constructorBuilder.append( "){" )
      .append( "super(\"" ).append( createType( serializerModel.getClassToSerializeQualifiedName() ) )
      .append( "\", \"http://cedarsoft.com/serialization/" ).append( serializerModel.getClassToSerializeQualifiedName() ).append( "\"" )
      .append( ", com.cedarsoft.version.VersionRange.from(1,0,0).to());" );
  }

  @Override
  protected void appendDeserializeFieldStatements( @Nonnull SerializerModel serializerModel, @Nonnull StringBuilder methodBody ) {
    for ( FieldToSerialize field : serializerModel.getFieldToSerializeEntries() ) {

      //nextTag( deserializeFrom, ELEMENT );
      methodBody.append( "nextTag( deserializeFrom, " ).append( field.getPropertyConstantName() ).append( " );" );

      //Declare the field
      methodBody.append( field.getFieldType().getCanonicalText() ).append( " " ).append( field.getFieldName() ).append( "=" );

      //Deserialize
      methodBody.append( "deserialize(" )
        .append( field.getFieldTypeBoxed() ).append( ".class" )
        .append( ", formatVersion, deserializeFrom" )
        .append( ");" );
    }
  }
}
