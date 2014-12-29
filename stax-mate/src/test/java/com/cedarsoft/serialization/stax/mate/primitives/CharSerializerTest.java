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
public class CharSerializerTest extends AbstractXmlSerializerTest2<Character> {
  @Nonnull
  @Override
  protected StreamSerializer<Character> getSerializer() throws Exception {
    return new CharSerializer();
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

    getSerializer().serialize( ( char ) 123, out );
    shallAcceptClose[0] = true;
    out.close();
  }

  @DataPoint
  public static final Entry<?> ENTRY1 = create( ( char ) 123.0, "<char>{</char>" );
  @DataPoint
  public static final Entry<?> ENTRY3 = create( ( char ) -123.5, "<char>ﾅ</char>" );
}