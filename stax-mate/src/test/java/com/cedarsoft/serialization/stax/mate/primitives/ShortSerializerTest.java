package com.cedarsoft.serialization.stax.mate.primitives;

import com.cedarsoft.serialization.StreamSerializer;
import com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest2;
import com.cedarsoft.serialization.test.utils.Entry;
import org.junit.*;
import org.junit.experimental.theories.*;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.assertj.core.api.Fail.fail;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ShortSerializerTest extends AbstractXmlSerializerTest2<Short> {
  @Nonnull
  @Override
  protected StreamSerializer<Short> getSerializer() throws Exception {
    return new ShortSerializer();
  }

  @Test
  public void testNotClose() throws Exception {
    final boolean[] shallAcceptClose = {false};

    OutputStream out = new FilterOutputStream( new ByteArrayOutputStream() ) {
      private boolean closed;

      @Override
      public void close() throws IOException {
        if ( !shallAcceptClose[0] ) {
          fail( "Unacceptable close!" );
        }

        super.close();
        closed = true;
      }
    };

    getSerializer().serialize( ( short ) 123, out );
    shallAcceptClose[0] = true;
    out.close();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( ( short ) 123, "<short>123</short>" );
  @DataPoint
  public static final Entry<?> ENTRY3 = create( ( short ) -123, "<short>-123</short>" );

  @DataPoint
  public static final Entry<?> ENTRY4 = create( Short.MAX_VALUE, "<short>32767</short>" );
  @DataPoint
  public static final Entry<?> ENTRY5 = create( Short.MIN_VALUE, "<short>-32768</short>" );
}