import org.jetbrains.annotations.NotNull;

public class PrimitivesSerializerVersionTest extends com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2<Primitives> {
    @NotNull
    @org.junit.experimental.theories.DataPoint
    public static final com.cedarsoft.serialization.test.utils.VersionEntry ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractJsonVersionTest2.create(
            com.cedarsoft.version.Version.valueOf(1, 0, 0), PrimitivesSerializerVersionTest.class.getResource("Primitives_1.0.0_1.json"));

    @NotNull
    @Override
    protected com.cedarsoft.serialization.StreamSerializer<Primitives> getSerializer() throws Exception {
        return com.google.inject.Guice.createInjector().getInstance(PrimitivesSerializer.class);
    }

    @Override
    protected void verifyDeserialized(@NotNull Primitives deserialized, @NotNull com.cedarsoft.version.Version version) {
        org.junit.Assert.assertNotNull(deserialized.getFoo1());
        org.junit.Assert.assertNotNull(deserialized.getFoo2());
        org.junit.Assert.assertNotNull(deserialized.getFoo3());
        org.junit.Assert.assertNotNull(deserialized.getFoo4());
        org.junit.Assert.assertNotNull(deserialized.getFoo5());
        org.junit.Assert.assertNotNull(deserialized.getFoo6());
        org.junit.Assert.assertNotNull(deserialized.getFoo7());
        org.junit.Assert.assertNotNull(deserialized.isFoo8());
        org.junit.Assert.assertNotNull(deserialized.getFoo9());
    }
}