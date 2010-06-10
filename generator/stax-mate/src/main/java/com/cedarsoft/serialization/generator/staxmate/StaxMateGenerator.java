package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.NameSpaceSupport;
import com.cedarsoft.serialization.generator.model.ClassToSerialize;
import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class StaxMateGenerator {
  @NonNls
  @NotNull
  public static final String SERIALIZER_CLASS_NAME_SUFFIX = "Serializer";
  @NonNls
  @NotNull
  public static final String DEFAULT_NAMESPACE_SUFFIX = "1.0.0";
  @NonNls
  public static final String METHOD_NAME_SERIALIZE = "serialize";
  @NonNls
  public static final String METHOD_NAME_DESERIALIZE = "deserialize";
  @NotNull
  public static final Version VERSION_FROM = Version.valueOf( 1, 0, 0 );

  @NotNull
  private final JCodeModel model;
  @NotNull
  private final SerializingEntryCreators creators;

  public StaxMateGenerator() {
    this.model = new JCodeModel();
    this.creators = new SerializingEntryCreators( model );
  }

  @NotNull
  public JCodeModel getModel() {
    return model;
  }

  @NotNull
  public JCodeModel generate( @NotNull ClassToSerialize... classesToSerialize ) throws JClassAlreadyExistsException {
    JCodeModel codeModel = new JCodeModel();

    for ( ClassToSerialize classToSerialize : classesToSerialize ) {
      generate( classToSerialize );
    }

    return codeModel;
  }

  public void generate( @NotNull ClassToSerialize classToSerialize ) throws JClassAlreadyExistsException {
    JClass domainType = model.ref( classToSerialize.getQualifiedName() );

    //the class
    JDefinedClass serializerClass = model._class( createSerializerClassName( domainType.fullName() ) )._extends( getExtendType( domainType ) );

    //the constructor
    serializerClass.constructor( JMod.PUBLIC ).body()
      .invoke( "super" ).arg( getDefaultElementName( classToSerialize ) ).arg( getNamespace( classToSerialize ) )
      .arg( createDefaultVersionRangeInvocation( VERSION_FROM, VERSION_FROM ) );

    //the serialize method
    JMethod serializeMethod = serializerClass.method( JMod.PUBLIC, Void.TYPE, METHOD_NAME_SERIALIZE );
    serializeMethod.annotate( Override.class );
    serializeMethod.param( SMOutputElement.class, "serializeTo" );
    serializeMethod.param( domainType, "object" );
    serializeMethod._throws( IOException.class )._throws( XMLStreamException.class );

    //the deserialize method
    //the serialize method
    JMethod deserializeMethod = serializerClass.method( JMod.PUBLIC, domainType, METHOD_NAME_DESERIALIZE );
    deserializeMethod.param( XMLStreamReader.class, "deserializeFrom" );
    deserializeMethod.param( Version.class, "formatVersion" );
    deserializeMethod.annotate( Override.class );
    deserializeMethod._throws( IOException.class )._throws( VersionException.class )._throws( XMLStreamException.class );

    //Add the serialize stuff
    for ( FieldWithInitializationInfo fieldInfo : classToSerialize.getFieldsToSerialize() ) {
      addFieldSerializationStuff( fieldInfo, serializeMethod, deserializeMethod );
    }
  }

  private void addFieldSerializationStuff( @NotNull FieldWithInitializationInfo fieldInfo, @NotNull JMethod serializeMethod, @NotNull JMethod deserializeMethod ) {
    SerializingEntryGenerator generator = creators.findGenerator();

    JVar serializeTo = serializeMethod.listParams()[0];
    JVar object = serializeMethod.listParams()[1];
    generator.appendSerializing( serializeMethod, serializeTo, object, fieldInfo );

    JVar deserializeFrom = deserializeMethod.listParams()[0];
    JVar formatVersion = deserializeMethod.listParams()[1];
    generator.appendDeserializing( deserializeMethod, deserializeFrom, formatVersion, fieldInfo );
  }

  @NotNull
  private JClass getExtendType( @NotNull JClass domainType ) {
    return getSerializerBaseClass().narrow( domainType );
  }

  @NotNull
  private JClass getSerializerBaseClass() {
    return model.ref( AbstractStaxMateSerializer.class );
  }

  @NotNull
  @NonNls
  String getNamespace( @NotNull ClassToSerialize classToSerialize ) {
    return NameSpaceSupport.createNameSpaceUriBase( classToSerialize.getQualifiedName() ) + "/" + DEFAULT_NAMESPACE_SUFFIX;
  }

  @NotNull
  @NonNls
  protected String getDefaultElementName( @NotNull ClassToSerialize classToSerialize ) {
    return classToSerialize.getClassDeclaration().getSimpleName().toLowerCase();
  }

  @NotNull
  protected JInvocation createDefaultVersionRangeInvocation( @NotNull Version from, @NotNull Version to ) {
    JClass versionRangeType = model.ref( VersionRange.class );
    return versionRangeType.staticInvoke( "from" ).arg( JExpr.lit( from.getMajor() ) ).arg( JExpr.lit( from.getMinor() ) ).arg( JExpr.lit( from.getBuild() ) )
      .invoke( "to" ).arg( JExpr.lit( to.getMajor() ) ).arg( JExpr.lit( to.getMinor() ) ).arg( JExpr.lit( to.getBuild() ) );
  }

  @NotNull
  @NonNls
  String createSerializerClassName( @NotNull @NonNls String domainClassName ) {
    return domainClassName + SERIALIZER_CLASS_NAME_SUFFIX;
  }
}
