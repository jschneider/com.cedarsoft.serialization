package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class VersionSerializerTest extends AbstractXmlSerializerTest<Version> {
  @Override
  @NotNull
  protected VersionSerializer getSerializer() {
    return new VersionSerializer();
  }

  @Override
  @NotNull
  protected Version createObjectToSerialize() {
    return new Version( 1, 2, 3, "build65" );
  }

  @Override
  @NotNull
  protected String getExpectedSerialized() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<version>1.2.3-build65</version>";
  }
}
