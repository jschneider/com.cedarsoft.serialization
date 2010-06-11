package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.NameSpaceSupport;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.FieldInitializedInConstructorInfo;
import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.cedarsoft.serialization.generator.output.ParseExpressionFactory;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Generator for stax mate based parsers
 */
public class StaxMateGenerator {
  /**
   * The suffix used for generated serializers
   */
  @NonNls
  @NotNull
  public static final String SERIALIZER_CLASS_NAME_SUFFIX = "Serializer";
  /**
   * The default namespace suffix
   */
  @NonNls
  @NotNull
  public static final String DEFAULT_NAMESPACE_SUFFIX = "1.0.0";
  /**
   * The name of the serialize method
   */
  @NonNls
  public static final String METHOD_NAME_SERIALIZE = "serialize";
  /**
   * The name of the deserialize method
   */
  @NonNls
  public static final String METHOD_NAME_DESERIALIZE = "deserialize";
  /**
   * The version the serializer supports
   */
  @NotNull
  public static final Version VERSION = Version.valueOf( 1, 0, 0 );

  @NotNull
  private final JCodeModel codeModel;
  @NotNull
  private final SerializingEntryCreators creators;

  /**
   * Creates a new generator
   *
   * @param decisionCallback the decision callback
   */
  public StaxMateGenerator( @NotNull XmlDecisionCallback decisionCallback ) {
    this.codeModel = new JCodeModel();
    this.creators = new SerializingEntryCreators( new CodeGenerator<XmlDecisionCallback>( codeModel, new ParseExpressionFactory( codeModel ), decisionCallback ) );
  }

  /**
   * Returns the code model this generator is using
   *
   * @return the code model
   */
  @NotNull
  public JCodeModel getCodeModel() {
    return codeModel;
  }

  /**
   * Generates the source code for the given classes
   *
   * @param classesToSerialize the classes test will be generated for
   * @throws JClassAlreadyExistsException
   */
  public void generate( @NotNull DomainObjectDescriptor... classesToSerialize ) throws JClassAlreadyExistsException {
    for ( DomainObjectDescriptor domainObjectDescriptor : classesToSerialize ) {
      generate( domainObjectDescriptor );
    }
  }

  public void generate( @NotNull DomainObjectDescriptor domainObjectDescriptor ) throws JClassAlreadyExistsException {
    JClass domainType = codeModel.ref( domainObjectDescriptor.getQualifiedName() );

    //the class
    JDefinedClass serializerClass = codeModel._class( createSerializerClassName( domainType.fullName() ) )._extends( getExtendType( domainType ) );

    //the constructor
    serializerClass.constructor( JMod.PUBLIC ).body()
      .invoke( "super" ).arg( getDefaultElementName( domainObjectDescriptor ) ).arg( getNamespace( domainObjectDescriptor ) )
      .arg( createDefaultVersionRangeInvocation( VERSION, VERSION ) );

    //the serialize method
    JMethod serializeMethod = serializerClass.method( JMod.PUBLIC, Void.TYPE, METHOD_NAME_SERIALIZE );
    serializeMethod.annotate( Override.class );
    serializeMethod.param( SMOutputElement.class, "serializeTo" ).annotate( NotNull.class );
    serializeMethod.param( domainType, "object" ).annotate( NotNull.class );
    serializeMethod._throws( IOException.class )._throws( XMLStreamException.class );

    //the deserialize method
    //the serialize method
    JMethod deserializeMethod = serializerClass.method( JMod.PUBLIC, domainType, METHOD_NAME_DESERIALIZE );
    deserializeMethod.param( XMLStreamReader.class, "deserializeFrom" ).annotate( NotNull.class );
    deserializeMethod.param( Version.class, "formatVersion" ).annotate( NotNull.class );
    deserializeMethod.annotate( Override.class );
    deserializeMethod._throws( IOException.class )._throws( VersionException.class )._throws( XMLStreamException.class );

    //Add the serialize stuff
    addSerializationStuff( domainObjectDescriptor, serializeMethod, deserializeMethod );
  }

  private void addSerializationStuff( @NotNull DomainObjectDescriptor domainObjectDescriptor, @NotNull JMethod serializeMethod, @NotNull JMethod deserializeMethod ) {
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
  private JClass getExtendType( @NotNull JClass domainType ) {
    return getSerializerBaseClass().narrow( domainType );
  }

  @NotNull
  private JClass getSerializerBaseClass() {
    return codeModel.ref( AbstractStaxMateSerializer.class );
  }

  @NotNull
  @NonNls
  String getNamespace( @NotNull DomainObjectDescriptor domainObjectDescriptor ) {
    return NameSpaceSupport.createNameSpaceUriBase( domainObjectDescriptor.getQualifiedName() ) + "/" + DEFAULT_NAMESPACE_SUFFIX;
  }

  @NotNull
  @NonNls
  protected String getDefaultElementName( @NotNull DomainObjectDescriptor domainObjectDescriptor ) {
    return domainObjectDescriptor.getClassDeclaration().getSimpleName().toLowerCase();
  }

  @NotNull
  protected JInvocation createDefaultVersionRangeInvocation( @NotNull Version from, @NotNull Version to ) {
    JClass versionRangeType = codeModel.ref( VersionRange.class );
    return versionRangeType.staticInvoke( "from" ).arg( JExpr.lit( from.getMajor() ) ).arg( JExpr.lit( from.getMinor() ) ).arg( JExpr.lit( from.getBuild() ) )
      .invoke( "to" ).arg( JExpr.lit( to.getMajor() ) ).arg( JExpr.lit( to.getMinor() ) ).arg( JExpr.lit( to.getBuild() ) );
  }

  @NotNull
  @NonNls
  String createSerializerClassName( @NotNull @NonNls String domainClassName ) {
    return domainClassName + SERIALIZER_CLASS_NAME_SUFFIX;
  }
}
