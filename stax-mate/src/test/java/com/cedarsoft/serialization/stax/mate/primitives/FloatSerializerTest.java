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
public class FloatSerializerTest extends AbstractXmlSerializerTest2<Float> {
  @Nonnull
  @Override
  protected StreamSerializer<Float> getSerializer() throws Exception {
    return new FloatSerializer();
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

    getSerializer().serialize( 123.0f, out );
    shallAcceptClose[0] = true;
    out.close();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( 123.0f, "<float>123.0</float>" );
  @DataPoint
  public static final Entry<?> ENTRY2 = create( 123.5f, "<float>123.5</float>" );
  @DataPoint
  public static final Entry<?> ENTRY3 = create( -123.5f, "<float>-123.5</float>" );

  @DataPoint
  public static final Entry<?> ENTRY4 = create( Float.MAX_VALUE, "<float>3.4028235E38</float>" );
  @DataPoint
  public static final Entry<?> ENTRY5 = create( -Float.MAX_VALUE, "<float>-3.4028235E38</float>" );
}