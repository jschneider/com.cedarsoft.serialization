package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.Entry;
import com.cedarsoft.serialization.SerializingStrategy;
import com.cedarsoft.serialization.ToString;
import com.cedarsoft.serialization.ui.VersionMappingsVisualizer;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.experimental.theories.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.junit.Assert.*;

/**
 *
 */
public class BallSerializerTest extends AbstractXmlSerializerTest2<Ball> {
  @NotNull
  @Override
  protected BallSerializer getSerializer() throws Exception {
    return new BallSerializer();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create(
    new TennisBall( 7 ), "<ball type=\"tennisBall\" id=\"7\" />" );

  @DataPoint
  public static final Entry<?> ENTRY2 = create(
    new BasketBall( "asdf" ), "<ball type=\"basketBall\" theId=\"asdf\"></ball>" );


  @Test
  public void testAsccii() throws Exception {
    assertEquals( 2, getSerializer().getSerializingStrategySupport().getVersionMappings().getMappings().size() );
    assertEquals( "         -->  basketBa  tennisBa\n" +
      "--------------------------------\n" +
      "   1.0.0 -->     2.0.0     1.5.0\n" +
      "   1.1.0 -->     2.0.1     1.5.1\n" +
      "--------------------------------\n", VersionMappingsVisualizer.toString( getSerializer().getSerializingStrategySupport().getVersionMappings(), new ToString<SerializingStrategy<? extends Ball, SMOutputElement, XMLStreamReader, XMLStreamException>>() {
      @NotNull
      @Override
      public String convert( @NotNull SerializingStrategy<? extends Ball, SMOutputElement, XMLStreamReader, XMLStreamException> object ) {
        return object.getId();
      }
    } ) );
  }

  @Test
  public void testVersion() throws Exception {
    BallSerializer serializer = getSerializer();
    assertTrue( serializer.isVersionReadable( Version.valueOf( 1, 0, 0 ) ) );
    assertFalse( serializer.isVersionReadable( Version.valueOf( 1, 2, 1 ) ) );
    assertFalse( serializer.isVersionReadable( Version.valueOf( 0, 9, 9 ) ) );

    assertTrue( serializer.isVersionWritable( Version.valueOf( 1, 1, 0 ) ) );
    assertFalse( serializer.isVersionWritable( Version.valueOf( 1, 1, 1 ) ) );
    assertFalse( serializer.isVersionWritable( Version.valueOf( 1, 0, 9 ) ) );
  }
}
