package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.file.FileType;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.junit.*;
import org.junit.experimental.theories.*;

public class FileTypeSerializerVersionTest
  extends AbstractJsonVersionTest2<FileType> {

  @DataPoint
  public static final VersionEntry ENTRY1 = FileTypeSerializerVersionTest.create(
    Version.valueOf( 1, 0, 0 ),
    FileTypeSerializerVersionTest.class.getResource( "FileType_1.0.0_1.json" ) );

  @Override
  protected Serializer<FileType> getSerializer()
    throws Exception {
    return new FileTypeSerializer( new ExtensionSerializer() );
  }

  @Override
  protected void verifyDeserialized( FileType deserialized, Version version )
    throws Exception {
    Assert.assertEquals( 2, deserialized.getExtensions().size() );
    Assert.assertEquals( "id", deserialized.getId() );
    Assert.assertEquals( true, deserialized.isDependentType() );
    Assert.assertEquals( "contentType", deserialized.getContentType() );
  }

}
