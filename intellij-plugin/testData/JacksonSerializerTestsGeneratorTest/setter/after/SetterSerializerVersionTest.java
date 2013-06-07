import org.jetbrains.annotations.NotNull;

public class SetterSerializerVersionTest extends com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2<Setter> {
    @NotNull
    @DataPoint
    public static final VersionEntry ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2.create(com.cedarsoft.version.Version.valueOf(1, 0, 0), SetterSerializerVersionTest.class.getResource("Setter_1.0.0_1.json"));

    @NotNull
    @Override
    protected Serializer<Setter> getSerializer() throws Exception {
        return com.google.inject.Guice.createInject().getInstance(SetterSerializer.class);
    }

    @Override
    protected void verifyDeserialized(@NotNull Setter deserialized, @NotNull com.cedarsoft.version.Version version) {
        org.fest.assertions.Assertions.assertThat(deserialized.getFoo()).isNotNull();
    }
}