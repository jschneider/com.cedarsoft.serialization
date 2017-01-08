import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class CollectionsSerializer extends com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<Collections> {
    public static final String PROPERTY_FOO_1 = "foo1";

    @javax.inject.Inject
    public CollectionsSerializer(@NotNull StringSerializer stringSerializer) {
        super("collections", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@NotNull com.fasterxml.jackson.core.JsonGenerator serializeTo, @NotNull Collections object, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, com.fasterxml.jackson.core.JsonProcessingException {
        verifyVersionWritable(formatVersion);
        serializeArray(object.getFoo1(), String.class, PROPERTY_FOO_1, serializeTo, formatVersion);
    }

    @Override
    @NotNull
    public Collections deserialize(@NotNull com.fasterxml.jackson.core.JsonParser deserializeFrom, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, com.fasterxml.jackson.core.JsonProcessingException {
        verifyVersionWritable(formatVersion);

        List<? extends String> foo1 = null;

        com.cedarsoft.serialization.jackson.JacksonParserWrapper parser = new com.cedarsoft.serialization.jackson.JacksonParserWrapper(deserializeFrom);
        while (parser.nextToken() == com.fasterxml.jackson.core.JsonToken.FIELD_NAME) {
            String currentName = parser.getCurrentName();

            if (currentName.equals(PROPERTY_FOO_1)) {
                parser.nextToken();
                foo1 = deserializeArray(String.class, formatVersion, deserializeFrom);
                continue;
            }
            throw new IllegalStateException("Unexpected field reached <" + currentName + ">");
        }

        parser.verifyDeserialized(foo1, PROPERTY_FOO_1);
        assert foo1 != null;

        parser.ensureObjectClosed();

        Collections object = new Collections(foo1);
        return object;
    }
}