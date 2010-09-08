package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.file.FileType;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

import static org.junit.Assert.*;

/**
 *
 */
public class FileTypeSerializerVersionTest extends AbstractXmlVersionTest2<FileType> {
  @NotNull
  @Override
  protected Serializer<FileType> getSerializer() throws Exception {
    return new FileTypeSerializer( new ExtensionSerializer() );
  }

  @Override
  protected void verifyDeserialized( @NotNull FileType deserialized, @NotNull Version version ) throws Exception {
    assertEquals( "TheId", deserialized.getId() );
    assertEquals( 2, deserialized.getExtensions().size() );
    assertEquals( "ext", deserialized.getExtensions().get( 0 ).getExtension() );

    if ( version.equals( Version.valueOf( 1, 0, 0 ) ) ) {
      assertEquals( "application/unknown", deserialized.getContentType() );
    } else {
      assertEquals( "application/mytype", deserialized.getContentType() );
    }
  }

  @DataPoint
  public static final VersionEntry ENTRY1 = create( Version.valueOf( 1, 0, 0 ),
                                                    "<fileType dependent=\"true\">\n" +
                                                      "  <id>TheId</id>\n" +
                                                      "  <extension default=\"true\" delimiter=\",\">ext</extension>\n" +
                                                      "  <extension delimiter=\".\">_ext2</extension>\n" +
                                                      "</fileType>" );

  @DataPoint
  public static final VersionEntry ENTRY2 = create( Version.valueOf( 1, 0, 1 ),
                                                    "<fileType dependent=\"true\">\n" +
                                                      "  <id>TheId</id>\n" +
                                                      "  <contentType>application/mytype</contentType>\n" +
                                                      "  <extension default=\"true\" delimiter=\",\">ext</extension>\n" +
                                                      "  <extension delimiter=\".\">_ext2</extension>\n" +
                                                      "</fileType>" );
}
