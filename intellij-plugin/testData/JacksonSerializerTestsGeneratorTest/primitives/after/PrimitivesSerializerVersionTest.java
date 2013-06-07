import org.jetbrains.annotations.NotNull;

public class PrimitivesSerializerVersionTest extends com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2<Primitives> {
    @NotNull
    @DataPoint
    public static final VersionEntry ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2.create(com.cedarsoft.version.Version.valueOf(1, 0, 0), PrimitivesSerializerVersionTest.class.getResource("Primitives_1.0.0_1.json"));

    @NotNull
    @Override
    protected Serializer<Primitives> getSerializer() throws Exception {
        return com.google.inject.Guice.createInject().getInstance(PrimitivesSerializer.class);
    }

    @Override
    protected void verifyDeserialized(@NotNull Primitives deserialized, @NotNull com.cedarsoft.version.Version version) {
        org.fest.assertions.Assertions.assertThat(deserialized.getFoo1()).isNotNull();
        org.fest.assertions.Assertions.assertThat(deserialized.getFoo2()).isNotNull();
        org.fest.assertions.Assertions.assertThat(deserialized.getFoo3()).isNotNull();
        org.fest.assertions.Assertions.assertThat(deserialized.getFoo4()).isNotNull();
        org.fest.assertions.Assertions.assertThat(deserialized.getFoo5()).isNotNull();
        org.fest.assertions.Assertions.assertThat(deserialized.getFoo6()).isNotNull();
        org.fest.assertions.Assertions.assertThat(deserialized.getFoo7()).isNotNull();
        org.fest.assertions.Assertions.assertThat(deserialized.isFoo8()).isNotNull();
        org.fest.assertions.Assertions.assertThat(deserialized.getFoo9()).isNotNull();
    }
}