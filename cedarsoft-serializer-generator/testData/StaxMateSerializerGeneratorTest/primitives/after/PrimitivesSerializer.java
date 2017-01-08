import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PrimitivesSerializer extends com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer<Primitives> {
    public static final String PROPERTY_FOO_1 = "foo1";
    public static final String PROPERTY_FOO_2 = "foo2";
    public static final String PROPERTY_FOO_3 = "foo3";
    public static final String PROPERTY_FOO_4 = "foo4";
    public static final String PROPERTY_FOO_5 = "foo5";
    public static final String PROPERTY_FOO_6 = "foo6";
    public static final String PROPERTY_FOO_7 = "foo7";
    public static final String PROPERTY_FOO_8 = "foo8";
    public static final String PROPERTY_FOO_9 = "foo9";

    @javax.inject.Inject
    public PrimitivesSerializer(@NotNull IntegerSerializer integerSerializer, @NotNull ShortSerializer shortSerializer, @NotNull ByteSerializer byteSerializer, @NotNull LongSerializer longSerializer, @NotNull DoubleSerializer doubleSerializer, @NotNull FloatSerializer floatSerializer, @NotNull CharacterSerializer characterSerializer, @NotNull BooleanSerializer booleanSerializer, @NotNull StringSerializer stringSerializer) {
        super("primitives", "http://cedarsoft.com/serialization/Primitives", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(integerSerializer).responsibleFor(Integer.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(shortSerializer).responsibleFor(Short.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(byteSerializer).responsibleFor(Byte.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(longSerializer).responsibleFor(Long.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(doubleSerializer).responsibleFor(Double.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(floatSerializer).responsibleFor(Float.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(characterSerializer).responsibleFor(Character.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(booleanSerializer).responsibleFor(Boolean.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@NotNull org.codehaus.staxmate.out.SMOutputElement serializeTo, @NotNull Primitives object, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, javax.xml.stream.XMLStreamException {
        verifyVersionWritable(formatVersion);
        serialize(object.getFoo1(), Integer.class, PROPERTY_FOO_1, serializeTo, formatVersion);
        serialize(object.getFoo2(), Short.class, PROPERTY_FOO_2, serializeTo, formatVersion);
        serialize(object.getFoo3(), Byte.class, PROPERTY_FOO_3, serializeTo, formatVersion);
        serialize(object.getFoo4(), Long.class, PROPERTY_FOO_4, serializeTo, formatVersion);
        serialize(object.getFoo5(), Double.class, PROPERTY_FOO_5, serializeTo, formatVersion);
        serialize(object.getFoo6(), Float.class, PROPERTY_FOO_6, serializeTo, formatVersion);
        serialize(object.getFoo7(), Character.class, PROPERTY_FOO_7, serializeTo, formatVersion);
        serialize(object.isFoo8(), Boolean.class, PROPERTY_FOO_8, serializeTo, formatVersion);
        serialize(object.getFoo9(), String.class, PROPERTY_FOO_9, serializeTo, formatVersion);
    }

    @Override
    @NotNull
    public Primitives deserialize(@NotNull javax.xml.stream.XMLStreamReader deserializeFrom, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, javax.xml.stream.XMLStreamException {
        verifyVersionWritable(formatVersion);

        nextTag(deserializeFrom, PROPERTY_FOO_1);
        int foo1 = deserialize(Integer.class, formatVersion, deserializeFrom);
        nextTag(deserializeFrom, PROPERTY_FOO_2);
        short foo2 = deserialize(Short.class, formatVersion, deserializeFrom);
        nextTag(deserializeFrom, PROPERTY_FOO_3);
        byte foo3 = deserialize(Byte.class, formatVersion, deserializeFrom);
        nextTag(deserializeFrom, PROPERTY_FOO_4);
        long foo4 = deserialize(Long.class, formatVersion, deserializeFrom);
        nextTag(deserializeFrom, PROPERTY_FOO_5);
        double foo5 = deserialize(Double.class, formatVersion, deserializeFrom);
        nextTag(deserializeFrom, PROPERTY_FOO_6);
        float foo6 = deserialize(Float.class, formatVersion, deserializeFrom);
        nextTag(deserializeFrom, PROPERTY_FOO_7);
        char foo7 = deserialize(Character.class, formatVersion, deserializeFrom);
        nextTag(deserializeFrom, PROPERTY_FOO_8);
        boolean foo8 = deserialize(Boolean.class, formatVersion, deserializeFrom);
        nextTag(deserializeFrom, PROPERTY_FOO_9);
        String foo9 = deserialize(String.class, formatVersion, deserializeFrom);
        Primitives object = new Primitives(foo1, foo2, foo3, foo4, foo5, foo6, foo7, foo8, foo9);
        return object;
    }
}