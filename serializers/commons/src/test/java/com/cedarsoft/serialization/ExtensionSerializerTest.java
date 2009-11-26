package com.cedarsoft.serialization;

import com.cedarsoft.file.Extension;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ExtensionSerializerTest extends AbstractXmlSerializerMultiTest<Extension> {
  @NotNull
  @Override
  protected AbstractStaxMateSerializer<Extension> getSerializer() {
    return new ExtensionSerializer();
  }

  @NotNull
  @Override
  protected Iterable<? extends Extension> createObjectsToSerialize() {
    return Arrays.asList(
      new Extension( ",", "jpg" ),
      new Extension( ".", "jpg" ),
      new Extension( "-", "jpg" ),
      new Extension( ",", "cr2" )
    );
  }

  @NotNull
  @Override
  protected List<? extends String> getExpectedSerialized() {
    return Arrays.asList(
      "<extension delimiter=\",\">jpg</extension>",
      "<extension delimiter=\".\">jpg</extension>",
      "<extension delimiter=\"-\">jpg</extension>",
      "<extension delimiter=\",\">cr2</extension>"
    );
  }
}
