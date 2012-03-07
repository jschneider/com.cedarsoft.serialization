package com.cedarsoft.serialization.stax.mate;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.staxmate.out.SMOutputElement;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class CollectionSerializer<T> extends AbstractStaxMateSerializer<List<? extends T>> {
  @Nonnull
  private final Class<T> type;

  protected CollectionSerializer(@Nonnull Class<T> type, @Nonnull AbstractStaxMateSerializer<T> serializer) {
    this(type, serializer, serializer.getDefaultElementName() + "s", serializer.getNameSpaceBase() + "s", serializer.getFormatVersionRange());
  }

  public CollectionSerializer(@Nonnull Class<T> type, @Nonnull AbstractStaxMateSerializer<T> serializer, @Nonnull String defaultElementName, @Nonnull String nameSpaceUriBase, @Nonnull VersionRange formatVersionRange) {
    super(defaultElementName, nameSpaceUriBase, formatVersionRange);
    this.type = type;

    add(serializer).responsibleFor(type).map(formatVersionRange.getMax()).toDelegateVersion(serializer.getFormatVersion());
    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize(@Nonnull SMOutputElement serializeTo, @Nonnull List<? extends T> object, @Nonnull Version formatVersion) throws IOException, VersionException, XMLStreamException {
    verifyVersionWritable(formatVersion);

    serializeCollection(object, type, serializeTo, formatVersion);
  }

  @Nonnull
  @Override
  public List<? extends T> deserialize(@Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion) throws IOException, VersionException, XMLStreamException {
    verifyVersionReadable(formatVersion);

    return deserializeCollection(deserializeFrom, type, formatVersion);
  }
}
