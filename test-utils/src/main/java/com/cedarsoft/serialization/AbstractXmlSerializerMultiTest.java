package com.cedarsoft.serialization;

import com.cedarsoft.AssertUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Abstract base class for XML based serializers.
 *
 * @param <T> the type of the serialized object
 */
public abstract class AbstractXmlSerializerMultiTest<T> extends AbstractSerializerMultiTest<T> {
  @Override
  protected void verifySerialized( @NotNull List<? extends byte[]> serialized ) throws Exception {
    List<? extends String> expected = getExpectedSerialized();

    int index = 0;
    for ( byte[] current : serialized ) {
      String expectedWithNamespace = AbstractXmlSerializerTest.addNameSpace( expected.get( index ), ( AbstractXmlSerializer<?, ?, ?, ?> ) getSerializer() );
      AssertUtils.assertXMLEqual( new String( current ), expectedWithNamespace );
      index++;
    }
  }

  /**
   * Returns the expected serialized string
   *
   * @return the expected serialized string
   */
  @NotNull
  @NonNls
  protected abstract List<? extends String> getExpectedSerialized();
}