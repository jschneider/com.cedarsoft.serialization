import org.jetbrains.annotations.NotNull;

public class SetterSerializerTest extends com.cedarsoft.serialization.test.utils.AbstractJsonSerializerTest2<Setter> {
    @NotNull
    @DataPoint
    public static final Entry<? extends Setter> ENTRY1 = com.cedarsoft.serialization.test.utils.AbstractSerializerTest2.create(new Setter(), SetterSerializerTest.class.getResource("Setter_1.0.0_1.json"));

    @NotNull
    @Override
    protected Serializer<Setter> getSerializer() throws Exception {
        return com.google.inject.Guice.createInject().getInstance(SetterSerializer.class);
    }
}