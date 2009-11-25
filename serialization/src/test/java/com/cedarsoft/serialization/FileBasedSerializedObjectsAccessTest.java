package com.cedarsoft.serialization;

import com.cedarsoft.StillContainedException;
import com.cedarsoft.TestUtils;
import com.cedarsoft.serialization.FileBasedSerializedObjectsAccess;
import org.apache.commons.io.IOUtils;
import static org.testng.Assert.*;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class FileBasedSerializedObjectsAccessTest {
  private FileBasedSerializedObjectsAccess access;

  @BeforeMethod
  protected void setUp() throws Exception {
    access = new FileBasedSerializedObjectsAccess( TestUtils.createEmptyTmpDir(), "xml" );
  }

  @Test
  public void testIt() throws IOException {
    assertEquals( access.getStoredIds().size(), 0 );
    {
      OutputStream out = access.openOut( "id" );
      IOUtils.write( "asdf".getBytes(), out );
      out.close();
    }

    assertEquals( access.getStoredIds().size(), 1 );
    assertTrue( access.getStoredIds().contains( "id" ) );

    {
      InputStream in = access.getInputStream( "id" );
      assertEquals( IOUtils.toString( in ), "asdf" );
      in.close();
    }
    InputStream in = new FileBasedSerializedObjectsAccess( access.getBaseDir(), "xml" ).getInputStream( "id" );
    assertEquals( IOUtils.toString( in ), "asdf" );
    in.close();
  }

  @Test
  public void testExists() throws IOException {
    assertEquals( access.getStoredIds().size(), 0 );
    {
      OutputStream out = access.openOut( "id" );
      IOUtils.write( "asdf".getBytes(), out );
      out.close();
    }

    try {
      access.openOut( "id" );
      fail("Where is the Exception");
    } catch ( StillContainedException e ) {
    }
  }
}
