public class SetterSerializer extends com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<Setter> {
    public static final String PROPERTY_FOO = "foo"

    public SetterSerializer(@org.jetbrains.annotations.NotNull StringSerializer stringSerializer) {
        super("setter_serializer", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@org.jetbrains.annotations.NotNull com.fasterxml.jackson.core.JsonGenerator serializeTo, @org.jetbrains.annotations.NotNull Setter object, @org.jetbrains.annotations.NotNull com.cedarsoft.version.Version formatVersion) {
        verifyVersionWritable(formatVersion);
        serialize(object.getFoo(), String.class, PROPERTY_FOO, serializeTo, formatVersion);
    }

    @Override
    public void deserialize(@org.jetbrains.annotations.NotNull com.fasterxml.jackson.core.JsonParser deserializeFrom, @org.jetbrains.annotations.NotNull com.cedarsoft.version.Version formatVersion) {
        verifyVersionWritable(formatVersion);

        String foo = null;

        com.cedarsoft.serialization.jackson.JacksonParserWrapper parser = new com.cedarsoft.serialization.jackson.JacksonParserWrapper(deserializeFrom);
        while (parser.nextToken() == com.fasterxml.jackson.core.JsonToken.FIELD_NAME) {
            String currentName = parser.getCurrentName();

            if (currentName.equals(PROPERTY_FOO)) {
                parser.nextToken(com.fasterxml.jackson.core.JsonToken.START_OBJECT);
                foo = deserialize(String.class, formatVersion, deserializeFrom);
                continue;
            }
        }

        parser.verifyDeserialized(foo, PROPERTY_FOO);
        assert foo != null;

        parser.ensureObjectClosed();

        Setter object = new Setter();
        object.setFoo(foo);
        return object;
    }
}