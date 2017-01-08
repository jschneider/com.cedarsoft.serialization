import org.jetbrains.annotations.NotNull;

public class SimpleSerializerVersionTest extends com.cedarsoft.serialization.test.utils.AbstractXmlVersionTest2<Simple> {
    @NotNull
    @org.junit.experimental.theories.DataPoint
    public static final com.cedarsoft.serialization.test.utils.VersionEntry ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractXmlVersionTest2.create(com.cedarsoft.version.Version.valueOf(1, 0, 0), SimpleSerializerVersionTest.class.getResource("Simple_1.0.0_1.xml"));

    @NotNull
    @Override
    protected com.cedarsoft.serialization.StreamSerializer<Simple> getSerializer() throws Exception {
        return com.google.inject.Guice.createInjector().getInstance(SimpleSerializer.class);
    }

    @Override
    protected void verifyDeserialized(@NotNull Simple deserialized, @NotNull com.cedarsoft.version.Version version) {
        org.assertj.core.api.Assertions.assertThat(deserialized.getFoo()).isNotNull();
    }
}