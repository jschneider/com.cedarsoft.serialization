package com.cedarsoft.test.io;

import com.cedarsoft.UnsupportedVersionException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.test.Money;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 * This is an extended version of money serializer.
 * <p/>
 * It represents the next step of the evolution of MoneySerializer.
 * This is an example for a refactoring that might happen, after the serializer has been
 * released (and shipped to thousands of customers creating millions of files).
 * <p/>
 * Therefore this serializer is able to still read the old format.
 * Writing is only supported for the latest file.
 */
public class MoneySerializer2 extends AbstractStaxMateSerializer<Money> {
  public MoneySerializer2() {
    //This serializer supports an old version, too
    super( "money", "http://thecompany.com/test/money", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 1 ) ) );
  }

  @Override
  public void serialize( SMOutputElement serializeTo, Money object ) throws IOException, XMLStreamException {
    serializeTo.addAttribute( "cents", String.valueOf( object.getCents() ) );
    //We use an attribute - just because it is possible...
  }


  @Override
  public Money deserialize( XMLStreamReader deserializeFrom, Version formatVersion ) throws IOException, XMLStreamException {
    //This serializer supports reading of an old format. Therefore we have to switch based on the format version.
    //This might be solved using the strategy pattern. But in most of the cases the format changes only in small portions.
    //So it seems easier to add just one if/else.
    //If a serializer evolves further a switch to the strategy pattern might be done. A simple map holding the strategies should do it.


    //The common case - current version
    if ( formatVersion.equals( new Version( 1, 0, 1 ) ) ) {
      int cents = Integer.parseInt( deserializeFrom.getAttributeValue( null, "cents" ) );

      //We have to close the tag! Every stax based serializer has to close its tag
      //getText does this automatically for us. But when only using attributes, we have to close it manually.
      closeTag( deserializeFrom );

      return new Money( cents );

      //The old format that does not use an attribute but text instead
    } else if ( formatVersion.equals( new Version( 1, 0, 0 ) ) ) {
      int cents = Integer.parseInt( getText( deserializeFrom ) );

      //We don't have to close the tag. The getText method does that for us
      return new Money( cents );

      //Whoo - something went terribly wrong
    } else {
      throw new UnsupportedVersionException( formatVersion, getFormatVersionRange() );
    }
  }
}