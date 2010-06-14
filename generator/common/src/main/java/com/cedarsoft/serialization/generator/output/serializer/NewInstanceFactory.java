package com.cedarsoft.serialization.generator.output.serializer;

import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.mirror.type.TypeMirror;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class NewInstanceFactory {
  @NotNull
  private final JCodeModel codeModel;
  public static final int DEFAULT_VALUE_INTEGER = 42;
  public static final float DEFAULT_VALUE_FLOAT = 44.0F;
  public static final long DEFAULT_VALUE_LONG = 43L;
  public static final double DEFAULT_VALUE_DOUBLE = 12.5;
  public static final char DEFAULT_VALUE_CHAR = 'c';

  public NewInstanceFactory( @NotNull JCodeModel codeModel ) {
    this.codeModel = codeModel;
  }

  @NotNull
  public JExpression create( @NotNull TypeMirror type, @NotNull @NonNls String simpleName ) {
    if ( DomainObjectDescriptor.isType( type, String.class ) ) {
      return JExpr.lit( simpleName );
    }

    //Primitive types
    if ( DomainObjectDescriptor.isType( type, Integer.TYPE ) ) {
      return JExpr.lit( DEFAULT_VALUE_INTEGER );
    }
    if ( DomainObjectDescriptor.isType( type, Long.TYPE ) ) {
      return JExpr.lit( DEFAULT_VALUE_LONG );
    }
    if ( DomainObjectDescriptor.isType( type, Float.TYPE ) ) {
      return JExpr.lit( DEFAULT_VALUE_FLOAT );
    }
    if ( DomainObjectDescriptor.isType( type, Double.TYPE ) ) {
      return JExpr.lit( DEFAULT_VALUE_DOUBLE );
    }
    if ( DomainObjectDescriptor.isType( type, Boolean.TYPE ) ) {
      return JExpr.lit( true );
    }
    if ( DomainObjectDescriptor.isType( type, Character.TYPE ) ) {
      return JExpr.lit( DEFAULT_VALUE_CHAR );
    }

    //Default types
    if ( DomainObjectDescriptor.isType( type, Integer.class ) ) {
      return codeModel.ref( Integer.class ).staticInvoke( "valueOf" ).arg( JExpr.lit( DEFAULT_VALUE_INTEGER ) );
    }
    if ( DomainObjectDescriptor.isType( type, Double.class ) ) {
      return codeModel.ref( Double.class ).staticInvoke( "valueOf" ).arg( JExpr.lit( DEFAULT_VALUE_DOUBLE ) );
    }
    if ( DomainObjectDescriptor.isType( type, Long.class ) ) {
      return codeModel.ref( Long.class ).staticInvoke( "valueOf" ).arg( JExpr.lit( DEFAULT_VALUE_LONG ) );
    }
    if ( DomainObjectDescriptor.isType( type, Float.class ) ) {
      return codeModel.ref( Float.class ).staticInvoke( "valueOf" ).arg( JExpr.lit( DEFAULT_VALUE_FLOAT ) );
    }
    if ( DomainObjectDescriptor.isType( type, Boolean.class ) ) {
      return codeModel.ref( Boolean.class ).staticRef( "TRUE" );
    }


    return JExpr._new( codeModel.ref( type.toString() ) );
  }
}
