package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.serialization.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class UserSerializerTest extends AbstractXmlSerializerTest2<User> {
  @NotNull
  @Override
  protected Serializer<User> getSerializer() throws Exception {
    return new UserSerializer( new RoleSerializer(), new EmailSerializer() );
  }

  @Override
  protected void verifyDeserialized( @NotNull User deserialized, @NotNull User original ) {
    assertEquals( 2, deserialized.getEmails().size() );
    assertEquals( 2, deserialized.getRoles().size() );

    super.verifyDeserialized( deserialized, original );
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( new User( "Markus Mustermann",
                                                          Arrays.<Email>asList(
                                                            new Email( "test@test.de" ),
                                                            new Email( "other@test.de" )
                                                          ),
                                                          Arrays.asList(
                                                            new Role( 1, "nobody" ),
                                                            new Role( 2, "othergroup" )
                                                          )
  ), UserSerializerTest.class.getResource( "user.xml" ) );
}
