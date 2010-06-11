package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.NameSpaceSupport;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMod;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

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
  @NonNls
  public static final String METHOD_NAME_FROM = "from";
  @NonNls
  public static final String METHOD_NAME_TO = "to";
  @NonNls
  public static final String METHOD_SUPER = "super";

  /**
   * Creates a new generator
   *
   * @param codeGenerator the used code generator
   */
  protected AbstractXmlGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  @Override
  protected void createConstructor( @NotNull JDefinedClass serializerClass, @NotNull DomainObjectDescriptor domainObjectDescriptor ) {
    serializerClass.constructor( JMod.PUBLIC ).body()
      .invoke( METHOD_SUPER ).arg( getDefaultElementName( domainObjectDescriptor ) ).arg( getNamespace( domainObjectDescriptor ) )
      .arg( createDefaultVersionRangeInvocation( AbstractXmlGenerator.VERSION, AbstractXmlGenerator.VERSION ) );
  }

  /**
   * Returns the namespace that is used for the serialized documents
   *
   * @param domainObjectDescriptor the object descriptor
   * @return the namespace
   */
  @NotNull
  @NonNls
  protected String getNamespace( @NotNull DomainObjectDescriptor domainObjectDescriptor ) {
    return NameSpaceSupport.createNameSpaceUriBase( domainObjectDescriptor.getQualifiedName() ) + "/" + DEFAULT_NAMESPACE_SUFFIX;
  }

  /**
   * Returns the default element name
   *
   * @param domainObjectDescriptor the descriptor
   * @return the default element name
   */
  @NotNull
  @NonNls
  protected String getDefaultElementName( @NotNull DomainObjectDescriptor domainObjectDescriptor ) {
    return domainObjectDescriptor.getClassDeclaration().getSimpleName().toLowerCase();
  }

  /**
   * Creates the default version range invocation
   *
   * @param from the from version
   * @param to   the to version
   * @return the invocation creating the version range
   */
  @NotNull
  protected JInvocation createDefaultVersionRangeInvocation( @NotNull Version from, @NotNull Version to ) {
    JClass versionRangeType = codeModel.ref( VersionRange.class );
    return versionRangeType.staticInvoke( METHOD_NAME_FROM ).arg( JExpr.lit( from.getMajor() ) ).arg( JExpr.lit( from.getMinor() ) ).arg( JExpr.lit( from.getBuild() ) )
      .invoke( METHOD_NAME_TO ).arg( JExpr.lit( to.getMajor() ) ).arg( JExpr.lit( to.getMinor() ) ).arg( JExpr.lit( to.getBuild() ) );
  }
}
