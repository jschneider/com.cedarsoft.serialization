import org.jetbrains.annotations.NotNull;

public class PrimitivesSerializerTest extends com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2<Primitives> {
    @NotNull
    @org.junit.experimental.theories.DataPoint
    public static final com.cedarsoft.serialization.test.utils.Entry<? extends Primitives> ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractSerializerTest2.create(
            new Primitives(
                    99,
                    99,
                    99,
                    99,
                    99,
                    99,
                    99,
                    99,
                    "foo"
            ),
            PrimitivesSerializerTest.class.getResource("Primitives_1.0.0_1.json"));

    @NotNull
    @Override
    protected com.cedarsoft.serialization.StreamSerializer<Primitives> getSerializer() throws Exception {
        return com.google.inject.Guice.createInjector().getInstance(PrimitivesSerializer.class);
    }
}