public class SimpleSerializer extends com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<Simple> {
    public SimpleSerializer(StringSerializer stringSerializer) {
        super("simple_serializer", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }
}