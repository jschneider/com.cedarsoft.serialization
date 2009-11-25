package com.cedarsoft.serialization.jdom;

import com.cedarsoft.Version;
import com.cedarsoft.VersionMismatchException;
import com.cedarsoft.VersionRange;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

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
  public void testIt() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.serialize( 7, out );

    assertEquals( out.toString().trim(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<?format 1.2.3?>\n" +
      "<my>7</my>" );


    ByteArrayInputStream in = new ByteArrayInputStream( ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<?format 1.2.3?>\n" +
      "<my>7</my>" ).getBytes() );

    assertEquals( serializer.deserialize( in ), new Integer( 7 ) );
  }

  @Test
  public void testWrongVersion() throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream( ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<?format 1.0.2?>\n" +
      "<my>7</my>" ).getBytes() );

    try {
      serializer.deserialize( in );
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException ignore ) {
    }
  }

  @Test
  public void testVersionRange() throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream( ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<?format 1.2.1?>\n" +
      "<my>7</my>" ).getBytes() );

    try {
      serializer.deserialize( in );
      fail( "Where is the Exception" );
    } catch ( VersionMismatchException ignore ) {
    }
  }

  @Test
  public void testNoVersion() throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream( ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<my>7</my>" ).getBytes() );

    try {
      serializer.deserialize( in );
      fail( "Where is the Exception" );
    } catch ( IllegalStateException ignore ) {
    }
  }

  public static class MySerializer extends AbstractJDomSerializer<Integer> {
    public MySerializer() {
      super( "my", new VersionRange( new Version( 1, 2, 1 ), new Version( 1, 2, 3 ) ) );
    }

    @NotNull
    @Override
    public Element serialize( @NotNull Element serializeTo, @NotNull Integer object ) throws IOException, IOException {
      serializeTo.setText( String.valueOf( object ) );
      return serializeTo;
    }

    @NotNull
    @Override
    public Integer deserialize( @NotNull Element deserializeFrom ) throws IOException, IOException {
      return Integer.parseInt( deserializeFrom.getText() );
    }
  }
}
