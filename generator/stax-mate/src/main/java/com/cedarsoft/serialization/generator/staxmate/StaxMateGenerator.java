package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.output.AbstractXmlGenerator;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
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
   * @param codeGenerator the code generator
   */
  public StaxMateGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
    this.creators = new SerializingEntryCreators( this.codeGenerator );
  }

  @NotNull
  @Override
  protected Map<FieldDeclarationInfo, JVar> fillDeSerializationMethods( @NotNull DomainObjectDescriptor domainObjectDescriptor, @NotNull JDefinedClass serializerClass, @NotNull JMethod serializeMethod, @NotNull JMethod deserializeMethod ) {
    try {
      return super.fillDeSerializationMethods( domainObjectDescriptor, serializerClass, serializeMethod, deserializeMethod );
    } finally {
      //Call closeTag( deserializeFrom ); on deserialize
      JVar deserializeFrom = deserializeMethod.listParams()[0];
      deserializeMethod.body().invoke( StaxMateGenerator.METHOD_NAME_CLOSE_TAG ).arg( deserializeFrom );
    }
  }

  @Override
  @NotNull
  protected JVar appendDeserializeStatement( @NotNull JDefinedClass serializerClass, @NotNull JMethod deserializeMethod, @NotNull JVar deserializeFrom, @NotNull JVar formatVersion, @NotNull FieldDeclarationInfo fieldInfo ) {
    SerializingEntryGenerator generator = creators.findGenerator();
    return generator.appendDeserializing( deserializeMethod, deserializeFrom, formatVersion, fieldInfo );
  }

  @Override
  protected void appendSerializeStatement( @NotNull JDefinedClass serializerClass, @NotNull JMethod serializeMethod, @NotNull JVar serializeTo, @NotNull JVar object, @NotNull FieldDeclarationInfo fieldInfo ) {
    SerializingEntryGenerator generator = creators.findGenerator();
    generator.appendSerializing( serializeMethod, serializeTo, object, fieldInfo );
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
