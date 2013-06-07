import org.jetbrains.annotations.NotNull;

public class SimpleSerializerVersionTest extends com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2<Simple> {
    @NotNull
    @DataPoint
    public static final VersionEntry ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2.create(com.cedarsoft.version.Version.valueOf(1, 0, 0), SimpleSerializerVersionTest.class.getResource("Simple_1.0.0_1.json"));

    @NotNull
    @Override
    protected Serializer<Simple> getSerializer() throws Exception {
        return com.google.inject.Guice.createInject().getInstance(SimpleSerializer.class);
    }

    @Override
    protected void verifyDeserialized(@NotNull Simple deserialized, @NotNull com.cedarsoft.version.Version version) {
        org.fest.assertions.Assertions.assertThat(deserialized.getFoo()).isNotNull();
    }
}