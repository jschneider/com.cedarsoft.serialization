package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.app.Application;
import com.cedarsoft.serialization.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.junit.experimental.theories.*;

public class ApplicationSerializerTest extends AbstractJsonSerializerTest2<Application> {

  @DataPoint
  public static final Entry<? extends Application> ENTRY1 = ApplicationSerializerTest.create(
    new Application( "name", Version.valueOf( 1, 2, 3 ) ), ApplicationSerializerTest.class.getResource( "Application_1.0.0_1.json" ) );

  @Override
  protected Serializer<Application> getSerializer() throws Exception {
    return new ApplicationSerializer( new VersionSerializer() );
  }

}
