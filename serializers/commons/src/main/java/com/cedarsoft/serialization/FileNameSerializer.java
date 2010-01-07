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
  private static final Version VERSION_EXTENSION_SERIALIZER = new Version( 1, 0, 0 );
  @NotNull
  private static final Version VERSION_BASE_NAME_SERIALIZER = new Version( 1, 0, 0 );
  @NotNull
  @NonNls
  public static final String ELEMENT_EXTENSION = "extension";

  @NotNull
  @NonNls
  public static final String ELEMENT_BASE_NAME = "baseName";
  @NotNull
  private final ExtensionSerializer extensionSerializer;
  @NotNull
  private final BaseNameSerializer baseNameSerializer;

  @Inject
  public FileNameSerializer( @NotNull BaseNameSerializer baseNameSerializer, @NotNull ExtensionSerializer extensionSerializer ) {
    super( "fileName", new VersionRange( VERSION_EXTENSION_SERIALIZER, VERSION_EXTENSION_SERIALIZER ) );
    this.extensionSerializer = extensionSerializer;
    this.baseNameSerializer = baseNameSerializer;

    verifyDelegatingSerializerVersion( extensionSerializer, VERSION_EXTENSION_SERIALIZER );
    verifyDelegatingSerializerVersion( baseNameSerializer, VERSION_BASE_NAME_SERIALIZER );
  }

  @Override
  public void serialize( @NotNull SMOutputElement serializeTo, @NotNull FileName object ) throws IOException, XMLStreamException {
    baseNameSerializer.serialize( serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_BASE_NAME ), object.getBaseName() );
    extensionSerializer.serialize( serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_EXTENSION ), object.getExtension() );
  }

  @NotNull
  @Override
  public FileName deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, XMLStreamException {
    nextTag( deserializeFrom, ELEMENT_BASE_NAME );
    BaseName baseName = baseNameSerializer.deserialize( deserializeFrom, VERSION_BASE_NAME_SERIALIZER );

    nextTag( deserializeFrom, ELEMENT_EXTENSION );
    Extension extension = extensionSerializer.deserialize( deserializeFrom, VERSION_EXTENSION_SERIALIZER );

    closeTag( deserializeFrom );

    return new FileName( baseName, extension );
  }
}
