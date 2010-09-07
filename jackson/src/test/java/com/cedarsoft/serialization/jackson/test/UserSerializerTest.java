package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.serialization.AbstractSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

import java.util.Arrays;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserSerializerTest extends AbstractSerializerTest2<User> {
  @Override
  protected void verifySerialized( @NotNull Entry<User> entry, @NotNull byte[] serialized ) throws Exception {
    AssertUtils.assertJsonEquals( new String( entry.getExpected() ), new String( serialized ) );
  }

  @NotNull
  @Override
  protected Serializer<User> getSerializer() throws Exception {
    return new UserSerializer( new EmailSerializer(), new RoleSerializer() );
  }

  @DataPoint
  public static Entry<?> json() {
    return create( new User( "Max Mustermann",
                             Arrays.asList(
                               new Email( "test@test.de" ),
                               new Email( "other@test.de" )
                             ),
                             Arrays.asList(
                               new Role( 1, "Nobody" ),
                               new Role( 0, "Admin" )
                             )
    ), UserSerializerTest.class.getResource( "user.json" ) );
  }
}
