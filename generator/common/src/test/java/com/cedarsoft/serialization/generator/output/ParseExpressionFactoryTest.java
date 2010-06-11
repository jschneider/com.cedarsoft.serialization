package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.serialization.generator.model.DefaultFieldTypeInformation;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFormatter;
import com.sun.mirror.type.TypeMirror;
import com.sun.mirror.util.TypeVisitor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.io.StringWriter;

import static org.testng.Assert.*;

/**
 *
 */
public class ParseExpressionFactoryTest {
  private JCodeModel model;
  private ParseExpressionFactory factory;

  @BeforeMethod
  protected void setUp() throws Exception {
    model = new JCodeModel();
    factory = new ParseExpressionFactory( model );
  }

  @Test
  public void testString() {
    checkForType( String.class, "aCall()" );
  }

  @Test
  public void testNumbers() {
    checkForType( Double.class, "java.lang.Double.parseDouble(aCall())" );
    checkForType( Double.TYPE, "java.lang.Double.parseDouble(aCall())" );
    checkForType( Integer.class, "java.lang.Integer.parseInt(aCall())" );
    checkForType( Integer.TYPE, "java.lang.Integer.parseInt(aCall())" );
    checkForType( Float.class, "java.lang.Float.parseFloat(aCall())" );
    checkForType( Float.TYPE, "java.lang.Float.parseFloat(aCall())" );
    checkForType( Boolean.class, "java.lang.Boolean.parseBoolean(aCall())" );
    checkForType( Boolean.TYPE, "java.lang.Boolean.parseBoolean(aCall())" );
  }

  private void checkForType( @NotNull Class<?> type, @NotNull @NonNls String expected ) {
    JExpression parseExpression = factory.createParseExpression( JExpr.invoke( "aCall" ), new DefaultFieldTypeInformation( new TypeMirrorMock( type ) ) );

    StringWriter out = new StringWriter();
    parseExpression.generate( new JFormatter( out ) );
    assertEquals( out.toString(), expected );
  }

  private static class TypeMirrorMock implements TypeMirror {
    @NotNull
    private final Class<?> type;

    private TypeMirrorMock( @NotNull Class<?> type ) {
      this.type = type;
    }

    @Override
    public void accept( TypeVisitor v ) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return type.getName();
    }
  }
}
