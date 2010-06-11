package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.output.AbstractXmlGenerator;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.google.common.collect.Maps;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Map;

/**
 * Generator for stax mate based parsers
 */
public class StaxMateGenerator extends AbstractXmlGenerator {
  @NonNls
  public static final String METHOD_NAME_CLOSE_TAG = "closeTag";
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
  protected Map<FieldDeclarationInfo, JVar> addSerializationStuff( @NotNull DomainObjectDescriptor domainObjectDescriptor, @NotNull JMethod serializeMethod, @NotNull JMethod deserializeMethod ) {
    Map<FieldDeclarationInfo, JVar> fieldToVar = Maps.newHashMap();

    //Extract the parameters for the serialize method
    JVar serializeTo = serializeMethod.listParams()[0];
    JVar object = serializeMethod.listParams()[1];

    //Extract the parameters for the deserialize method
    JVar deserializeFrom = deserializeMethod.listParams()[0];
    JVar formatVersion = deserializeMethod.listParams()[1];

    //Generate the serialization and deserialization for every field. We use the ordering of the fields used within the class
    for ( FieldDeclarationInfo fieldInfo : domainObjectDescriptor.getFieldsToSerialize() ) {
      SerializingEntryGenerator generator = creators.findGenerator();

      generator.appendSerializing( serializeMethod, serializeTo, object, fieldInfo );

      JVar theVar = generator.appendDeserializing( deserializeMethod, deserializeFrom, formatVersion, fieldInfo );

      fieldToVar.put( fieldInfo, theVar );
    }

    //Call closeTag( deserializeFrom ); on deserialize
    deserializeMethod.body().invoke( METHOD_NAME_CLOSE_TAG ).arg( deserializeFrom );
    return fieldToVar;
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
