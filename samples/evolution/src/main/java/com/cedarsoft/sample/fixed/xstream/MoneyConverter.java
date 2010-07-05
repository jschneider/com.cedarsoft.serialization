package com.cedarsoft.sample.fixed.xstream;

import com.cedarsoft.sample.fixed.Money;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *
 */
//START SNIPPET: marshal
public class MoneyConverter implements Converter {
  @Override
  public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
    writer.startNode( "cents" );
    writer.setValue( String.valueOf( ( ( Money ) source ).getCents() ) );
    writer.endNode();
  }
  //END SNIPPET: marshal

  //START SNIPPET: unmarshal

  @Override
  public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
    reader.moveDown();
    long cents;

    //We have to guess which kind of XML we have
    //This might become very difficult and complicated for complex scenarios
    if ( reader.getNodeName().equals( "amount" ) ) {
      //Legacy!
      cents = Money.convertValueToCents( Double.parseDouble( reader.getValue() ) );
    } else {
      cents = Long.parseLong( reader.getValue() );
    }
    reader.getValue();
    reader.moveUp();

    return new Money( cents );
  }
  //END SNIPPET: unmarshal

  @Override
  public boolean canConvert( Class type ) {
    return type.equals( Money.class );
  }
}
