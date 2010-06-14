package com.cedarsoft.serialization.generator.output.serializer;

import com.sun.mirror.type.TypeMirror;
import com.sun.mirror.util.TypeVisitor;
import org.jetbrains.annotations.NotNull;

/**
*
*/
public class TypeMirrorMock implements TypeMirror {
  @NotNull
  private final Class<?> type;

  TypeMirrorMock( @NotNull Class<?> type ) {
    this.type = type;
  }

  @Override
  public void accept( TypeVisitor v ) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return type.getName();
  }
}
