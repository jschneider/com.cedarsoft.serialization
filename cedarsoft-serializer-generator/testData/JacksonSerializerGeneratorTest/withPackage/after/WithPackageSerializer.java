import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class WithPackageSerializer extends com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<com.cedarsoft.test.WithPackage> {
    public static final String PROPERTY_FOO = "foo";

    @javax.inject.Inject
    public WithPackageSerializer(@NotNull StringSerializer stringSerializer) {
        super("with-package", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@NotNull com.fasterxml.jackson.core.JsonGenerator serializeTo, @NotNull com.cedarsoft.test.WithPackage object, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, com.fasterxml.jackson.core.JsonProcessingException {
        verifyVersionWritable(formatVersion);
        serialize(object.getFoo(), String.class, PROPERTY_FOO, serializeTo, formatVersion);
    }

    @Override
    @NotNull
    public com.cedarsoft.test.WithPackage deserialize(@NotNull com.fasterxml.jackson.core.JsonParser deserializeFrom, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, com.fasterxml.jackson.core.JsonProcessingException {
        verifyVersionWritable(formatVersion);

        String foo = null;

        com.cedarsoft.serialization.jackson.JacksonParserWrapper parser = new com.cedarsoft.serialization.jackson.JacksonParserWrapper(deserializeFrom);
        while (parser.nextToken() == com.fasterxml.jackson.core.JsonToken.FIELD_NAME) {
            String currentName = parser.getCurrentName();

            if (currentName.equals(PROPERTY_FOO)) {
                parser.nextToken();
                foo = deserialize(String.class, formatVersion, deserializeFrom);
                continue;
            }
            throw new IllegalStateException("Unexpected field reached <" + currentName + ">");
        }

        parser.verifyDeserialized(foo, PROPERTY_FOO);
        assert foo != null;

        parser.ensureObjectClosed();

        com.cedarsoft.test.WithPackage object = new com.cedarsoft.test.WithPackage(foo);
        return object;
    }
}