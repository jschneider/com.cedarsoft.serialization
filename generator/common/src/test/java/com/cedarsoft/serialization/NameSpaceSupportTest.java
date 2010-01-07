package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import org.testng.annotations.*;

import static org.testng.AssertJUnit.assertEquals;

/**
 *
 */
public class NameSpaceSupportTest {
  @Test
  public void testToUri() {
    assertEquals( NameSpaceSupport.createNameSpaceUriBase( XmlSerializerNameSpaceTest.class ), "http://www.cedarsoft.com/serialization/XmlSerializerNameSpaceTest" );
    assertEquals( NameSpaceSupport.createNameSpaceUriBase( String.class ), "http://www.lang.java/String" );
  }
}
