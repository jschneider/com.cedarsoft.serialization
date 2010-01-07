package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

/**
 *
 */
public class XmlSerializerNameSpaceTest {
  @Test
  public void testVersion() {
    assertEquals( new Version( 1, 0, 0, "b4" ).format(), "1.0.0-b4" );
  }

  @Test
  public void testParseVersion() {
    assertEquals( AbstractXmlSerializer.parseVersionFromNamespaceUri( "http://www.cedarsoft.com/serialization/XmlSerializerNameSpaceTest/1.0.0" ), new Version( 1, 0, 0 ) );
    assertEquals( AbstractXmlSerializer.parseVersionFromNamespaceUri( "http://www.lang.java/String/2.5.1" ), new Version( 2, 5, 1 ) );
    assertEquals( AbstractXmlSerializer.parseVersionFromNamespaceUri( "http://www.lang.java/String/2.5.1-asdf" ), new Version( 2, 5, 1, "asdf" ) );
  }
}
