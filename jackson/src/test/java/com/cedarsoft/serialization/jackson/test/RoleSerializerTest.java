package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.serialization.AbstractSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class RoleSerializerTest extends AbstractSerializerTest2<Role> {
  @Override
  protected void verifySerialized( @NotNull Entry<Role> entry, @NotNull byte[] serialized ) throws Exception {
    AssertUtils.assertJsonEquals( new String( entry.getExpected() ), new String( serialized ) );
  }

  @NotNull
  @Override
  protected Serializer<Role> getSerializer() throws Exception {
    return new RoleSerializer();
  }

  @DataPoint
  public static Entry<?> json() {
    return create( new Role( 7, "nobody" ), RoleSerializerTest.class.getResource( "role.json" ) );
  }
}
