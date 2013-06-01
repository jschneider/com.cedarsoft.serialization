public class PrimitivesSerializer extends com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<Primitives> {
    public static final String PROPERTY_FOO_1 = "foo1";
    public static final String PROPERTY_FOO_2 = "foo2";
    public static final String PROPERTY_FOO_3 = "foo3";
    public static final String PROPERTY_FOO_4 = "foo4";
    public static final String PROPERTY_FOO_5 = "foo5";
    public static final String PROPERTY_FOO_6 = "foo6";
    public static final String PROPERTY_FOO_7 = "foo7";
    public static final String PROPERTY_FOO_8 = "foo8";
    public static final String PROPERTY_FOO_9 = "foo9";

    public PrimitivesSerializer(@org.jetbrains.annotations.NotNull StringSerializer stringSerializer) {
        super("primitives_serializer", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@org.jetbrains.annotations.NotNull com.fasterxml.jackson.core.JsonGenerator serializeTo, @org.jetbrains.annotations.NotNull Primitives object, @org.jetbrains.annotations.NotNull com.cedarsoft.version.Version formatVersion) throws java.io.IOException, com.cedarsoft.version.VersionException {
        verifyVersionWritable(formatVersion);
        serialize(object.getFoo1(), int.class, PROPERTY_FOO_1, serializeTo, formatVersion);
        serialize(object.getFoo2(), short.class, PROPERTY_FOO_2, serializeTo, formatVersion);
        serialize(object.getFoo3(), byte.class, PROPERTY_FOO_3, serializeTo, formatVersion);
        serialize(object.getFoo4(), long.class, PROPERTY_FOO_4, serializeTo, formatVersion);
        serialize(object.getFoo5(), double.class, PROPERTY_FOO_5, serializeTo, formatVersion);
        serialize(object.getFoo6(), float.class, PROPERTY_FOO_6, serializeTo, formatVersion);
        serialize(object.getFoo7(), char.class, PROPERTY_FOO_7, serializeTo, formatVersion);
        serialize(object.getFoo8(), boolean.class, PROPERTY_FOO_8, serializeTo, formatVersion);
        serialize(object.getFoo9(), String.class, PROPERTY_FOO_9, serializeTo, formatVersion);
    }

    @Override
    public
    @org.jetbrains.annotations.NotNull
    Primitives deserialize(@org.jetbrains.annotations.NotNull com.fasterxml.jackson.core.JsonParser deserializeFrom, @org.jetbrains.annotations.NotNull com.cedarsoft.version.Version formatVersion) throws java.io.IOException, com.cedarsoft.version.VersionException {
        verifyVersionWritable(formatVersion);

        int foo1 = -1;
        short foo2 = -1;
        byte foo3 = -1;
        long foo4 = -1;
        double foo5 = -1;
        float foo6 = -1;
        char foo7 = -1;
        boolean foo8 = -1;
        String foo9 = null;

        com.cedarsoft.serialization.jackson.JacksonParserWrapper parser = new com.cedarsoft.serialization.jackson.JacksonParserWrapper(deserializeFrom);
        while (parser.nextToken() == com.fasterxml.jackson.core.JsonToken.FIELD_NAME) {
            String currentName = parser.getCurrentName();

            if (currentName.equals(PROPERTY_FOO_1)) {
                parser.nextToken(com.fasterxml.jackson.core.JsonToken.START_OBJECT);
                foo1 = deserialize(int.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_FOO_2)) {
                parser.nextToken(com.fasterxml.jackson.core.JsonToken.START_OBJECT);
                foo2 = deserialize(short.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_FOO_3)) {
                parser.nextToken(com.fasterxml.jackson.core.JsonToken.START_OBJECT);
                foo3 = deserialize(byte.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_FOO_4)) {
                parser.nextToken(com.fasterxml.jackson.core.JsonToken.START_OBJECT);
                foo4 = deserialize(long.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_FOO_5)) {
                parser.nextToken(com.fasterxml.jackson.core.JsonToken.START_OBJECT);
                foo5 = deserialize(double.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_FOO_6)) {
                parser.nextToken(com.fasterxml.jackson.core.JsonToken.START_OBJECT);
                foo6 = deserialize(float.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_FOO_7)) {
                parser.nextToken(com.fasterxml.jackson.core.JsonToken.START_OBJECT);
                foo7 = deserialize(char.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_FOO_8)) {
                parser.nextToken(com.fasterxml.jackson.core.JsonToken.START_OBJECT);
                foo8 = deserialize(boolean.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_FOO_9)) {
                parser.nextToken(com.fasterxml.jackson.core.JsonToken.START_OBJECT);
                foo9 = deserialize(String.class, formatVersion, deserializeFrom);
                continue;
            }
        }

        parser.verifyDeserialized(foo1, PROPERTY_FOO_1);
        parser.verifyDeserialized(foo2, PROPERTY_FOO_2);
        parser.verifyDeserialized(foo3, PROPERTY_FOO_3);
        parser.verifyDeserialized(foo4, PROPERTY_FOO_4);
        parser.verifyDeserialized(foo5, PROPERTY_FOO_5);
        parser.verifyDeserialized(foo6, PROPERTY_FOO_6);
        parser.verifyDeserialized(foo7, PROPERTY_FOO_7);
        parser.verifyDeserialized(foo8, PROPERTY_FOO_8);
        parser.verifyDeserialized(foo9, PROPERTY_FOO_9);
        assert foo9 != null;

        parser.ensureObjectClosed();

        Primitives object = new Primitives(foo1, foo2, foo3, foo4, foo5, foo6, foo7, foo8, foo9);
        return object;
    }
}