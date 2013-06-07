package com.cedarsoft.serialization.generator.intellij;

import com.cedarsoft.serialization.generator.intellij.model.DelegatingSerializerEntry;
import com.cedarsoft.serialization.generator.intellij.model.FieldSetter;
import com.cedarsoft.serialization.generator.intellij.model.FieldToSerializeEntry;
import com.cedarsoft.serialization.generator.intellij.model.SerializerModel;
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

/**
 * A simple class that generates a jackson serializer
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonSerializerGenerator {
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

  public JacksonSerializerGenerator( @Nonnull Project project ) {
    this.project = project;

    codeStyleManager = CodeStyleManager.getInstance( project );
    javaCodeStyleManager = JavaCodeStyleManager.getInstance( project );
    elementFactory = JavaPsiFacade.getElementFactory( project );
    javaPsiFacade = JavaPsiFacade.getInstance( project );
    shortNamesCache = PsiShortNamesCache.getInstance( project );
    notNullManager = NullableNotNullManager.getInstance( project );
  }

  @Nonnull
  public PsiClass generate( @Nonnull final SerializerModel serializerModel ) {
    final PsiFile psiFile = serializerModel.getClassToSerialize().getContainingFile();

    //The directory the serializer is generated in
    final PsiDirectory directory = selectTargetDir( serializerModel.getClassToSerialize() );

    final PsiClass[] serializerClass = new PsiClass[1];
    new WriteCommandAction.Simple( serializerModel.getClassToSerialize().getProject(), psiFile ) {
      @Override
      protected void run() throws Throwable {
        serializerClass[0] = JavaDirectoryService.getInstance().createClass( directory, serializerModel.generateSerializerClassName() );
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
  protected PsiDirectory selectTargetDir( @Nonnull PsiClass psiClass ) {
    //TODO implement me!
    return psiClass.getContainingFile().getParent();
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

  private void addExtends( @Nonnull PsiClass serializerClass, @Nonnull PsiClass classToSerialize ) {
    //Add extends abstract base class
    {
      PsiJavaCodeReferenceElement extendsRef = elementFactory.createReferenceFromText( "com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<" + classToSerialize.getQualifiedName() + ">", classToSerialize );

      PsiReferenceList extendsList = serializerClass.getExtendsList();
      assert extendsList != null;
      extendsList.add( extendsRef );
    }
  }

  private void addPropertyConstants( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    for ( FieldToSerializeEntry entry : serializerModel.getFieldToSerializeEntries() ) {
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
  private PsiMethod generateConstructor( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    @Nonnull Collection<? extends DelegatingSerializerEntry> delegatingSerializerEntries = serializerModel.getDelegatingSerializerEntries();

    StringBuilder constructorBuilder = new StringBuilder();
    constructorBuilder.append( "@javax.inject.Inject public " ).append( serializerClass.getName() ).append( "(" );

    //Add serializers
    for ( Iterator<? extends DelegatingSerializerEntry> iterator = delegatingSerializerEntries.iterator(); iterator.hasNext(); ) {
      DelegatingSerializerEntry delegatingSerializerEntry = iterator.next();

      PsiType delegatingSerializerType = delegatingSerializerEntry.getDelegatingSerializerType();
      String paramName = delegatingSerializerEntry.getSerializerParamName();

      constructorBuilder
        .append( notNull() )
        .append( delegatingSerializerType.getCanonicalText() ).append( " " ).append( paramName );

      if ( iterator.hasNext() ) {
        constructorBuilder.append( "," );
      }
    }

    constructorBuilder.append( "){" )
      .append( "super(\"" ).append( createType( serializerClass.getName() ) ).append( "\", com.cedarsoft.version.VersionRange.from(1,0,0).to());" );


    //register the delegating serializers
    for ( DelegatingSerializerEntry entry : delegatingSerializerEntries ) {
      constructorBuilder.append( "getDelegatesMappings().add( " ).append( entry.getSerializerParamName() ).append( " ).responsibleFor( " ).append( entry.getSerializedTypeBoxed() ).append( ".class )" ).append( ".map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );" );
    }
    if ( !delegatingSerializerEntries.isEmpty() ) {
      constructorBuilder.append( "assert getDelegatesMappings().verify();" );
    }

    constructorBuilder.append( "}" );

    return elementFactory.createMethodFromText( constructorBuilder.toString(), null );
  }


  @Nonnull
  private PsiElement generateSerializeMethod( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    @Nonnull PsiClass classToSerialize = serializerModel.getClassToSerialize();
    @Nonnull Collection<? extends FieldToSerializeEntry> fields = serializerModel.getFieldToSerializeEntries();

    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( "@Override public void serialize (" )
      .append( notNull() )
      .append( "com.fasterxml.jackson.core.JsonGenerator serializeTo, " )
      .append( notNull() )
      .append( classToSerialize.getQualifiedName() ).append( " object," )
      .append( notNull() )
      .append( "com.cedarsoft.version.Version formatVersion" )
      .append( ")throws java.io.IOException, com.cedarsoft.version.VersionException{" );

    methodBuilder.append( "verifyVersionWritable( formatVersion );" );

    for ( FieldToSerializeEntry field : fields ) {
      methodBuilder.append( "serialize(object." ).append( field.getAccessor() ).append( "," ).append( field.getFieldTypeBoxed() ).append( ".class, " ).append( field.getPropertyConstantName() ).append( " , serializeTo, formatVersion);" );
    }

    methodBuilder.append( "}" );

    return elementFactory.createMethodFromText( methodBuilder.toString(), serializerClass );
  }

  @Nonnull
  private PsiElement generateDeserializeMethod( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass ) {
    @Nonnull PsiClass classToSerialize = serializerModel.getClassToSerialize();
    @Nonnull Collection<? extends FieldToSerializeEntry> fields = serializerModel.getFieldToSerializeEntries();

    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( "@Override public " ).append( notNull() ).append( classToSerialize.getQualifiedName() ).append( " deserialize(" )
      .append( notNull() )
      .append( "com.fasterxml.jackson.core.JsonParser deserializeFrom, " )
      .append( notNull() )
      .append( "com.cedarsoft.version.Version formatVersion" )
      .append( ") throws java.io.IOException, com.cedarsoft.version.VersionException {" );

    methodBuilder.append( "verifyVersionWritable( formatVersion );" );
    methodBuilder.append( "\n\n" );

    //Declare the fields
    for ( FieldToSerializeEntry field : fields ) {
      methodBuilder.append( field.getFieldType().getCanonicalText() ).append( " " ).append( field.getFieldName() ).append( "=" ).append( field.getDefaultValue() ).append( ";" );

    }

    methodBuilder.append( "\n\n" );

    {
      //While for fields
      methodBuilder.append( "com.cedarsoft.serialization.jackson.JacksonParserWrapper parser = new com.cedarsoft.serialization.jackson.JacksonParserWrapper( deserializeFrom );" +
                              "while ( parser.nextToken() == com.fasterxml.jackson.core.JsonToken.FIELD_NAME ) {" +
                              "String currentName = parser.getCurrentName();\n\n" );

      //add the ifs for the field names
      for ( FieldToSerializeEntry field : fields ) {
        methodBuilder.append( "if ( currentName.equals( " ).append( field.getPropertyConstantName() ).append( " ) ) {" )
          .append( "parser.nextToken();" )

          .append( field.getFieldName() ).append( "=deserialize(" )
          .append( field.getFieldTypeBoxed() ).append( ".class" )
          .append( ", formatVersion, deserializeFrom" )
          .append( ");" )

          .append( "continue;" )
          .append( "}" )
        ;
      }

      methodBuilder.append( "throw new IllegalStateException( \"Unexpected field reached <\" + currentName + \">\" );" );
      methodBuilder.append( "}" );
    }

    methodBuilder.append( "\n\n" );

    //Verify deserialization
    for ( FieldToSerializeEntry field : fields ) {
      if ( !field.shallVerifyDeserialized() ) {
        continue;
      }

      methodBuilder.append( "parser.verifyDeserialized(" ).append( field.getFieldName() ).append( "," ).append( field.getPropertyConstantName() ).append( ");" );
      if ( !field.isPrimitive() ) {
        methodBuilder.append( "assert " ).append( field.getFieldName() ).append( " !=" ).append( field.getDefaultValue() ).append( ";" );
      }
    }

    methodBuilder.append( "\n\n" );

    //clean up
    methodBuilder.append( "parser.ensureObjectClosed();" );
    methodBuilder.append( "\n\n" );

    //Create the deserialized object


    methodBuilder.append( classToSerialize.getQualifiedName() ).append( " object = new " ).append( classToSerialize.getQualifiedName() ).append( "(" );

    List<FieldToSerializeEntry> constructorArguments = findConstructorArgs( fields );

    for ( Iterator<FieldToSerializeEntry> iterator = constructorArguments.iterator(); iterator.hasNext(); ) {
      FieldToSerializeEntry constructorArgument = iterator.next();
      methodBuilder.append( constructorArgument.getFieldName() );

      if ( iterator.hasNext() ) {
        methodBuilder.append( "," );
      }
    }

    methodBuilder.append( ");" );

    //Adding the setters
    for ( FieldToSerializeEntry field : fields ) {
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

  @Nonnull
  private static List<FieldToSerializeEntry> findConstructorArgs( @Nonnull Collection<? extends FieldToSerializeEntry> fields ) {
    Map<Integer, FieldToSerializeEntry> fieldsWithConstructor = new HashMap<Integer, FieldToSerializeEntry>();

    for ( FieldToSerializeEntry entry : fields ) {
      FieldSetter fieldSetter = entry.getFieldSetter();
      if ( !fieldSetter.isConstructorAccess() ) {
        continue;
      }

      int index = ( ( FieldSetter.ConstructorFieldSetter ) fieldSetter ).getParameterIndex();
      @Nullable FieldToSerializeEntry oldValue = fieldsWithConstructor.put( index, entry );
      if ( oldValue != null ) {
        throw new IllegalStateException( "Duplicate entries for index <" + index + ">: " + oldValue.getFieldName() + " - " + entry.getFieldName() );
      }
    }


    List<FieldToSerializeEntry> argsSorted = new ArrayList<FieldToSerializeEntry>();

    int index = 0;
    while ( !fieldsWithConstructor.isEmpty() ) {
      @Nullable FieldToSerializeEntry entry = fieldsWithConstructor.remove( index );
      if ( entry == null ) {
        throw new IllegalStateException( "No entry found for index <" + index + ">" );
      }
      argsSorted.add( entry );
      index++;
    }

    return argsSorted;
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
