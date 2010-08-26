package com.cedarsoft.serialization.stax;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.junit.*;

import javax.xml.stream.XMLStreamReader;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class StaxMateSupportTest {
  @Before
  public void setup() {
    clear();
  }

  @After
  public void testDown() {
    clear();
  }

  private void clear() {
    StaxMateSupport.clear();
  }

  @Test
  public void testEventNames() {
    assertEquals( "START_ELEMENT", StaxSupport.getEventName( XMLStreamReader.START_ELEMENT ) );
    assertEquals( "DTD", StaxSupport.getEventName( XMLStreamReader.DTD ) );
    assertEquals( "END_ELEMENT", StaxSupport.getEventName( XMLStreamReader.END_ELEMENT ) );
    assertEquals( "6151351", StaxSupport.getEventName( 6151351 ) );
  }

  @Test
  public void testFactories() {
    assertEquals( WstxInputFactory.class, StaxSupport.getXmlInputFactory().getClass() );
    assertEquals( WstxOutputFactory.class, StaxSupport.getXmlOutputFactory().getClass() );

    StaxSupport.XML_INPUT_FACTORY.set( new MappedXMLInputFactory( new Configuration() ) );
    assertEquals( MappedXMLInputFactory.class, StaxSupport.getXmlInputFactory().getClass() );

    StaxSupport.XML_OUTPUT_FACTORY.set( new MappedXMLOutputFactory( new Configuration() ) );
    assertEquals( MappedXMLOutputFactory.class, StaxSupport.getXmlOutputFactory().getClass() );
  }

  @Test
  public void testFactories2() {
    assertEquals( WstxInputFactory.class, StaxSupport.getXmlInputFactory().getClass() );
    assertEquals( WstxOutputFactory.class, StaxSupport.getXmlOutputFactory().getClass() );

    StaxSupport.XML_INPUT_FACTORY.set( new MappedXMLInputFactory( new Configuration() ) );
    assertEquals( MappedXMLInputFactory.class, StaxSupport.getXmlInputFactory().getClass() );

    StaxSupport.XML_OUTPUT_FACTORY.set( new MappedXMLOutputFactory( new Configuration() ) );
    assertEquals( MappedXMLOutputFactory.class, StaxSupport.getXmlOutputFactory().getClass() );
  }

  @Test
  public void testStaxMate() throws Exception {
    assertEquals( SMInputFactory.class, StaxMateSupport.getSmInputFactory().getClass() );
    assertEquals( SMOutputFactory.class, StaxMateSupport.getSmOutputFactory().getClass() );

    assertEquals( WstxInputFactory.class, StaxMateSupport.getSmInputFactory().getStaxFactory().getClass() );
    assertEquals( WstxOutputFactory.class, StaxMateSupport.getSmOutputFactory().getStaxFactory().getClass() );
  }

  @Test
  public void testStaxMateChangeAfter() throws Exception {
    assertEquals( WstxInputFactory.class, StaxMateSupport.getSmInputFactory().getStaxFactory().getClass() );
    assertEquals( WstxOutputFactory.class, StaxMateSupport.getSmOutputFactory().getStaxFactory().getClass() );

    StaxSupport.XML_INPUT_FACTORY.set( new MappedXMLInputFactory( new Configuration() ) );
    assertEquals( MappedXMLInputFactory.class, StaxSupport.getXmlInputFactory().getClass() );

    StaxSupport.XML_OUTPUT_FACTORY.set( new MappedXMLOutputFactory( new Configuration() ) );
    assertEquals( MappedXMLOutputFactory.class, StaxSupport.getXmlOutputFactory().getClass() );

    assertEquals( WstxInputFactory.class, StaxMateSupport.getSmInputFactory().getStaxFactory().getClass() );
    assertEquals( WstxOutputFactory.class, StaxMateSupport.getSmOutputFactory().getStaxFactory().getClass() );
  }

  @Test
  public void testStaxMateChangeBefore() throws Exception {
    StaxSupport.XML_INPUT_FACTORY.set( new MappedXMLInputFactory( new Configuration() ) );
    StaxSupport.XML_OUTPUT_FACTORY.set( new MappedXMLOutputFactory( new Configuration() ) );

    assertEquals( MappedXMLInputFactory.class, StaxMateSupport.getSmInputFactory().getStaxFactory().getClass() );
    assertEquals( MappedXMLOutputFactory.class, StaxMateSupport.getSmOutputFactory().getStaxFactory().getClass() );
  }
}
