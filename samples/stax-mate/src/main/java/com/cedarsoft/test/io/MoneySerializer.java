package com.cedarsoft.test.io;

import com.cedarsoft.UnsupportedVersionException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.test.Money;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class MoneySerializer extends AbstractStaxMateSerializer<Money> {
  public MoneySerializer() {
    //This serializer supports an old version, too
    super( "money", new VersionRange( new Version( 0, 9, 9 ), new Version( 1, 0, 0 ) ) );
  }

  @NotNull
  @Override
  public SMOutputElement serialize( @NotNull SMOutputElement serializeTo, @NotNull Money object ) throws IOException, XMLStreamException {
    serializeTo.addAttribute( "cents", String.valueOf( object.getCents() ) );
    //We use an attribute - just because it is possible...
    return serializeTo;
  }

  @NotNull
  @Override
  public Money deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
    //This serializer supports reading of an old format. Therefore we have to switch based on the format version.
    //This might be solved using the strategy pattern. But in most of the cases the format changes only in small portions.
    //So it seems easier to add just one if/else.
    //If a serializer evolves further a switch to the strategy pattern might be done. A simple map holding the strategies should do it.


    //The common case - current version
    if ( formatVersion.equals( new Version( 1, 0, 0 ) ) ) {
      int cents = Integer.parseInt( deserializeFrom.getAttributeValue( null, "cents" ) );

      //We have to close the tag! Every stax based serializer has to close its tag
      //getText does this automatically for us. But when only using attributes, we have to close it manually.
      closeTag( deserializeFrom );

      return new Money( cents );

      //The old format that does not use an attribute but text instead
    } else if ( formatVersion.equals( new Version( 0, 9, 9 ) ) ) {
      int cents = Integer.parseInt( getText( deserializeFrom ) );

      //We don't have to close the tag. The getText method does that for us
      return new Money( cents );

      //Whoo - something went terribly wrong
    } else {
      throw new UnsupportedVersionException( formatVersion, getFormatVersionRange() );
    }
  }
}
