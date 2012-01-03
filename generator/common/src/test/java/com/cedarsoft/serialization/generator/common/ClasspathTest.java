package com.cedarsoft.serialization.generator.common;

import com.sun.tools.internal.xjc.api.util.APTClassLoader;
import org.junit.*;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com<.a>)
 */
public class ClasspathTest {
  @Test
  public void testName() throws Exception {
    assertThat( Class.forName( "com.sun.tools.xjc.api.util.APTClassLoader" ) ).isNotNull();
  }

  @Test
  public void testOther() throws Exception {
    assertThat( Class.forName( "com.sun.tools.internal.xjc.api.util.APTClassLoader" ) ).isNotNull();
  }
}
