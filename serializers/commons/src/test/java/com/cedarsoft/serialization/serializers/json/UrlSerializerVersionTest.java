package com.cedarsoft.serialization.serializers.json;

import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.test.utils.VersionEntry;
import com.cedarsoft.version.Version;
import org.junit.experimental.theories.DataPoint;

import javax.annotation.Nonnull;
import java.net.URL;

import static org.fest.assertions.Assertions.assertThat;

public class UrlSerializerVersionTest extends AbstractJsonVersionTest2<URL> {
  @DataPoint
  public static final VersionEntry ENTRY1 = UrlSerializerVersionTest.create( Version.valueOf( 1, 0, 0 ), UrlSerializerVersionTest.class.getResource( "Url_1.0.0_1.json" ) );

  @Nonnull
  @Override
  protected Serializer<URL> getSerializer() throws Exception {
    return new UrlSerializer();
  }

  @Override
  protected void verifyDeserialized( @Nonnull URL deserialized, @Nonnull Version version ) throws Exception {
    assertThat( deserialized ).isEqualTo( new URL( "http://localhost:351/asdf" ) );
  }
}
