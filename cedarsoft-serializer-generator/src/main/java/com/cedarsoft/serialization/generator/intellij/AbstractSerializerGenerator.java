package com.cedarsoft.serialization.generator.intellij;

import com.cedarsoft.serialization.generator.intellij.model.DelegatingSerializer;
import com.cedarsoft.serialization.generator.intellij.model.FieldSetter;
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
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.codeStyle.VariableKind;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class AbstractSerializerGenerator implements SerializerGenerator {
  @Nonnull
  protected final Project project;
  @Nonnull
  protected final CodeStyleManager codeStyleManager;
  @Nonnull
  protected final JavaCodeStyleManager javaCodeStyleManager;
  @Nonnull
  protected final PsiElementFactory elementFactory;
  @Nonnull
  protected final JavaPsiFacade javaPsiFacade;
  @Nonnull
  protected final PsiShortNamesCache shortNamesCache;
  @Nonnull
  protected final NullableNotNullManager notNullManager;
  @Nonnull
  protected final String abstractSerializerType;
  @Nonnull
  protected final String serializeToType;
  @Nonnull
  protected final String deserializeFromType;
  @Nonnull
  protected final String serializeExceptionType;

  protected AbstractSerializerGenerator( @Nonnull Project project, @Nonnull String abstractSerializerType, @Nonnull String serializeToType, @Nonnull String deserializeFromType, @Nonnull String serializeExceptionType ) {
    this.project = project;
    this.serializeExceptionType = serializeExceptionType;
    javaCodeStyleManager = JavaCodeStyleManager.getInstance( project );
    codeStyleManager = CodeStyleManager.getInstance( project );
    shortNamesCache = PsiShortNamesCache.getInstance( project );
    notNullManager = NullableNotNullManager.getInstance( project );
    javaPsiFacade = JavaPsiFacade.getInstance( project );
    elementFactory = JavaPsiFacade.getElementFactory( project );
    this.abstractSerializerType = abstractSerializerType;
    this.serializeToType = serializeToType;
    this.deserializeFromType = deserializeFromType;
  }

  @Override
  @Nonnull
  public PsiClass generate( @Nonnull final SerializerModel serializerModel, @Nonnull final PsiDirectory targetDir ) {
    final PsiFile psiFile = serializerModel.getClassToSerialize().getContainingFile();

    final PsiClass[] serializerClass = new PsiClass[1];
    new WriteCommandAction.Simple( serializerModel.getClassToSerialize().getProject(), psiFile ) {
      @Override
      protected void run() throws Throwable {
        serializerClass[0] = JavaDirectoryService.getInstance().createClass( targetDir, serializerModel.generateSerializerClassName() );
        fillSerializerClass( serializerModel, serializerClass[0] );

        //Now beautify the code
        codeStyleManager.reformat( serializerClass[0] );
        javaCodeStyleManager.shortenClassReferences( serializerClass[0] );
        javaCodeStyleManager.optimizeImports( serializerClass[0].getContainingFile() );
      }
    }.execute();

    return serializerClass[0];
  }

  @Nonnull
  public PsiClass fillSerializerClass( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    PsiClass classToSerialize = serializerModel.getClassToSerialize();
    addExtends( serializerClass, classToSerialize );

    addPropertyConstants( serializerModel, serializerClass );

    serializerClass.add( generateConstructor( serializerModel, serializerClass ) );
    serializerClass.add( generateSerializeMethod( serializerModel, serializerClass ) );
    serializerClass.add( generateDeserializeMethod( serializerModel, serializerClass ) );

    return serializerClass;
  }

  protected void addExtends( @Nonnull PsiClass serializerClass, @Nonnull PsiClass classToSerialize ) {
    //Add extends abstract base class
    PsiJavaCodeReferenceElement extendsRef = elementFactory.createReferenceFromText( abstractSerializerType + "<" + classToSerialize.getQualifiedName() + ">", classToSerialize );

    PsiReferenceList extendsList = serializerClass.getExtendsList();
    assert extendsList != null;
    extendsList.add( extendsRef );
  }

  protected void addPropertyConstants( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    for ( FieldToSerialize entry : serializerModel.getFieldToSerializeEntries() ) {
      serializerClass.add( elementFactory.createFieldFromText( "public static final String " + entry.getPropertyConstantName() + "=\"" + entry.getFieldName() + "\";", serializerClass ) );
    }
  }

  /**
   * Generates a constructor
   *
   * @param serializerModel the serializer model
   * @return the generated constructor
   */
  @Nonnull
  protected PsiMethod generateConstructor( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    @Nonnull Collection<? extends DelegatingSerializer> delegatingSerializers = serializerModel.getDelegatingSerializerEntries();

    StringBuilder constructorBuilder = new StringBuilder();
    constructorBuilder.append( "@javax.inject.Inject public " ).append( serializerClass.getName() ).append( "(" );

    //Add serializers
    for ( Iterator<? extends DelegatingSerializer> iterator = delegatingSerializers.iterator(); iterator.hasNext(); ) {
      DelegatingSerializer delegatingSerializer = iterator.next();

      PsiType delegatingSerializerType = delegatingSerializer.getDelegatingSerializerType();
      String paramName = delegatingSerializer.getSerializerParamName();

      constructorBuilder
        .append( notNull() )
        .append( delegatingSerializerType.getCanonicalText() ).append( " " ).append( paramName );

      if ( iterator.hasNext() ) {
        constructorBuilder.append( "," );
      }
    }

    callSuperConstructor( serializerModel, constructorBuilder );

    //register the delegating serializers
    for ( DelegatingSerializer entry : delegatingSerializers ) {
      constructorBuilder.append( "getDelegatesMappings().add( " ).append( entry.getSerializerParamName() ).append( " ).responsibleFor( " ).append( entry.getSerializedTypeBoxed() ).append( ".class )" ).append( ".map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );" );
    }
    if ( !delegatingSerializers.isEmpty() ) {
      constructorBuilder.append( "assert getDelegatesMappings().verify();" );
    }

    constructorBuilder.append( "}" );

    return elementFactory.createMethodFromText( constructorBuilder.toString(), null );
  }

  /**
   * Adds the super() call
   * @param serializerModel the model
   * @param constructorBuilder the builder for the constructor
   */
  protected abstract void callSuperConstructor( @Nonnull SerializerModel serializerModel, @Nonnull StringBuilder constructorBuilder );

  /**
   * Generates the serialize method
   * @param serializerModel the serializer model
   * @param serializerClass the serializer class
   * @return the generated method
   */
  @Nonnull
  protected PsiElement generateSerializeMethod( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    @Nonnull PsiClass classToSerialize = serializerModel.getClassToSerialize();
    @Nonnull Collection<? extends FieldToSerialize> fields = serializerModel.getFieldToSerializeEntries();

    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( "@Override public void serialize (" )
      .append( notNull() ).append( serializeToType ).append( " serializeTo, " )
      .append( notNull() )
      .append( classToSerialize.getQualifiedName() ).append( " object," )
      .append( notNull() )
      .append( "com.cedarsoft.version.Version formatVersion" )
      .append( ")throws java.io.IOException, com.cedarsoft.version.VersionException, " ).append( serializeExceptionType ).append( "{" );

    methodBuilder.append( "verifyVersionWritable( formatVersion );" );

    for ( FieldToSerialize field : fields ) {
      String serializeMethodName;
      if (field.isCollection()) {
        serializeMethodName = "serializeArray";
      }
      else {
        serializeMethodName = "serialize";
      }

      methodBuilder.append(serializeMethodName).append("(object.").append(field.getAccessor()).append(",").append(field.getFieldTypeBoxed()).append(".class, ").append(field.getPropertyConstantName()).append(" , serializeTo, formatVersion);");
    }

    methodBuilder.append( "}" );

    return elementFactory.createMethodFromText( methodBuilder.toString(), serializerClass );
  }

  /**
   * Generates the deserialize method
   * @param serializerModel the serializer model
   * @param serializerClass the serializer class
   * @return the generated method
   */
  @Nonnull
  protected PsiElement generateDeserializeMethod( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    @Nonnull PsiClass classToSerialize = serializerModel.getClassToSerialize();

    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( "@Override " ).append( notNull() ).append( "public " ).append( classToSerialize.getQualifiedName() ).append( " deserialize(" )
      .append( notNull() ).append( deserializeFromType ).append( " deserializeFrom, " )
      .append( notNull() )
      .append( "com.cedarsoft.version.Version formatVersion" ).append( ") throws java.io.IOException, com.cedarsoft.version.VersionException, " ).append( serializeExceptionType ).append( " {" );

    methodBuilder.append( "verifyVersionWritable( formatVersion );" );
    methodBuilder.append( "\n\n" );

    //Appends the deserialize statements
    appendDeserializeFieldStatements( serializerModel, methodBuilder );

    //Create the deserialized object using the constructor
    methodBuilder.append( classToSerialize.getQualifiedName() ).append( " object = new " ).append( classToSerialize.getQualifiedName() ).append( "(" );
    for ( Iterator<FieldToSerialize> iterator = findConstructorArgs( serializerModel.getFieldToSerializeEntries() ).iterator(); iterator.hasNext(); ) {
      FieldToSerialize constructorArgument = iterator.next();
      methodBuilder.append( constructorArgument.getFieldName() );

      if ( iterator.hasNext() ) {
        methodBuilder.append( "," );
      }
    }

    methodBuilder.append( ");" );

    //Setting the fields using setters
    for ( FieldToSerialize field : serializerModel.getFieldToSerializeEntries() ) {
      FieldSetter fieldSetter = field.getFieldSetter();
      if ( !fieldSetter.isSetterAccess() ) {
        continue;
      }

      methodBuilder.append( "object." ).append( ( ( FieldSetter.SetterFieldSetter ) fieldSetter ).getSetter() ).append( "(" ).append( field.getFieldName() ).append( ");" );
    }

    methodBuilder.append( " return object;" );
    methodBuilder.append( "}" );
    return elementFactory.createMethodFromText( methodBuilder.toString(), serializerClass );
  }

  /**
   * Append the deserialize field statements
   * @param serializerModel the serializer model
   * @param methodBody the method body
   */
  protected abstract void appendDeserializeFieldStatements( @Nonnull SerializerModel serializerModel, @Nonnull StringBuilder methodBody );

  @Nonnull
  protected String notNull() {
    return "@" + notNullManager.getDefaultNotNull() + " ";
  }

  /**
   * Creates the json serializedType for the given class name
   *
   * @param className the class name
   * @return the json serializedType
   */
  @Nonnull
  protected String createType( @Nonnull String className ) {
    String[] suggestedNames = javaCodeStyleManager.suggestVariableName(VariableKind.STATIC_FINAL_FIELD, className, null, null).names;
    //Find the longest name without any dots
    for (String suggestedName : suggestedNames) {
      if (suggestedName.contains(".")) {
        continue;
      }
      return suggestedName.toLowerCase().replace("_", "-");
    }

    //fallback
    return suggestedNames[0].toLowerCase(Locale.getDefault());
  }

  @Nonnull
  public static List<FieldToSerialize> findConstructorArgs( @Nonnull Collection<? extends FieldToSerialize> fields ) {
    SortedMap<Integer, FieldToSerialize> fieldsWithConstructor = new TreeMap<>();

    for ( FieldToSerialize entry : fields ) {
      FieldSetter fieldSetter = entry.getFieldSetter();
      if ( !fieldSetter.isConstructorAccess() ) {
        continue;
      }

      int index = ( ( FieldSetter.ConstructorFieldSetter ) fieldSetter ).getParameterIndex();
      @Nullable FieldToSerialize oldValue = fieldsWithConstructor.put( index, entry );
      if ( oldValue != null ) {
        throw new IllegalStateException( "Duplicate entries for index <" + index + ">: " + oldValue.getFieldName() + " - " + entry.getFieldName() );
      }
    }

    return ImmutableList.copyOf(fieldsWithConstructor.values());
  }
}
