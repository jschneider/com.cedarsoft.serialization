package com.cedarsoft.generator.maven;


import com.cedarsoft.MockitoTemplate;
import org.apache.maven.plugin.logging.Log;
import org.mockito.Mock;
import org.testng.annotations.*;

import static org.mockito.Mockito.*;

/**
 *
 */
public class LogWriterTest {
  @Test
  public void testIt() throws Exception {
    new MockitoTemplate() {
      @Mock
      private Log log;

      @Override
      protected void stub() throws Exception {
      }

      @Override
      protected void execute() throws Exception {
        LogWriter logWriter = new LogWriter( log );

        logWriter.write( "a\n" );
        logWriter.write( "b\n" );
        logWriter.write( "c\n" );
        logWriter.write( "asdf\n" );
        logWriter.write( "another message\n" );

        logWriter.close();
      }

      @Override
      protected void verifyMocks() throws Exception {
        verify( log ).info( "a" );
        verify( log ).info( "b" );
        verify( log ).info( "c" );
        verify( log ).info( "asdf" );
        verify( log ).info( "another message" );

        verifyNoMoreInteractions( log );
      }
    }.run();
  }

}
