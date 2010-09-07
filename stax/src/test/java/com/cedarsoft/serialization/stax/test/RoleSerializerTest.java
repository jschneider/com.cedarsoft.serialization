package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.serialization.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class RoleSerializerTest extends AbstractXmlSerializerTest2<Role> {
  @NotNull
  @Override
  protected Serializer<Role> getSerializer() throws Exception {
    return new RoleSerializer();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( new Role( 7, "Nobody" ), "<role id=\"7\">Nobody</role>" );
}
