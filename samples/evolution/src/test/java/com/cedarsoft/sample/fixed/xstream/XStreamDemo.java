package com.cedarsoft.sample.fixed.xstream;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.sample.fixed.Car;
import com.cedarsoft.sample.fixed.Extra;
import com.cedarsoft.sample.fixed.Model;
import com.cedarsoft.sample.fixed.Money;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import org.junit.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


/**
 *
 */
public class XStreamDemo {
  public Car createSampleCar() {
    Model model = new Model( "Ford" );
    Extra extra0 = new Extra( "Whoo effect", new Money( 99.98 ) );
    Extra extra1 = new Extra( "Better Whoo effect", new Money( 199.00 ) );
    List<Extra> extras = Arrays.asList( extra0, extra1 );
    return new Car( model, new Money( 19000.00 ), extras );
  }

  @Test
  public void testSerialization() throws IOException, SAXException {
    XStream xStream = new XStream();
    //We define some aliases to get a nicer xml output without fqns
    xStream.alias( "car", Car.class );
    xStream.alias( "extra", Extra.class );
    xStream.alias( "money", Money.class );

    String xml = xStream.toXML( createSampleCar() );
    AssertUtils.assertXMLEquals( getClass().getResource( "car.xml" ), xml );
  }

  @Test
  public void testDeserialize() {
    XStream xStream = new XStream();
    //We define some aliases to get a nicer xml output without fqns
    xStream.alias( "car", Car.class );
    xStream.alias( "extra", Extra.class );
    xStream.alias( "money", Money.class );

    Car deserialized = ( Car ) xStream.fromXML( getClass().getResourceAsStream( "car.xml" ) );
    assertEquals( createSampleCar().getBasePrice(), deserialized.getBasePrice() );
  }

  @Test
  public void testDeserializeOld() {
    XStream xStream = new XStream();
    //We define some aliases to get a nicer xml output without fqns
    xStream.alias( "car", Car.class );
    xStream.alias( "extra", Extra.class );
    xStream.alias( "money", Money.class );

    try {
      xStream.fromXML( getClass().getResourceAsStream( "../../xstream/car.xml" ) );
      fail( "Where is the Exception" );
    } catch ( ConversionException e ) {
      assertEquals( e.getMessage().trim(), "amount : amount : amount : amount\n" +
        "---- Debugging information ----\n" +
        "message             : amount : amount\n" +
        "cause-exception     : com.thoughtworks.xstream.mapper.CannotResolveClassException\n" +
        "cause-message       : amount : amount\n" +
        "class               : com.cedarsoft.sample.fixed.Car\n" +
        "required-type       : com.cedarsoft.sample.fixed.Money\n" +
        "path                : /car/basePrice/amount\n" +
        "line number         : 8\n" +
        "-------------------------------" );
    }
  }

  @Test
  public void testSerializationConverter() throws IOException, SAXException {
    XStream xStream = new XStream();
    //We define some aliases to get a nicer xml output without fqns
    xStream.alias( "car", Car.class );
    xStream.alias( "extra", Extra.class );
    xStream.alias( "money", Money.class );
    xStream.registerConverter( new MoneyConverter() );

    String xml = xStream.toXML( createSampleCar() );
    AssertUtils.assertXMLEquals( getClass().getResource( "car.xml" ), xml );
  }

  @Test
  public void testDeserializeOldWithConverter() {
    XStream xStream = new XStream();
    //We define some aliases to get a nicer xml output without fqns
    xStream.alias( "car", Car.class );
    xStream.alias( "extra", Extra.class );
    xStream.alias( "money", Money.class );
    xStream.registerConverter( new MoneyConverter() );

    Car deserialized = ( Car ) xStream.fromXML( getClass().getResourceAsStream( "../../xstream/car.xml" ) );
    assertEquals( deserialized.getBasePrice(), createSampleCar().getBasePrice() );
  }
}
