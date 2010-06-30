package com.cedarsoft.serialization.demo2;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
*
*/
public class MoneyConverter implements Converter {
  @Override
  public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
    writer.startNode( "cents" );
    writer.setValue( String.valueOf( ( ( Money ) source ).getCents() ) );
    writer.endNode();
  }

  @Override
  public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
    reader.moveDown();
    long cents;
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

  @Override
  public boolean canConvert( Class type ) {
    return type.equals( Money.class );
  }
}
