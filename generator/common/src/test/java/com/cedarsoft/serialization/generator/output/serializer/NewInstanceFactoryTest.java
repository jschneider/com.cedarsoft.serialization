package com.cedarsoft.serialization.generator.output.serializer;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFormatter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.testng.Assert.*;

/**
 *
 */
public class NewInstanceFactoryTest {
  private NewInstanceFactory factory;
  private JCodeModel codeModel;
  private JFormatter formatter;
  private StringWriter out;

  @BeforeMethod
  protected void setUp() throws Exception {
    codeModel = new JCodeModel();
    factory = new NewInstanceFactory( codeModel );
    initializeFormatter();
  }

  private void initializeFormatter() {
    out = new StringWriter();
    formatter = new JFormatter( new PrintWriter( out ) );
  }

  @Test
  public void testString() throws IOException {
    assertFactory( String.class, "\"daValue\"" );
  }

  @Test
  public void testPrim() throws IOException {
    assertFactory( Integer.TYPE, "42" );
    assertFactory( Double.TYPE, "12.5D" );
    assertFactory( Float.TYPE, "44.0F" );
    assertFactory( Long.TYPE, "43L" );
    assertFactory( Boolean.TYPE, "true" );
    assertFactory( Character.TYPE, "'c'" );
  }

  @Test
  public void testNum() throws IOException {
    assertFactory( Integer.class, "java.lang.Integer.valueOf(42)" );
    assertFactory( Double.class, "java.lang.Double.valueOf(12.5D)" );
    assertFactory( Float.class, "java.lang.Float.valueOf(44.0F)" );
    assertFactory( Long.class, "java.lang.Long.valueOf(43L)" );
    assertFactory( Boolean.class, "java.lang.Boolean.TRUE" );
  }

  @Test
  public void testObject() throws IOException {
    assertFactory( Object.class, "new java.lang.Object()" );
  }

  private void assertFactory( @NotNull Class<?> type, @NotNull @NonNls String expected ) throws IOException {
    initializeFormatter();
    factory.create( new TypeMirrorMock( type ), "daValue" ).generate( formatter );
    assertEquals( out.toString().trim(), expected.trim() );
  }
}
