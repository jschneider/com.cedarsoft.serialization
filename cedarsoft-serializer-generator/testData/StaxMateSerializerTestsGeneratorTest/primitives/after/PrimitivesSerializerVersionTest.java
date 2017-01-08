import org.jetbrains.annotations.NotNull;

public class PrimitivesSerializerVersionTest extends com.cedarsoft.serialization.test.utils.AbstractXmlVersionTest2<Primitives> {
    @NotNull
    @org.junit.experimental.theories.DataPoint
    public static final com.cedarsoft.serialization.test.utils.VersionEntry ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractXmlVersionTest2.create(com.cedarsoft.version.Version.valueOf(1, 0, 0), PrimitivesSerializerVersionTest.class.getResource("Primitives_1.0.0_1.xml"));

    @NotNull
    @Override
    protected com.cedarsoft.serialization.StreamSerializer<Primitives> getSerializer() throws Exception {
        return com.google.inject.Guice.createInjector().getInstance(PrimitivesSerializer.class);
    }

    @Override
    protected void verifyDeserialized(@NotNull Primitives deserialized, @NotNull com.cedarsoft.version.Version version) {
        org.assertj.core.api.Assertions.assertThat(deserialized.getFoo1()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(deserialized.getFoo2()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(deserialized.getFoo3()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(deserialized.getFoo4()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(deserialized.getFoo5()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(deserialized.getFoo6()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(deserialized.getFoo7()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(deserialized.isFoo8()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(deserialized.getFoo9()).isNotNull();
    }
}