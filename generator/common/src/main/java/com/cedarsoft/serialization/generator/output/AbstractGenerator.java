package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.serialization.generator.decision.DecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

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
}
