package com.cedarsoft.serialization;

import com.cedarsoft.VersionException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface NameSpaceAware {
  @NotNull
  @NonNls
  String getNameSpace();

  /**
   * Verifies the name space
   *
   * @param namespace the name space
   * @throws InvalidNamespaceException
   * @throws VersionException
   */
  void verifyNamespace( @Nullable @NonNls String namespace ) throws InvalidNamespaceException, VersionException;
}