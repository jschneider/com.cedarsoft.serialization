public class SimpleSerializer extends com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<Simple> {
    public SimpleSerializer(@org.jetbrains.annotations.NotNull StringSerializer stringSerializer) {
        super("simple_serializer", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@org.jetbrains.annotations.NotNull com.fasterxml.jackson.core.JsonGenerator serializeTo, @org.jetbrains.annotations.NotNull Simple object, @org.jetbrains.annotations.NotNull com.cedarsoft.version.Version formatVersion) {
        verifyVersionWritable(formatVersion);
    }

    @Override
    public void deserialize(@org.jetbrains.annotations.NotNull com.fasterxml.jackson.core.JsonParser deserializeFrom, @org.jetbrains.annotations.NotNull com.cedarsoft.version.Version formatVersion) {
        verifyVersionWritable(formatVersion);
    }
}