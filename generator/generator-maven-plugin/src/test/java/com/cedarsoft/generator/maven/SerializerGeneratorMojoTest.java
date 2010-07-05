package com.cedarsoft.generator.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.testng.annotations.*;

import java.io.File;

/**
 *
 */
public class SerializerGeneratorMojoTest extends AbstractMojoTestCase {
  @Test
  public void testIt() throws Exception {
    File testPom = new File( getBasedir(), "src/test/resources/unit/basic-test/basic-test-plugin-config.xml" );

    SerializerGeneratorMojo mojo = ( SerializerGeneratorMojo ) lookupMojo( "generate", testPom );

    assertNotNull( mojo );


  }

}
