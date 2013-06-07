import org.jetbrains.annotations.NotNull;

public class SimpleSerializerTest extends com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2<Simple> {
    @NotNull
    @DataPoint
    public static final Entry<? extends Simple> ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractSerializerTest2.create(new Simple(), SimpleSerializerTest.class.getResource("Simple_1.0.0_1.json"));

    @NotNull
    @Override
    protected Serializer<Simple> getSerializer() throws Exception {
        return com.google.inject.Guice.createInject().getInstance(SimpleSerializer.class);
    }
}