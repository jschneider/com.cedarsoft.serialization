package com.cedarsoft.serialization.generator;

import com.cedarsoft.AssertUtils;
import com.cedarsoft.TestUtils;
import com.sun.codemodel.JClassAlreadyExistsException;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.*;

/**
 * 
 */
public class GeneratorTest {
  private File destDir;
  private File testDestDir;

  @BeforeMethod
  protected void setUp() throws Exception {
    destDir = TestUtils.createEmptyTmpDir();
    testDestDir = TestUtils.createEmptyTmpDir();
  }

  @AfterMethod
  public void tearDown() throws IOException {
    FileUtils.deleteDirectory( destDir );
    FileUtils.deleteDirectory( testDestDir );
  }

  @Test
  public void testIt() throws URISyntaxException, IOException, JClassAlreadyExistsException {
    File javaFile = new File( getClass().getResource( "/com/cedarsoft/serialization/generator/staxmate/test/Window.java" ).toURI() );

    GeneratorConfiguration configuration = new GeneratorConfiguration( javaFile, destDir, testDestDir );
    Generator.GeneratorRunner.generate( configuration );


    File serializerFile = new File( destDir, "com/cedarsoft/serialization/generator/staxmate/test/WindowSerializer.java" );
    assertTrue( serializerFile.exists() );

    AssertUtils.assertEquals( FileUtils.readFileToString( serializerFile ).trim(), getClass().getResource("GeneratorTest.testIt_1.txt" ));

    File serializerTestFile = new File( testDestDir, "com/cedarsoft/serialization/generator/staxmate/test/WindowSerializerTest.java" );
    assertTrue( serializerTestFile.exists() );
    AssertUtils.assertEquals( FileUtils.readFileToString( serializerTestFile ).trim(), getClass().getResource("GeneratorTest.testIt_2.txt" ) );

    File serializerVersionTestFile = new File( testDestDir, "com/cedarsoft/serialization/generator/staxmate/test/WindowSerializerVersionTest.java" );
    assertTrue( serializerVersionTestFile.exists() );
    AssertUtils.assertEquals( FileUtils.readFileToString( serializerVersionTestFile ).trim(),getClass().getResource("GeneratorTest.testIt_3.txt" ));
  }
}
