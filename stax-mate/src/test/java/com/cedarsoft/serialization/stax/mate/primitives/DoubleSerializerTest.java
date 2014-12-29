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
public class DoubleSerializerTest extends AbstractXmlSerializerTest2<Double> {
  @Nonnull
  @Override
  protected StreamSerializer<Double> getSerializer() throws Exception {
    return new DoubleSerializer();
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

    getSerializer().serialize( 123.0, out );
    shallAcceptClose[0] = true;
    out.close();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( 123.0, "<double>123.0</double>" );
  @DataPoint
  public static final Entry<?> ENTRY2 = create( 123.5, "<double>123.5</double>" );
  @DataPoint
  public static final Entry<?> ENTRY3 = create( -123.5, "<double>-123.5</double>" );

  @DataPoint
  public static final Entry<?> ENTRY4 = create( Double.MAX_VALUE, "<double>1.7976931348623157E308</double>" );
  @DataPoint
  public static final Entry<?> ENTRY5 = create( -Double.MAX_VALUE, "<double>-1.7976931348623157E308</double>" );
}