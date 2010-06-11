package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.serialization.generator.decision.DecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.FieldInitializedInConstructorInfo;
import com.cedarsoft.serialization.generator.model.FieldWithInitializationInfo;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @param <T> the type of the decision callback
 */
public abstract class AbstractGenerator<T extends DecisionCallback> {
  /**
   * The suffix used for generated serializers
   */
  @NonNls
  @NotNull
  public static final String SERIALIZER_CLASS_NAME_SUFFIX = "Serializer";
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
  @NonNls
  public static final String METHOD_NAME_DESERIALIZE_FROM = "deserializeFrom";
  @NonNls
  public static final String PARAM_NAME_FORMAT_VERSION = "formatVersion";
  @NonNls
  public static final String PARAM_NAME_SERIALIZE_TO = "serializeTo";
  @NonNls
  public static final String PARAM_NAME_OBJECT = "object";

  @NotNull
  protected final CodeGenerator<T> codeGenerator;
  @NotNull
  protected final JCodeModel codeModel;

  protected AbstractGenerator( @NotNull CodeGenerator<T> codeGenerator ) {
    this.codeGenerator = codeGenerator;
    this.codeModel = codeGenerator.getModel();
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

  /**
   * Returns the class the serializer extends (including type information!)
   *
   * @param domainType the domain type
   * @return the class the serializer extends
   */
  @NotNull
  protected abstract JClass createSerializerExtendsExpression( @NotNull JClass domainType );

  /**
   * Creates the class name for the serializer
   *
   * @param domainClassName the class name of the domain object that is serialized
   * @return the created class name for the serializer
   */
  @NotNull
  @NonNls
  protected String createSerializerClassName( @NotNull @NonNls String domainClassName ) {
    return domainClassName + SERIALIZER_CLASS_NAME_SUFFIX;
  }

  @NotNull
  public CodeGenerator<T> getCodeGenerator() {
    return codeGenerator;
  }

  public void generate( @NotNull DomainObjectDescriptor domainObjectDescriptor ) throws JClassAlreadyExistsException {
    JClass domainType = codeModel.ref( domainObjectDescriptor.getQualifiedName() );

    //the class
    JDefinedClass serializerClass = codeModel._class( createSerializerClassName( domainType.fullName() ) )._extends( createSerializerExtendsExpression( domainType ) );

    //the constructor
    createConstructor( serializerClass, domainObjectDescriptor );

    JMethod serializeMethod = createSerializeMethodStub( domainType, serializerClass );
    JMethod deserializeMethod = createDeserializeMethodStub( domainType, serializerClass );

    //Add the serialize stuff
    Map<FieldWithInitializationInfo, JVar> fieldToVar = addSerializationStuff( domainObjectDescriptor, serializeMethod, deserializeMethod );

    //Now construct the deserialized object
    constructDeserializedObject( domainObjectDescriptor, deserializeMethod, fieldToVar );
  }

  protected void constructDeserializedObject( @NotNull DomainObjectDescriptor domainObjectDescriptor, @NotNull JMethod deserializeMethod, @NotNull Map<FieldWithInitializationInfo, JVar> fieldToVar ) {
    deserializeMethod.body().directStatement( "//Constructing the deserialized object" );

    //Now create the constructor for the deserializeMethod
    JClass domainType = codeModel.ref( domainObjectDescriptor.getQualifiedName() );
    JInvocation domainTypeInit = JExpr._new( domainType );

    {
      List<? extends FieldInitializedInConstructorInfo> fieldsToSerialize = domainObjectDescriptor.getFieldsToSerializeInitializedInConstructor();
      for ( FieldInitializedInConstructorInfo fieldInfo : fieldsToSerialize ) {
        domainTypeInit.arg( fieldToVar.get( fieldInfo ) );
      }

      //Add the return type
      JVar domainObjectVar = deserializeMethod.body().decl( domainType, "object", domainTypeInit );

      //todo setters(?)

      deserializeMethod.body()._return( domainObjectVar );
    }
  }

  /**
   * Creates the constructor for the given serializer class (if necessary)
   *
   * @param serializerClass        the serialize class
   * @param domainObjectDescriptor the domain object descriptor
   */
  protected abstract void createConstructor( @NotNull JDefinedClass serializerClass, @NotNull DomainObjectDescriptor domainObjectDescriptor );

  @NotNull
  protected JMethod createSerializeMethodStub( @NotNull JType domainType, @NotNull JDefinedClass serializerClass ) {
    JMethod serializeMethod = serializerClass.method( JMod.PUBLIC, Void.TYPE, METHOD_NAME_SERIALIZE );
    serializeMethod.annotate( Override.class );
    serializeMethod.param( getSerializeToType(), PARAM_NAME_SERIALIZE_TO ).annotate( NotNull.class );
    serializeMethod.param( domainType, PARAM_NAME_OBJECT ).annotate( NotNull.class );
    serializeMethod._throws( IOException.class )._throws( getExceptionType() );
    return serializeMethod;
  }

  @NotNull
  protected JMethod createDeserializeMethodStub( @NotNull JType domainType, @NotNull JDefinedClass serializerClass ) {
    JMethod deserializeMethod = serializerClass.method( JMod.PUBLIC, domainType, METHOD_NAME_DESERIALIZE );
    deserializeMethod.param( getSerializeFromType(), METHOD_NAME_DESERIALIZE_FROM ).annotate( NotNull.class );
    deserializeMethod.param( Version.class, PARAM_NAME_FORMAT_VERSION ).annotate( NotNull.class );
    deserializeMethod.annotate( Override.class );
    deserializeMethod._throws( IOException.class )._throws( VersionException.class )._throws( getExceptionType() );
    return deserializeMethod;
  }

  /**
   * Adds the serialization stuff to the (de)serialize methods
   *
   * @param domainObjectDescriptor the domain object descriptor
   * @param serializeMethod        the serialize method
   * @param deserializeMethod      the deserialize method
   */
  protected abstract Map<FieldWithInitializationInfo, JVar> addSerializationStuff( @NotNull DomainObjectDescriptor domainObjectDescriptor, @NotNull JMethod serializeMethod, @NotNull JMethod deserializeMethod );

  /**
   * Returns the exception type that is thrown on the serialize and deserialize methods
   *
   * @return the exception type
   */
  @NotNull
  protected abstract Class<?> getExceptionType();

  /**
   * Returns the type of the serialize from object
   *
   * @return the type of the serialize from object
   */
  @NotNull
  protected abstract Class<?> getSerializeFromType();

  /**
   * Returns the type of the serializeTo type
   *
   * @return the type of the serializeTo type
   */
  @NotNull
  protected abstract Class<?> getSerializeToType();
}
