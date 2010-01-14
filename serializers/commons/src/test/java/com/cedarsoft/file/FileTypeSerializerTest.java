package com.cedarsoft.file;

import com.cedarsoft.serialization.AbstractXmlSerializerTest;
import com.cedarsoft.serialization.ExtensionSerializer;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.jetbrains.annotations.NotNull;
import org.testng.*;

/**
 *
 */
public class FileTypeSerializerTest extends AbstractXmlSerializerTest<FileType> {
  @NotNull
  @Override
  protected AbstractStaxMateSerializer<FileType> getSerializer() {
    return new FileTypeSerializer( new ExtensionSerializer() );
  }

  @NotNull
  @Override
  protected FileType createObjectToSerialize() {
    return new FileType( "TheId", true, new Extension( ",", "ext" ), new Extension( ".", "_ext2" ) );
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<fileType dependent=\"true\">\n" +
      "  <id>TheId</id>\n" +
      "  <extension default=\"true\" delimiter=\",\">ext</extension>\n" +
      "  <extension delimiter=\".\">_ext2</extension>\n" +
      "</fileType>";
  }

  @Override
  protected void verifyDeserialized( @NotNull FileType fileType ) {
    Assert.assertEquals( new FileType( "TheId", true, new Extension( ",", "ext" ), new Extension( ".", "_ext2" ) ), fileType );
  }
}
