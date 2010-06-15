package com.cedarsoft.serialization.generator.output;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ClassRefSupport {
  @NotNull
  protected final JCodeModel model;
  @NotNull
  private final Map<String, JClass> refs = new HashMap<String, JClass>();

  public ClassRefSupport( @NotNull JCodeModel model ) {
    this.model = model;
  }

  @NotNull
  public JClass ref( @NotNull @NonNls String qualifiedName ) {
    JClass resolved = refs.get( qualifiedName );
    if ( resolved != null ) {
      return resolved;
    }

    JClass newRef = model.ref( qualifiedName );
    refs.put( qualifiedName, newRef );
    return newRef;
  }
}
