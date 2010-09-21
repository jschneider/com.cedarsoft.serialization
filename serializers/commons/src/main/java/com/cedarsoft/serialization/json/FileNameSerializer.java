package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.file.BaseName;
import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileName;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class FileNameSerializer extends AbstractJacksonSerializer<FileName> {
  @NonNls
  public static final String PROPERTY_BASENAME = "baseName";
  @NonNls
  public static final String PROPERTY_EXTENSION = "extension";

  public FileNameSerializer( @NotNull BaseNameSerializer baseNameSerializer, @NotNull ExtensionSerializer extensionSerializer ) {
    super( "file-name", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
    add( baseNameSerializer ).responsibleFor( BaseName.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    add( extensionSerializer ).responsibleFor( Extension.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull FileName object, @NotNull Version formatVersion ) throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );
    //baseName
    serialize( object.getBaseName(), BaseName.class, PROPERTY_BASENAME, serializeTo, formatVersion );
    //extension
    serialize( object.getExtension(), Extension.class, PROPERTY_EXTENSION, serializeTo, formatVersion );
  }

  @Override
  public FileName deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion ) throws VersionException, IOException, JsonProcessingException {
    //baseName
    BaseName baseName = deserialize( BaseName.class, PROPERTY_BASENAME, formatVersion, deserializeFrom );
    //extension
    Extension extension = deserialize( Extension.class, PROPERTY_EXTENSION, formatVersion, deserializeFrom );
    //Finally closing element
    closeObject( deserializeFrom );
    //Constructing the deserialized object
    return new FileName( baseName, extension );
  }
}
