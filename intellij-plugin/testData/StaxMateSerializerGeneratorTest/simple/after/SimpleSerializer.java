import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SimpleSerializer extends com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer<Simple> {
    public static final String PROPERTY_FOO = "foo";

    @javax.inject.Inject
    public SimpleSerializer(@NotNull StringSerializer stringSerializer) {
        super("simple", "http://cedarsoft.com/serialization/Simple", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@NotNull org.codehaus.staxmate.out.SMOutputElement serializeTo, @NotNull Simple object, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, javax.xml.stream.XMLStreamException {
        verifyVersionWritable(formatVersion);
        serialize(object.getFoo(), String.class, PROPERTY_FOO, serializeTo, formatVersion);
    }

    @Override
    @NotNull
    public Simple deserialize(@NotNull javax.xml.stream.XMLStreamReader deserializeFrom, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, javax.xml.stream.XMLStreamException {
        verifyVersionWritable(formatVersion);

        nextTag(deserializeFrom, PROPERTY_FOO);
        String foo = deserialize(String.class, formatVersion, deserializeFrom);
        Simple object = new Simple(foo);
        return object;
    }
}