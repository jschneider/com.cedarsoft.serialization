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


  protected AbstractXmlGenerator( @NotNull CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  @Override
  protected void createConstructor( @NotNull JDefinedClass serializerClass, @NotNull DomainObjectDescriptor domainObjectDescriptor ) {
    serializerClass.constructor( JMod.PUBLIC ).body()
      .invoke( "super" ).arg( getDefaultElementName( domainObjectDescriptor ) ).arg( getNamespace( domainObjectDescriptor ) )
      .arg( createDefaultVersionRangeInvocation( AbstractXmlGenerator.VERSION, AbstractXmlGenerator.VERSION ) );
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
