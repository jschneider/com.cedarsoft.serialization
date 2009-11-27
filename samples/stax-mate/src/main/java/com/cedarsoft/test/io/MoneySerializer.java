package com.cedarsoft.test.io;

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
    super( "money", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
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
  public Money deserialize( @NotNull XMLStreamReader deserializeFrom ) throws IOException, XMLStreamException {
    int cents = Integer.parseInt( deserializeFrom.getAttributeValue( null, "cents" ) );

    //We have to close the tag! Every stax based serializer has to close its tag
    //getText does this automatically for us. But when only using attributes, we have to close it manually.
    closeTag( deserializeFrom );

    return new Money( cents );
  }
}
