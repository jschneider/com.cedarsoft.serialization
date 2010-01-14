package com.cedarsoft.serialization;

import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileType;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class FileTypeSerializerTest extends AbstractXmlSerializerMultiTest<FileType> {
  @NotNull
  @Override
  protected AbstractStaxMateSerializer<FileType> getSerializer() {
    return new FileTypeSerializer( new ExtensionSerializer() );
  }

  @NotNull
  @Override
  protected Iterable<? extends FileType> createObjectsToSerialize() throws Exception {
    return Arrays.asList(
      new FileType( "TheId", true, new Extension( ",", "ext" ), new Extension( ".", "_ext2" ) )
    );
  }

  @NotNull
  @Override
  protected List<? extends String> getExpectedSerialized() {
    return Arrays.asList(
      "<fileType dependent=\"true\">\n" +
        "  <id>TheId</id>\n" +
        "  <extension default=\"true\" delimiter=\",\">ext</extension>\n" +
        "  <extension delimiter=\".\">_ext2</extension>\n" +
        "</fileType>" );
  }
}
