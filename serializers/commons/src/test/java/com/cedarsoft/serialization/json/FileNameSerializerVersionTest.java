package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.file.FileName;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.junit.*;
import org.junit.experimental.theories.*;

public class FileNameSerializerVersionTest extends AbstractJsonVersionTest2<FileName> {

  @DataPoint
  public static final VersionEntry ENTRY1 = FileNameSerializerVersionTest.create( Version.valueOf( 1, 0, 0 ), FileNameSerializerVersionTest.class.getResource( "FileName_1.0.0_1.json" ) );

  @Override
  protected Serializer<FileName> getSerializer() throws Exception {
    return new FileNameSerializer( new BaseNameSerializer(), new ExtensionSerializer() );
  }

  @Override
  protected void verifyDeserialized( FileName deserialized, Version version ) throws Exception {
    Assert.assertEquals( "baseName", deserialized.getBaseName().getName() );
    Assert.assertEquals( ".extension", deserialized.getExtension().getCombined() );
  }

}
