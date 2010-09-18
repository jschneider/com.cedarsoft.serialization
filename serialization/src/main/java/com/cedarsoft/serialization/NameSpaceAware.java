package com.cedarsoft.serialization;

import com.cedarsoft.Version;
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
  String getNameSpaceUri();

  @NonNls
  @NotNull
  String getNameSpaceUriBase();

  @NotNull
  Version parseAndVerifyNameSpace( @Nullable @NonNls String namespaceURI ) throws InvalidNamespaceException, VersionException;
}