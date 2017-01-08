import org.jetbrains.annotations.NotNull;

public class SimpleSerializerTest extends com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2<Simple> {
    @NotNull
    @org.junit.experimental.theories.DataPoint
    public static final com.cedarsoft.serialization.test.utils.Entry<? extends Simple> ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractSerializerTest2.create(
            new Simple(
                    "foo"
            ),
            SimpleSerializerTest.class.getResource("Simple_1.0.0_1.json"));

    @NotNull
    @Override
    protected com.cedarsoft.serialization.StreamSerializer<Simple> getSerializer() throws Exception {
        return com.google.inject.Guice.createInjector().getInstance(SimpleSerializer.class);
    }
}