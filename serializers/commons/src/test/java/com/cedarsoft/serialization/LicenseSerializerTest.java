package com.cedarsoft.serialization;

import com.cedarsoft.license.License;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class LicenseSerializerTest extends AbstractXmlSerializerTest<License> {
  @NotNull
  @Override
  protected AbstractStaxMateSerializer<License> getSerializer() {
    return new LicenseSerializer();
  }

  @NotNull
  @Override
  protected License createObjectToSerialize() {
    return License.PUBLIC_DOMAIN;
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return
      "<license id=\"PUBLIC_DOMAIN\">\n" +
        "  <name>Public Domain</name>\n" +
        "</license>";
  }
}
