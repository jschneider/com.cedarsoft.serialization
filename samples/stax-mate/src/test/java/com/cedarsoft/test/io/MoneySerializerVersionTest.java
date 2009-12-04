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
 *
 */
public class MoneySerializerVersionTest {
  @Test
  public void testCurrent() throws IOException, SAXException {
    MoneySerializer serializer = new MoneySerializer();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( new Money( 7, 99 ), out );

    AssertUtils.assertXMLEqual( out.toString(), "<money cents=\"799\" />" );

    assertEquals( serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) ), new Money( 7, 99 ) );
  }

  @Test
  public void testOldFormat() throws IOException {
    MoneySerializer serializer = new MoneySerializer();
    assertEquals( serializer.deserialize( new ByteArrayInputStream(
      ( "<?format 0.9.9?>\n" +
        "<money>799</money>" ).getBytes() ) ), new Money( 7, 99 ) );
  }

  @Test
  public void testCurrentFormat() throws IOException {
    MoneySerializer serializer = new MoneySerializer();
    assertEquals( serializer.deserialize( new ByteArrayInputStream(
      ( "<?format 1.0.0?>\n" +
        "<money cents=\"799\" />" ).getBytes() ) ), new Money( 7, 99 ) );
  }
}
