package unit.basic;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

public class DaDomainObjectSerializer
  extends AbstractStaxMateSerializer<DaDomainObject> {

  public final static String ELEMENT_DESCRIPTION = "description";
  public final static String ATTRIBUTE_ID = "id";

  public DaDomainObjectSerializer() {
    super( "dadomainobject", "http://basic.unit/da-domain-object", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
  }

  @Override
  public void serialize( SMOutputElement serializeTo, DaDomainObject object, @Nonnull Version formatVersion )
    throws IOException, XMLStreamException {
    assert isVersionWritable( formatVersion );

    //description
    serializeTo.addElementWithCharacters( serializeTo.getNamespace(), ELEMENT_DESCRIPTION, object.getDescription() );
    //id
    serializeTo.addAttribute( ATTRIBUTE_ID, String.valueOf( object.getId() ) );
  }

  @Override
  public DaDomainObject deserialize( XMLStreamReader deserializeFrom, Version formatVersion )
    throws VersionException, IOException, XMLStreamException {
    assert isVersionReadable( formatVersion );
    //description
    String description = getChildText( deserializeFrom, ELEMENT_DESCRIPTION );
    //id
    int id = Integer.parseInt( deserializeFrom.getAttributeValue( null, ATTRIBUTE_ID ) );
    //Finally closing element
    closeTag( deserializeFrom );
    //Constructing the deserialized object
    DaDomainObject object = new DaDomainObject( description, id );
    return object;
  }

}
