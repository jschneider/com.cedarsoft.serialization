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
public class LongSerializerTest extends AbstractXmlSerializerTest2<Long> {
  @Nonnull
  @Override
  protected StreamSerializer<Long> getSerializer() throws Exception {
    return new LongSerializer();
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

    getSerializer().serialize( 123L, out );
    shallAcceptClose[0] = true;
    out.close();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( 123L, "<long>123</long>" );

  @DataPoint
  public static final Entry<?> ENTRY4 = create( Long.MAX_VALUE, "<long>9223372036854775807</long>" );
  @DataPoint
  public static final Entry<?> ENTRY5 = create( Long.MIN_VALUE, "<long>-9223372036854775808</long>" );
}