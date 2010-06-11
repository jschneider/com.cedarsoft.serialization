package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.FieldInitializedInConstructorInfo;
import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.cedarsoft.serialization.generator.output.AbstractXmlGenerator;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Generator for stax mate based parsers
 */
public class StaxMateGenerator extends AbstractXmlGenerator {
  @NotNull
  private final SerializingEntryCreators creators;

  /**
   * Creates a new generator
   *
   * @param decisionCallback the decision callback
   */
  public StaxMateGenerator( @NotNull XmlDecisionCallback decisionCallback ) {
    super( new CodeGenerator<XmlDecisionCallback>( decisionCallback ) );
    this.creators = new SerializingEntryCreators( codeGenerator );
  }

  @Override
  protected void addSerializationStuff( @NotNull DomainObjectDescriptor domainObjectDescriptor, @NotNull JMethod serializeMethod, @NotNull JMethod deserializeMethod ) {
    Map<FieldWithInitializationInfo, JVar> fieldToVar = Maps.newHashMap();

    //Extract the parameters for the serialize method
    JVar serializeTo = serializeMethod.listParams()[0];
    JVar object = serializeMethod.listParams()[1];

    //Extract the parameters for the deserialize method
    JVar deserializeFrom = deserializeMethod.listParams()[0];
    JVar formatVersion = deserializeMethod.listParams()[1];

    //Generate the serialization and deserialization for every field. We use the ordering of the fields used within the class
    for ( FieldWithInitializationInfo fieldInfo : domainObjectDescriptor.getFieldsToSerialize() ) {
      SerializingEntryGenerator generator = creators.findGenerator();

      generator.appendSerializing( serializeMethod, serializeTo, object, fieldInfo );

      JVar theVar = generator.appendDeserializing( deserializeMethod, deserializeFrom, formatVersion, fieldInfo );

      fieldToVar.put( fieldInfo, theVar );
    }

    //Call closeTag( deserializeFrom ); on deserialize
    deserializeMethod.body().invoke( "closeTag" ).arg( deserializeFrom );

    deserializeMethod.body().directStatement( "//Constructing the deserialized object" );

    //Now create the constructor for the deserializeMethod
    JClass domainType = codeModel.ref( domainObjectDescriptor.getQualifiedName() );
    JInvocation domainTypeInit = JExpr._new( domainType );

    {
      List<FieldWithInitializationInfo> fieldsToSerialize = Lists.newArrayList( domainObjectDescriptor.getFieldsToSerialize() );
      //Sort the fields to fit the constructor order
      Collections.sort( fieldsToSerialize, new Comparator<FieldWithInitializationInfo>() {
        @Override
        public int compare( FieldWithInitializationInfo o1, FieldWithInitializationInfo o2 ) {
          return Integer.valueOf( ( ( FieldInitializedInConstructorInfo ) o1 ).getConstructorCallInfo().getIndex() ).compareTo( ( ( FieldInitializedInConstructorInfo ) o2 ).getConstructorCallInfo().getIndex() );
        }
      } );

      for ( FieldWithInitializationInfo fieldInfo : fieldsToSerialize ) {
        domainTypeInit.arg( fieldToVar.get( fieldInfo ) );
      }

      //Add the return type
      JVar domainObjectVar = deserializeMethod.body().decl( domainType, "object", domainTypeInit );

      //todo setters(?)

      deserializeMethod.body()._return( domainObjectVar );
    }
  }

  @NotNull
  @Override
  protected JClass createSerializerExtendsExpression( @NotNull JClass domainType ) {
    return codeModel.ref( AbstractStaxMateSerializer.class ).narrow( domainType );
  }

  @Override
  @NotNull
  protected Class<?> getExceptionType() {
    return XMLStreamException.class;
  }

  @Override
  @NotNull
  protected Class<?> getSerializeFromType() {
    return XMLStreamReader.class;
  }

  @Override
  @NotNull
  protected Class<?> getSerializeToType() {
    return SMOutputElement.class;
  }
}
