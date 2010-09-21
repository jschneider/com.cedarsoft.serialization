package com.cedarsoft.serialization.json;

import com.cedarsoft.file.FileName;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.junit.experimental.theories.*;

public class FileNameSerializerTest extends AbstractJsonSerializerTest2<FileName> {

  @DataPoint
  public static final Entry<? extends FileName> ENTRY1 = FileNameSerializerTest.create( new FileName( "baseName", ".", "extension" ), FileNameSerializerTest.class.getResource( "FileName_1.0.0_1.json" ) );

  @Override
  protected Serializer<FileName> getSerializer() throws Exception {
    return new FileNameSerializer( new BaseNameSerializer(), new ExtensionSerializer() );
  }

}
