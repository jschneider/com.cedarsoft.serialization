package com.cedarsoft.serialization.demo1;

import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 *
 */
public class MoneyTest {
  private XStream xStream;

  @BeforeMethod
  protected void setUp() throws Exception {
    xStream = new XStream();
    xStream.alias( "money", Money.class );
    xStream.useAttributeFor( Money.class, "amount" );
  }

  @Test
  public void testXStream() {
    assertEquals( xStream.toXML( new Money( 11351.01 ) ), "<money amount=\"11351.01\"/>" );
  }

  @Test
  public void testPrecision() {
    assertEquals( xStream.toXML( new Money( ( float ) 1.01 ) ), "<money amount=\"1.0099999904632568\"/>" );
  }

  @Test
  public void testSimple() {
    assertEquals( serialize( new Money( 11351.01 ) ), "<money amount=\"11351.01\"/>" );
  }

  private static String serialize( @NotNull Money money ) {
    return "<money amount=\"" + money.getAmount() + "\"/>";
  }
}
