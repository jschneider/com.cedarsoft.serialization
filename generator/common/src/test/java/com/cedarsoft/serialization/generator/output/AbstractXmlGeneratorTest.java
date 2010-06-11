package com.cedarsoft.serialization.generator.output;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import com.cedarsoft.serialization.generator.model.DomainObjectDescriptor;
import com.cedarsoft.serialization.generator.model.FieldDeclarationInfo;
import com.cedarsoft.serialization.generator.model.FieldInfo;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.io.StringWriter;
import java.util.Map;

import static org.testng.Assert.*;

/**
 *
 */
public class AbstractXmlGeneratorTest {
  private AbstractXmlGenerator generator;

  @BeforeMethod
  public void setup() {
    generator = new AbstractXmlGenerator( new CodeGenerator<XmlDecisionCallback>( new XmlDecisionCallback() {
      @NotNull
      @Override
      public Target getSerializationTarget( @NotNull FieldInfo fieldInfo ) {
        throw new UnsupportedOperationException();
      }
    } ) ) {
      @NotNull
      @Override
      protected JClass createSerializerExtendsExpression( @NotNull JClass domainType ) {
        throw new UnsupportedOperationException();
      }

      @Override
      protected Map<FieldDeclarationInfo, JVar> addSerializationStuff( @NotNull DomainObjectDescriptor domainObjectDescriptor, @NotNull JMethod serializeMethod, @NotNull JMethod deserializeMethod ) {
        throw new UnsupportedOperationException();
      }

      @NotNull
      @Override
      protected Class<?> getExceptionType() {
        throw new UnsupportedOperationException();
      }

      @NotNull
      @Override
      protected Class<?> getSerializeFromType() {
        throw new UnsupportedOperationException();
      }

      @NotNull
      @Override
      protected Class<?> getSerializeToType() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Test
  public void testName() {
    assertEquals( generator.createSerializerClassName( "com.cedarsoft.serialization.generator.staxmate.StaxMateGenerator" ), "com.cedarsoft.serialization.generator.staxmate.StaxMateGeneratorSerializer" );
    assertEquals( generator.createSerializerClassName( "java.lang.String" ), "java.lang.StringSerializer" );
  }

  @Test
  public void testVersionRangeInvo() {
    StringWriter out = new StringWriter();
    generator.createDefaultVersionRangeInvocation( Version.valueOf( 1, 0, 0 ), Version.valueOf( 1, 0, 0 ) ).state( new JFormatter( out ) );
    assertEquals( out.toString().trim(), "com.cedarsoft.VersionRange.from(1, 0, 0).to(1, 0, 0);" );
  }

  @Test
  public void testNameSpace() {
    assertEquals( generator.getNamespace( "com.cedarsoft.serialization.generator.test.Window" ), "http://www.cedarsoft.com/serialization/generator/test/Window/1.0.0" );
  }
}
