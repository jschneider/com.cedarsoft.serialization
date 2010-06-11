package com.cedarsoft.serialization.generator.staxmate;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.NameSpaceSupport;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.output.CodeGenerator;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 *
 */
public abstract class AbstractXmlGenerator extends AbstractGenerator<XmlDecisionCallback> {
  /**
   * The default namespace suffix
   */
  @NonNls
  @NotNull
  public static final String DEFAULT_NAMESPACE_SUFFIX = "1.0.0";
  /**
   * The version the serializer supports
   */
  @NotNull
  public static final Version VERSION = Version.valueOf( 1, 0, 0 );


  protected AbstractXmlGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  public void generate( @NotNull DomainObjectDescriptor domainObjectDescriptor ) throws JClassAlreadyExistsException {
    JClass domainType = codeModel.ref( domainObjectDescriptor.getQualifiedName() );

    //the class
    JDefinedClass serializerClass = codeModel._class( createSerializerClassName( domainType.fullName() ) )._extends( createSerializerExtendsExpression( domainType ) );

    //the constructor
    serializerClass.constructor( JMod.PUBLIC ).body()
      .invoke( "super" ).arg( getDefaultElementName( domainObjectDescriptor ) ).arg( getNamespace( domainObjectDescriptor ) )
      .arg( createDefaultVersionRangeInvocation( VERSION, VERSION ) );

    JMethod serializeMethod = createSerializeMethodStub( domainType, serializerClass );
    JMethod deserializeMethod = createDeserializeMethodStub( domainType, serializerClass );

    //Add the serialize stuff
    addSerializationStuff( domainObjectDescriptor, serializeMethod, deserializeMethod );
  }

  @NotNull
  protected JMethod createDeserializeMethodStub( @NotNull JType domainType, @NotNull JDefinedClass serializerClass ) {
    JMethod deserializeMethod = serializerClass.method( JMod.PUBLIC, domainType, METHOD_NAME_DESERIALIZE );
    deserializeMethod.param( getSerializeFromType(), "deserializeFrom" ).annotate( NotNull.class );
    deserializeMethod.param( Version.class, "formatVersion" ).annotate( NotNull.class );
    deserializeMethod.annotate( Override.class );
    deserializeMethod._throws( IOException.class )._throws( VersionException.class )._throws( getExceptionType() );
    return deserializeMethod;
  }

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

  @NotNull
  protected JMethod createSerializeMethodStub( @NotNull JType domainType, @NotNull JDefinedClass serializerClass ) {
    JMethod serializeMethod = serializerClass.method( JMod.PUBLIC, Void.TYPE, METHOD_NAME_SERIALIZE );
    serializeMethod.annotate( Override.class );
    serializeMethod.param( getSerializeToType(), "serializeTo" ).annotate( NotNull.class );
    serializeMethod.param( domainType, "object" ).annotate( NotNull.class );
    serializeMethod._throws( IOException.class )._throws( getExceptionType() );
    return serializeMethod;
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
}
