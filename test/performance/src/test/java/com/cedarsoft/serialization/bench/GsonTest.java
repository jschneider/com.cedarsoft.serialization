package com.cedarsoft.serialization.bench;

import com.cedarsoft.serialization.bench.jaxb.Extension;
import com.cedarsoft.serialization.bench.jaxb.FileType;
import com.google.gson.Gson;
import org.jetbrains.annotations.NonNls;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class GsonTest {
  @NonNls
  public static final String FILE_TYPE = "{\"dependent\":false,\"id\":\"jpg\",\"extension\":{\"isDefault\":true,\"delimiter\":\".\",\"extension\":\"jpg\"}}";

  @Test
  public void testIt() {
    Gson gson = new Gson();
    assertEquals( "1", gson.toJson( 1 ) );
  }

  @Test
  public void testBag() throws Exception {
    BagOfPrimitives obj = new BagOfPrimitives();
    Gson gson = new Gson();
    assertEquals( "{\"value1\":1,\"value2\":\"abc\"}", gson.toJson( obj ) );
  }

  @Test
  public void testFileType() throws Exception {
    FileType type = new FileType( "jpg", new Extension( ".", "jpg", true ), false );
    assertEquals( FILE_TYPE, new Gson().toJson( type ) );

    FileType deserialized = new Gson().fromJson( FILE_TYPE, FileType.class );
    assertEquals( "jpg", deserialized.getId() );
    assertEquals( "jpg", deserialized.getExtension().getExtension() );
    assertEquals( ".", deserialized.getExtension().getDelimiter() );
    assertTrue( deserialized.getExtension().isDefault() );
    assertFalse( deserialized.isDependent() );
  }

  static class BagOfPrimitives {
    private int value1 = 1;
    private String value2 = "abc";
    private transient int value3 = 3;

    BagOfPrimitives() {
      // no-args constructor
    }
  }
}
