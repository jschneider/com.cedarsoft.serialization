package com.cedarsoft.serialization.generator.intellij.stax.mate;

import com.cedarsoft.serialization.generator.intellij.SerializerTestsGenerator;
import com.cedarsoft.serialization.generator.intellij.model.FieldToSerialize;
import com.cedarsoft.serialization.generator.intellij.model.SerializerModel;
import com.google.common.collect.ImmutableList;
import com.intellij.codeInsight.NullableNotNullManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.codeStyle.VariableKind;
import com.intellij.psi.search.PsiShortNamesCache;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

/**
 * A simple class that generates a jackson serializer
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class StaxMateSerializerTestsGenerator implements SerializerTestsGenerator {
  @Nonnull
  private final Project project;
  @Nonnull
  private final CodeStyleManager codeStyleManager;
  @Nonnull
  private final JavaCodeStyleManager javaCodeStyleManager;
  @Nonnull
  private final PsiElementFactory elementFactory;
  @Nonnull
  private final JavaPsiFacade javaPsiFacade;
  @Nonnull
  private final PsiShortNamesCache shortNamesCache;
  @Nonnull
  private final NullableNotNullManager notNullManager;

  public StaxMateSerializerTestsGenerator( @Nonnull Project project ) {
    this.project = project;

    codeStyleManager = CodeStyleManager.getInstance( project );
    javaCodeStyleManager = JavaCodeStyleManager.getInstance( project );
    elementFactory = JavaPsiFacade.getElementFactory( project );
    javaPsiFacade = JavaPsiFacade.getInstance( project );
    shortNamesCache = PsiShortNamesCache.getInstance( project );
    notNullManager = NullableNotNullManager.getInstance( project );
  }

  @Nonnull
  public List<? extends PsiClass> generate( @Nonnull final SerializerModel serializerModel, @Nonnull final PsiDirectory testsTargetDir, @Nonnull final PsiDirectory testResourcesTargetDir ) {
    final PsiFile psiFile = serializerModel.getClassToSerialize().getContainingFile();

    //The directory the serializer is generated in
    final PsiClass[] testClasses = new PsiClass[2];

    new WriteCommandAction.Simple( serializerModel.getClassToSerialize().getProject(), psiFile ) {
      @Override
      protected void run() throws Throwable {
        testClasses[0] = JavaDirectoryService.getInstance().createClass( testsTargetDir, serializerModel.generateSerializerTestClassName() );
        testClasses[1] = JavaDirectoryService.getInstance().createClass( testsTargetDir, serializerModel.generateSerializerVersionTestClassName() );

        fillTest( serializerModel, testClasses[0] );
        fillVersionTest( serializerModel, testClasses[1] );


        //Now create the resource file
        testResourcesTargetDir.createFile( generateTestResourceName( serializerModel.getClassToSerialize().getName(), 1 ) );

        //Now beautify the code
        codeStyleManager.reformat( testClasses[0] );
        javaCodeStyleManager.shortenClassReferences( testClasses[0] );
        javaCodeStyleManager.optimizeImports( testClasses[0].getContainingFile() );
        codeStyleManager.reformat( testClasses[1] );
        javaCodeStyleManager.shortenClassReferences( testClasses[1] );
        javaCodeStyleManager.optimizeImports( testClasses[1].getContainingFile() );
      }
    }.execute();

    return ImmutableList.copyOf( testClasses );
  }

  private void fillVersionTest( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass versionTestClass ) {
    PsiClass classToSerialize = serializerModel.getClassToSerialize();
    addExtends( versionTestClass, classToSerialize, "com.cedarsoft.serialization.test.utils.AbstractXmlVersionTest2" );

    versionTestClass.add( generateGetSerializerMethod( serializerModel, versionTestClass ) );
    versionTestClass.add( generateVersionEntry( serializerModel, versionTestClass, 1 ) );

    versionTestClass.add( generateVerifyDeserialized( serializerModel, versionTestClass ) );
  }

  private PsiElement generateVerifyDeserialized( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass versionTestClass ) {
    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( "@Override protected void verifyDeserialized(" )
      .append( notNull() )
      .append( serializerModel.getClassToSerialize().getQualifiedName() )
      .append( " deserialized, " )
      .append( notNull() ).append( " com.cedarsoft.version.Version version" )
    ;

    methodBuilder.append( "){" );

    for ( FieldToSerialize entry : serializerModel.getFieldToSerializeEntries() ) {
      methodBuilder.append( "org.assertj.core.api.Assertions.assertThat(deserialized." ).append( entry.getAccessor() ).append( ").isNotNull();" );
    }

    methodBuilder.append( "}" );

    return elementFactory.createMethodFromText( methodBuilder.toString(), versionTestClass );
  }

  @Nonnull
  public PsiClass fillTest( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass testClass ) {
    PsiClass classToSerialize = serializerModel.getClassToSerialize();
    addExtends( testClass, classToSerialize, "com.cedarsoft.serialization.test.utils.AbstractXmlSerializerTest2" );

    testClass.add( generateGetSerializerMethod( serializerModel, testClass ) );
    testClass.add( generateEntry( serializerModel, testClass, 1 ) );


    return testClass;
  }

  @Nonnull
  private PsiField generateEntry( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass testClass, int entryIndex ) {
    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( notNull() ).append( "@org.junit.experimental.theories.DataPoint public static final com.cedarsoft.serialization.test.utils.Entry<? extends " )
      .append( serializerModel.getClassToSerialize().getQualifiedName() )
      .append( "> ENTRY" ).append( entryIndex )
      .append( "=" )
      .append( "com.cedarsoft.serialization.test.utils.AbstractSerializerTest2" ).append( ".create(" )
    ;


    //TODO add object generation
    methodBuilder.append( "new " ).append( serializerModel.getClassToSerialize().getQualifiedName() ).append( "()" );
    methodBuilder.append( ", " ).append( testClass.getQualifiedName() ).append( ".class.getResource(\"" ).append( generateTestResourceName( serializerModel.getClassToSerialize().getName(), entryIndex ) ).append( "\"));" );

    return elementFactory.createFieldFromText( methodBuilder.toString(), testClass );
  }

  @Nonnull
  private PsiField generateVersionEntry( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass testClass, int entryIndex ) {
    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( notNull() ).append( "@org.junit.experimental.theories.DataPoint public static final com.cedarsoft.serialization.test.utils.VersionEntry ENTRY" ).append( entryIndex )
      .append( "=" )
      .append( "com.cedarsoft.serialization.test.utils.AbstractXmlVersionTest2" ).append( ".create(com.cedarsoft.version.Version.valueOf( 1, 0, 0 )" )
    ;

    methodBuilder.append( ", " ).append( testClass.getQualifiedName() ).append( ".class.getResource(\"" ).append( generateTestResourceName( serializerModel.getClassToSerialize().getName(), entryIndex ) ).append( "\"));" );

    return elementFactory.createFieldFromText( methodBuilder.toString(), testClass );
  }

  @Nonnull
  private static String generateTestResourceName( @Nonnull String qualifiedName, int entryIndex ) {
    return qualifiedName + "_1.0.0_" + entryIndex + ".xml";
  }

  private void addExtends( @Nonnull PsiClass serializerClass, @Nonnull PsiClass classToSerialize, final String baseClass ) {
    //Add extends abstract base class
    {
      PsiJavaCodeReferenceElement extendsRef = elementFactory.createReferenceFromText( baseClass + "<" + classToSerialize.getQualifiedName() + ">", classToSerialize );

      PsiReferenceList extendsList = serializerClass.getExtendsList();
      assert extendsList != null;
      extendsList.add( extendsRef );
    }
  }

  @Nonnull
  private PsiElement generateGetSerializerMethod( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass testClass ) {
    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( notNull() ).append( "@Override\n" ).append( "  protected com.cedarsoft.serialization.StreamSerializer<" ).append( serializerModel.getClassToSerialize().getQualifiedName() ).append( "> getSerializer() throws Exception {" );
    methodBuilder.append( "return com.google.inject.Guice.createInjector().getInstance(" ).append( serializerModel.generateSerializerClassName() ).append( ".class);" );
    methodBuilder.append( "}" );

    return elementFactory.createMethodFromText( methodBuilder.toString(), testClass );
  }

  private String notNull() {
    return "@" + notNullManager.getDefaultNotNull() + " ";
  }

  /**
   * Creates the json serializedType for the given class name
   *
   * @param className the class name
   * @return the json serializedType
   */
  @Nonnull
  private String createType( @Nonnull String className ) {
    return javaCodeStyleManager.suggestVariableName( VariableKind.STATIC_FINAL_FIELD, className, null, null ).names[0].toLowerCase( Locale.getDefault() );
  }
}
