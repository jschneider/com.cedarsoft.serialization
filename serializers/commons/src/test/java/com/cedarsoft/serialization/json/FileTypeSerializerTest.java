package com.cedarsoft.serialization.json;

import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileType;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.AbstractSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.junit.experimental.theories.*;

public class FileTypeSerializerTest
  extends AbstractJsonSerializerTest2<FileType> {

  @DataPoint
  public static final Entry<? extends FileType> ENTRY1 = AbstractSerializerTest2.create(
    new FileType( "id", "contentType", true, new Extension( "jpg" ), new Extension( "ccx" ) ),
    FileTypeSerializerTest.class.getResource( "FileType_1.0.0_1.json" )

  );

  @Override
  protected Serializer<FileType> getSerializer() throws Exception {
    return new FileTypeSerializer( new ExtensionSerializer() );
  }

}
