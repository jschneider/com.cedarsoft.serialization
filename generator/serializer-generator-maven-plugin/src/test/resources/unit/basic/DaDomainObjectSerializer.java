
package unit.basic;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.DeserializationContext;
import com.cedarsoft.serialization.SerializationContext;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;

public class DaDomainObjectSerializer
    extends AbstractStaxMateSerializer<DaDomainObject>
{

    public final static String ELEMENT_DESCRIPTION = "description";
    public final static String ATTRIBUTE_ID = "id";

    public DaDomainObjectSerializer() {
        super("dadomainobject", "http://basic.unit/da-domain-object", VersionRange.from(1, 0, 0).to(1, 0, 0));
    }

    @Override
    public void serialize( SMOutputElement serializeTo, DaDomainObject object, SerializationContext context )
        throws IOException, XMLStreamException
    {
        //description
        serializeTo.addElementWithCharacters(serializeTo.getNamespace(), ELEMENT_DESCRIPTION, object.getDescription());
        //id
        serializeTo.addAttribute(ATTRIBUTE_ID, String.valueOf(object.getId()));
    }

    @Override
    public DaDomainObject deserialize( XMLStreamReader deserializeFrom, Version formatVersion, DeserializationContext context )
        throws VersionException, IOException, XMLStreamException
    {
        //description
        String description = getChildText(deserializeFrom, ELEMENT_DESCRIPTION);
        //id
        int id = Integer.parseInt(deserializeFrom.getAttributeValue(null, ATTRIBUTE_ID));
        //Finally closing element
        closeTag(deserializeFrom);
        //Constructing the deserialized object
        DaDomainObject object = new DaDomainObject(description, id);
        return object;
    }

}
