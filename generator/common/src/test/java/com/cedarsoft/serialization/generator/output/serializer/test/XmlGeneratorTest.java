package com.cedarsoft.serialization.generator.output.serializer.test;

import com.cedarsoft.codegen.CodeGenerator;
import com.cedarsoft.codegen.model.FieldInfo;
import com.cedarsoft.serialization.generator.decision.XmlDecisionCallback;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import static org.junit.Assert.*;

/**
 *
 */
public class XmlGeneratorTest {
  private XmlGenerator generator;

  @Before
  public void setUp() throws Exception {
    generator = new XmlGenerator( new CodeGenerator<XmlDecisionCallback>( new XmlDecisionCallback() {
      @NotNull
      @Override
      public Target getSerializationTarget( @NotNull FieldInfo fieldInfo ) {
        return Target.ELEMENT;
      }
    } ) );
  }

  @Test
  public void testNames() {
    assertEquals( "com.test.SerializerTest", generator.createSerializerTestName( "com.test.Serializer" ) );
  }
}
