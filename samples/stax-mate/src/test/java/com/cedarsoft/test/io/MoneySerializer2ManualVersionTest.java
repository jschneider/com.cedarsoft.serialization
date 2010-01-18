package com.cedarsoft.test.io;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.test.Money;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Test class that tests the old versions manually
 */
public class MoneySerializer2ManualVersionTest {
  @Test
  public void testCurrent() throws IOException, SAXException {
    MoneySerializer2 serializer = new MoneySerializer2();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( new Money( 7, 99 ), out );

    AssertUtils.assertXMLEqual( out.toString(), "<money xmlns=\"http://thecompany.com/test/money/1.0.1\" cents=\"799\" />" );

    assertEquals( serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) ), new Money( 7, 99 ) );
  }

  @Test
  public void testOldFormat() throws IOException {
    MoneySerializer2 serializer = new MoneySerializer2();
    assertEquals( serializer.deserialize( new ByteArrayInputStream( ( "<money xmlns=\"http://thecompany.com/test/money/1.0.0\">799</money>" ).getBytes() ) ), new Money( 7, 99 ) );
  }

  @Test
  public void testCurrentFormat() throws IOException {
    MoneySerializer2 serializer = new MoneySerializer2();
    assertEquals( serializer.deserialize( new ByteArrayInputStream( ( "<money xmlns=\"http://thecompany.com/test/money/1.0.1\" cents=\"799\" />" ).getBytes() ) ), new Money( 7, 99 ) );
  }
}
