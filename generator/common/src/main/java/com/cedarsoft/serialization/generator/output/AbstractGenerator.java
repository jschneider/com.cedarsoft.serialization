package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.serialization.generator.decision.DecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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
    addSerializationStuff( domainObjectDescriptor, serializeMethod, deserializeMethod );
  }

  /**
   * Creates the constructor for the given serializer class (if necessary)
   *
   * @param serializerClass        the serialize class
   * @param domainObjectDescriptor the domain object descriptor
   */
  protected abstract void createConstructor( @NotNull JDefinedClass serializerClass, @NotNull DomainObjectDescriptor domainObjectDescriptor );

  @NotNull
  protected JMethod createDeserializeMethodStub( @NotNull JType domainType, @NotNull JDefinedClass serializerClass ) {
    JMethod deserializeMethod = serializerClass.method( JMod.PUBLIC, domainType, METHOD_NAME_DESERIALIZE );
    deserializeMethod.param( getSerializeFromType(), "deserializeFrom" ).annotate( NotNull.class );
    deserializeMethod.param( Version.class, "formatVersion" ).annotate( NotNull.class );
    deserializeMethod.annotate( Override.class );
    deserializeMethod._throws( IOException.class )._throws( VersionException.class )._throws( getExceptionType() );
    return deserializeMethod;
  }

  @NotNull
  protected JMethod createSerializeMethodStub( @NotNull JType domainType, @NotNull JDefinedClass serializerClass ) {
    JMethod serializeMethod = serializerClass.method( JMod.PUBLIC, Void.TYPE, METHOD_NAME_SERIALIZE );
    serializeMethod.annotate( Override.class );
    serializeMethod.param( getSerializeToType(), "serializeTo" ).annotate( NotNull.class );
    serializeMethod.param( domainType, "object" ).annotate( NotNull.class );
    serializeMethod._throws( IOException.class )._throws( getExceptionType() );
    return serializeMethod;
  }

  /**
   * Adds the serialization stuff to the (de)serialize methods
   *
   * @param domainObjectDescriptor the domain object descriptor
   * @param serializeMethod        the serialize method
   * @param deserializeMethod      the deserialize method
   */
  protected abstract void addSerializationStuff( @NotNull DomainObjectDescriptor domainObjectDescriptor, @NotNull JMethod serializeMethod, @NotNull JMethod deserializeMethod );

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
