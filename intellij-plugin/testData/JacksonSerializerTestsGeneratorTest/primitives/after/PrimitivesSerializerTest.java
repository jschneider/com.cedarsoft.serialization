import org.jetbrains.annotations.NotNull;

public class PrimitivesSerializerTest extends com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2<Primitives> {
    @NotNull
    @DataPoint
    public static final Entry<? extends Primitives> ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractSerializerTest2.create(new Primitives(), PrimitivesSerializerTest.class.getResource("Primitives_1.0.0_1.json"));

    @NotNull
    @Override
    protected Serializer<Primitives> getSerializer() throws Exception {
        return com.google.inject.Guice.createInject().getInstance(PrimitivesSerializer.class);
    }
}