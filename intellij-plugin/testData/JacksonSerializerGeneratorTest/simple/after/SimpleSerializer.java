public class SimpleSerializer extends com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<Simple> {
    public static final String PROPERTY_FOO = "foo"

    public SimpleSerializer(@org.jetbrains.annotations.NotNull StringSerializer stringSerializer) {
        super("simple_serializer", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@org.jetbrains.annotations.NotNull com.fasterxml.jackson.core.JsonGenerator serializeTo, @org.jetbrains.annotations.NotNull Simple object, @org.jetbrains.annotations.NotNull com.cedarsoft.version.Version formatVersion) {
        verifyVersionWritable(formatVersion);
        serialize(object.getFoo(), String.class, PROPERTY_FOO, serializeTo, formatVersion);
    }

    @Override
    public void deserialize(@org.jetbrains.annotations.NotNull com.fasterxml.jackson.core.JsonParser deserializeFrom, @org.jetbrains.annotations.NotNull com.cedarsoft.version.Version formatVersion) {
        verifyVersionWritable(formatVersion);

        String foo = null;

        JacksonParserWrapper parser = new JacksonParserWrapper(deserializeFrom);
        while (parser.nextToken() == JsonToken.FIELD_NAME) {
            String currentName = parser.getCurrentName();

            if (currentName.equals(PROPERTY_FOO)) {
                parser.nextToken(JsonToken.START_OBJECT);
                foo = deserialize(String.class, formatVersion, deserializeFrom);
                continue;
            }
        }

        parser.verifyDeserialized(foo, PROPERTY_FOO);
        assert foo != null;

        parser.ensureObjectClosed();

        Simple object = new Simple(foo);
        return object;
    }
}