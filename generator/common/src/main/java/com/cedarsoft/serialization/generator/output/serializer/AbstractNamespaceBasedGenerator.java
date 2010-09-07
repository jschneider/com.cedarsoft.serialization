package com.cedarsoft.serialization.generator.output.serializer;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.id.NameSpaceSupport;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractNamespaceBasedGenerator extends AbstractGenerator<XmlDecisionCallback> {
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

  protected AbstractNamespaceBasedGenerator( CodeGenerator<XmlDecisionCallback> codeGenerator ) {
    super( codeGenerator );
  }

  /**
   * Returns the namespace that is used for the serialized documents
   *
   * @param domainObjectType the domain object type
   * @return the namespace
   */
  @NotNull
  @NonNls
  protected String getNamespace( @NotNull @NonNls final String domainObjectType ) {
    return NameSpaceSupport.createNameSpaceUriBase( domainObjectType );
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
    JClass versionRangeType = codeGenerator.ref( VersionRange.class );
    return versionRangeType.staticInvoke( METHOD_NAME_FROM ).arg( JExpr.lit( from.getMajor() ) ).arg( JExpr.lit( from.getMinor() ) ).arg( JExpr.lit( from.getBuild() ) )
      .invoke( METHOD_NAME_TO ).arg( JExpr.lit( to.getMajor() ) ).arg( JExpr.lit( to.getMinor() ) ).arg( JExpr.lit( to.getBuild() ) );
  }
}
