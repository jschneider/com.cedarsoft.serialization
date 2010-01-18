package com.cedarsoft.test.io;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.cedarsoft.test.Model;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 * This is a very simple serializer that serializes a {@link Model}.
 * <p>
 * Results in:<br/>
 * <code>
 * &lt;model&gt;Toyota&lt;/model&gt;</code><br/>
 * for<br/>
 * <code>new Model("Toyota")</code>
 */
public class ModelSerializer extends AbstractStaxMateSerializer<Model> {
  //START SNIPPET: constructor

  public ModelSerializer() {
    super( "model", "http://thecompany.com/test/model", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
  }
  //END SNIPPET: constructor


  //START SNIPPET: serialize

  @Override
  public void serialize( SMOutputElement serializeTo, Model object ) throws IOException, XMLStreamException {
    serializeTo.addCharacters( object.getName() );
  }
  //END SNIPPET: serialize


  //START SNIPPET: deserialize

  @Override
  public Model deserialize( XMLStreamReader deserializeFrom, Version formatVersion ) throws IOException, XMLStreamException {
    return new Model( getText( deserializeFrom ) );
    //getText automatically closes the tag
  }
  //START SNIPPET: deserialize
}
