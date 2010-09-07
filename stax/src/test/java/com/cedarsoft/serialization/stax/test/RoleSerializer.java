package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxSerializer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class RoleSerializer extends AbstractStaxSerializer<Role> {
  @NonNls
  public static final String DEFAULT_ELEMENT_NAME = "role";

  public RoleSerializer() {
    super( DEFAULT_ELEMENT_NAME, "http://test/role", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( @NotNull XMLStreamWriter serializeTo, @NotNull Role object, @NotNull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    serializeTo.writeAttribute( "id", String.valueOf( object.getId() ) );
    serializeTo.writeCharacters( object.getDescription() );
  }

  @NotNull
  @Override
  public Role deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
    int id = Integer.parseInt( deserializeFrom.getAttributeValue( null, "id" ) );
    return new Role( id, getText( deserializeFrom ) );
  }
}
