package com.cedarsoft.serialization;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;
import org.testng.*;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *
 */
public class HashSerializer2Test {
  private HashSerializer serializer;

  @BeforeMethod
  public void setup() {
    serializer = new HashSerializer();
  }

  @Test
  public void testIt() throws IOException, SAXException {
    byte[] serialized = serializer.serializeToByteArray( Hash.fromHex( Algorithm.MD5, "121212" ) );
    AssertUtils.assertXMLEqual( new String( serialized ).trim(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<hash algorithm=\"MD5\">121212</hash>" );

    Hash deserialized = serializer.deserialize( new ByteArrayInputStream( serialized ) );
    Assert.assertEquals( deserialized, Hash.fromHex( Algorithm.MD5, "121212" ) );
  }
}

