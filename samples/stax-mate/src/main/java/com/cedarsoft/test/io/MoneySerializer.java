package com.cedarsoft.test.io;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.test.Money;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
//START SNIPPET: body
public class MoneySerializer extends AbstractStaxMateSerializer<Money> {
  public MoneySerializer() {
    super( "money", "http://thecompany.com/test/money",new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
  }

  @Override
  public void serialize( SMOutputElement serializeTo, Money object ) throws IOException, XMLStreamException {
    serializeTo.addCharacters( String.valueOf( object.getCents() ) );
  }

  @Override
  public Money deserialize( XMLStreamReader deserializeFrom, Version formatVersion ) throws IOException, XMLStreamException {
    int cents = Integer.parseInt( getText( deserializeFrom ) );

    //We don't have to close the tag. The getText method does that for us
    return new Money( cents );
  }
}
//END SNIPPET: body