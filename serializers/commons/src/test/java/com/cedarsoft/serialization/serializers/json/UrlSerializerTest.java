package com.cedarsoft.serialization.serializers.json;

import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import org.junit.experimental.theories.DataPoint;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlSerializerTest extends AbstractJsonSerializerTest2<URL> {
  @DataPoint
  public static Entry<? extends URL> entry1() throws MalformedURLException {
    return UrlSerializerTest.create( new URL( "http://localhost:351/asdf" ), UrlSerializerTest.class.getResource( "Url_1.0.0_1.json" ) );
  }

  @Nonnull
  @Override
  protected Serializer<URL> getSerializer() throws Exception {
    return new UrlSerializer();
  }

}
