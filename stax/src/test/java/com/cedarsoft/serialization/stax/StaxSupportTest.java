package com.cedarsoft.serialization.stax;

import org.testng.annotations.*;

import javax.xml.stream.XMLStreamReader;

import static org.testng.Assert.*;

/**
 *
 */
public class StaxSupportTest {
  @Test
  public void testEventNames() {
    assertEquals( StaxSupport.getEventName( XMLStreamReader.START_ELEMENT ), "START_ELEMENT" );
    assertEquals( StaxSupport.getEventName( XMLStreamReader.DTD ), "DTD" );
    assertEquals( StaxSupport.getEventName( XMLStreamReader.END_ELEMENT ), "END_ELEMENT" );
    assertEquals( StaxSupport.getEventName( 6151351 ), "6151351" );
  }
}
