import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SetterSerializer extends com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer<Setter> {
    public static final String PROPERTY_FOO = "foo";

    @javax.inject.Inject
    public SetterSerializer(@NotNull StringSerializer stringSerializer) {
        super("setter", "http://cedarsoft.com/serialization/Setter", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@NotNull org.codehaus.staxmate.out.SMOutputElement serializeTo, @NotNull Setter object, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, javax.xml.stream.XMLStreamException {
        verifyVersionWritable(formatVersion);
        serialize(object.getFoo(), String.class, PROPERTY_FOO, serializeTo, formatVersion);
    }

    @Override
    @NotNull
    public Setter deserialize(@NotNull javax.xml.stream.XMLStreamReader deserializeFrom, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, javax.xml.stream.XMLStreamException {
        verifyVersionWritable(formatVersion);

        nextTag(deserializeFrom, PROPERTY_FOO);
        String foo = deserialize(String.class, formatVersion, deserializeFrom);
        Setter object = new Setter();
        object.setFoo(foo);
        return object;
    }
}