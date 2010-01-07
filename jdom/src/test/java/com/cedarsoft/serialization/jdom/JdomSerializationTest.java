package com.cedarsoft.serialization.jdom;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class JdomSerializationTest {
  private MySerializer serializer;

  @BeforeMethod
  protected void setUp() throws Exception {
    serializer = new MySerializer();
  }

  @Test
  public void testIt() throws IOException, SAXException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( 7, out );

    AssertUtils.assertXMLEqual( out.toString().trim(), "<my  xmlns=\"http://my/1.2.3\">7</my>" );


    ByteArrayInputStream in = new ByteArrayInputStream( ( "<my  xmlns=\"http://my/1.2.3\">7</my>" ).getBytes() );

    assertEquals( serializer.deserialize( in ), new Integer( 7 ) );
  }

  @Test
  public void testWrongVersion() throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream( ( "<my  xmlns=\"http://my/1.0.2\">7</my>" ).getBytes() );

    try {
      serializer.deserialize( in );
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException ignore ) {
    }
  }

  @Test
  public void testVersionRange() throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream( ( "<my xmlns=\"http://my/1.2.1\">7</my>" ).getBytes() );

    try {
      serializer.deserialize( in );
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException ignore ) {
    }
  }

  @Test
  public void testNoVersion() throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream( ( "<my>7</my>" ).getBytes() );

    try {
      serializer.deserialize( in );
      fail( "Where is the Exception" );
    } catch ( VersionException ignore ) {
    }
  }

  public static class MySerializer extends AbstractJDomSerializer<Integer> {
    public MySerializer() {
      super( "my", "http://my", new VersionRange( new Version( 1, 2, 1 ), new Version( 1, 2, 3 ) ) );
    }

    @Override
    public void serialize( @NotNull Element serializeTo, @NotNull Integer object ) throws IOException, IOException {
      serializeTo.setText( String.valueOf( object ) );
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull Element deserializeFrom, @NotNull Version formatVersion ) throws IOException, IOException {
      return Integer.parseInt( deserializeFrom.getText() );
    }
  }
}
