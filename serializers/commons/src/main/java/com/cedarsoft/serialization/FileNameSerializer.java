package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.file.BaseName;
import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileName;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import com.google.inject.Inject;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 * Serializer for file names
 */
public class FileNameSerializer extends AbstractStaxMateSerializer<FileName> {
  @NotNull
  @NonNls
  public static final String ELEMENT_EXTENSION = "extension";
  @NotNull
  @NonNls
  public static final String ELEMENT_BASE_NAME = "baseName";

  @Inject
  public FileNameSerializer( @NotNull BaseNameSerializer baseNameSerializer, @NotNull ExtensionSerializer extensionSerializer ) {
    super( "fileName", "http://www.cedarsoft.com/file/fileName", VersionRange.from( 1, 0, 0 ).to() );

    add( extensionSerializer ).responsibleFor( Extension.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 )
      ;

    add( baseNameSerializer ).responsibleFor( BaseName.class )
      .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 )
      ;

    getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull FileName object ) throws IOException, XMLStreamException {
    serialize( BaseName.class, serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_BASE_NAME ), object.getBaseName() );
    serialize( Extension.class, serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_EXTENSION ), object.getExtension() );
  }

  @NotNull
  @Override
  public FileName deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
    nextTag( deserializeFrom, ELEMENT_BASE_NAME );
    BaseName baseName = deserialize( BaseName.class, formatVersion, deserializeFrom );

    nextTag( deserializeFrom, ELEMENT_EXTENSION );
    Extension extension = deserialize( Extension.class, formatVersion, deserializeFrom );

    closeTag( deserializeFrom );

    return new FileName( baseName, extension );
  }
}
