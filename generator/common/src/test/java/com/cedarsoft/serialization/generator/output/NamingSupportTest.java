package com.cedarsoft.serialization.generator.output;

import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 *
 */
public class NamingSupportTest {
  private NamingSupport support;

  @BeforeMethod
  protected void setUp() throws Exception {
    support = new NamingSupport();
  }

  @Test
  public void testIt() {
    assertEquals( support.createXmlElementName( "String" ), "string" );
    assertEquals( support.createXmlElementName( "ACamelCase" ), "acamelcase" );
  }
}
