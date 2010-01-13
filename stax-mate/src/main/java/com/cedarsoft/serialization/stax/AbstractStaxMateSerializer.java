package com.cedarsoft.serialization.stax;

import com.cedarsoft.VersionRange;
import org.codehaus.staxmate.out.SMNamespace;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract base class for stax mate based serializers
 *
 * @param <T> the type
 */
public abstract class AbstractStaxMateSerializer<T> extends AbstractStaxBasedSerializer<T, SMOutputElement> {
  protected AbstractStaxMateSerializer( @NotNull @NonNls String defaultElementName, @NonNls @NotNull String nameSpaceUriBase, @NotNull VersionRange formatVersionRange ) {
    super( defaultElementName, nameSpaceUriBase, formatVersionRange );
  }

  @Override
  public void serialize( @NotNull T object, @NotNull OutputStream out ) throws IOException {
    try {
      SMOutputDocument doc = StaxMateSupport.getSmOutputFactory().createOutputDocument( out );

      String nameSpaceUri = createNameSpaceUri( getFormatVersion() );
      SMNamespace nameSpace = doc.getNamespace( nameSpaceUri );

      SMOutputElement root = doc.addElement( nameSpace, getDefaultElementName() );
      serialize( root, object );
      doc.closeRoot();
    } catch ( XMLStreamException e ) {
      throw new IOException( e );
    }
  }
}
