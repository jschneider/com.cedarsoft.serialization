import org.jetbrains.annotations.NotNull;

public class SetterSerializerTest extends com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2<Setter> {
    @NotNull
    @org.junit.experimental.theories.DataPoint
    public static final com.cedarsoft.serialization.test.utils.Entry<? extends Setter> ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractSerializerTest2.create(
            new Setter(
                    "foo"
            ),
            SetterSerializerTest.class.getResource("Setter_1.0.0_1.json"));

    @NotNull
    @Override
    protected com.cedarsoft.serialization.StreamSerializer<Setter> getSerializer() throws Exception {
        return com.google.inject.Guice.createInjector().getInstance(SetterSerializer.class);
    }
}