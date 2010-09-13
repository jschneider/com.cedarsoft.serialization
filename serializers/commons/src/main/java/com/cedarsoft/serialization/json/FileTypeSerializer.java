package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.file.Extension;
import com.cedarsoft.file.FileType;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class FileTypeSerializer extends AbstractJacksonSerializer<FileType> {
  @NonNls
  public static final String PROPERTY_EXTENSIONS = "extensions";
  @NonNls
  public static final String PROPERTY_ID = "id";
  @NonNls
  public static final String PROPERTY_DEPENDENTTYPE = "dependentType";
  @NonNls
  public static final String PROPERTY_CONTENTTYPE = "contentType";

  public FileTypeSerializer( @NotNull ExtensionSerializer extensionSerializer ) {
    super( "http://cedarsoft.com/file/file-type", VersionRange.from( 1, 0, 0 ).to( 1, 0, 0 ) );
    add( extensionSerializer ).responsibleFor( Extension.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @NotNull JsonGenerator serializeTo, @NotNull FileType object, @NotNull Version formatVersion )
    throws IOException, JsonProcessingException {
    verifyVersionReadable( formatVersion );
    //extensions
    serializeArray( object.getExtensions(), Extension.class, PROPERTY_EXTENSIONS, serializeTo, formatVersion );
    //id
    serializeTo.writeStringField( PROPERTY_ID, object.getId() );
    //dependentType
    serializeTo.writeBooleanField( PROPERTY_DEPENDENTTYPE, object.isDependentType() );
    //contentType
    serializeTo.writeStringField( PROPERTY_CONTENTTYPE, object.getContentType() );
  }

  @NotNull
  @Override
  public FileType deserialize( @NotNull JsonParser deserializeFrom, @NotNull Version formatVersion )
    throws VersionException, IOException, JsonProcessingException {
    //extensions
    List<? extends Extension> extensions = deserializeArray( Extension.class, PROPERTY_EXTENSIONS, deserializeFrom, formatVersion );
    //id
    nextField( deserializeFrom, PROPERTY_ID );
    String id = deserializeFrom.getText();
    //dependentType
    nextField( deserializeFrom, PROPERTY_DEPENDENTTYPE );
    boolean dependentType = deserializeFrom.getBooleanValue();
    //contentType
    nextField( deserializeFrom, PROPERTY_CONTENTTYPE );
    String contentType = deserializeFrom.getText();
    //Finally closing element
    closeObject( deserializeFrom );
    //Constructing the deserialized object
    return new FileType( id, contentType, dependentType, extensions );
  }

}
