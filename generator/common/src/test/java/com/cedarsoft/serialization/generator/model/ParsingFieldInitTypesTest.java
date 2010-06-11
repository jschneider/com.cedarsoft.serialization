package com.cedarsoft.serialization.generator.model;

import com.cedarsoft.serialization.generator.parsing.Parser;
import com.cedarsoft.serialization.generator.parsing.Result;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import org.testng.annotations.*;

import java.io.File;
import java.net.URL;

import static org.testng.Assert.*;

/**
 *
 */
public class ParsingFieldInitTypesTest {
  private DomainObjectDescriptorFactory factory;

  @BeforeMethod
  protected void setUp() throws Exception {
    URL resource = getClass().getResource( "/com/cedarsoft/serialization/generator/parsing/test/FieldTypesInit.java" );
    assertNotNull( resource );
    File javaFile = new File( resource.toURI() );
    assertTrue( javaFile.exists() );

    Result parsed = Parser.parse( javaFile );
    assertNotNull( parsed );
    assertEquals( parsed.getClassDeclarations().size(), 1 );
    ClassDeclaration classDeclaration = parsed.getClassDeclaration( "com.cedarsoft.serialization.generator.parsing.test.FieldTypesInit" );

    factory = new DomainObjectDescriptorFactory( classDeclaration );
  }

  @Test
  public void testFindField() {
    FieldDeclaration fieldDeclaration = factory.findFieldDeclaration( "width" );
    assertEquals( fieldDeclaration.getSimpleName(), "width" );
    assertEquals( fieldDeclaration.getType().toString(), "double" );
  }

  @Test
  public void testFindSetterInit() {
    FieldDeclaration fieldDeclaration = factory.findFieldDeclaration( "height" );
    MethodDeclaration setter = factory.findSetter( fieldDeclaration );
    assertNotNull( setter );
    assertEquals( setter.getSimpleName(), "setHeight" );
  }

  @Test
  public void testInitTypes() {
    DomainObjectDescriptor descriptor = factory.create();
    assertEquals( descriptor.getFieldsInitializedInConstructor().size(), 2 );
    assertEquals( descriptor.getFieldsInitializedInConstructor().get( 0 ).getSimpleName(), "description" );
    assertEquals( descriptor.getFieldsInitializedInConstructor().get( 1 ).getSimpleName(), "width" );

    assertEquals( descriptor.getFieldsInitializedInSetter().size(), 1 );
    assertEquals( descriptor.getFieldsInitializedInSetter().get( 0 ).getSimpleName(), "height" );
  }
}
