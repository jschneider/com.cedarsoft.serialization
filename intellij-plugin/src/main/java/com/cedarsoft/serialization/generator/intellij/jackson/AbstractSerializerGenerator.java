package com.cedarsoft.serialization.generator.intellij.jackson;

import com.cedarsoft.serialization.generator.intellij.SerializerGenerator;
import com.cedarsoft.serialization.generator.intellij.model.DelegatingSerializer;
import com.cedarsoft.serialization.generator.intellij.model.FieldSetter;
import com.cedarsoft.serialization.generator.intellij.model.FieldToSerialize;
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

  protected AbstractSerializerGenerator( @Nonnull Project project, @Nonnull String abstractSerializerType, @Nonnull String serializeToType, @Nonnull String deserializeFromType ) {
    this.project = project;
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

    constructorBuilder.append( "){" )
      .append( "super(\"" ).append( createType( serializerModel.getClassToSerializeQualifiedName() ) ).append( "\", com.cedarsoft.version.VersionRange.from(1,0,0).to());" );


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
   * Generates the serialize method
   * @param serializerModel the serializer model
   * @param serializerClass the serializer class
   * @return the generated method
   */
  @Nonnull
  protected abstract PsiElement generateSerializeMethod( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass );

  /**
   * Generates the deserialize method
   * @param serializerModel the serializer model
   * @param serializerClass the serializer class
   * @return the generated method
   */
  @Nonnull
  protected abstract PsiElement generateDeserializeMethod( @Nonnull SerializerModel serializerModel, @Nonnull PsiClass serializerClass );

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
    return javaCodeStyleManager.suggestVariableName( VariableKind.STATIC_FINAL_FIELD, className, null, null ).names[0].toLowerCase( Locale.getDefault() );
  }

  @Nonnull
  public static List<FieldToSerialize> findConstructorArgs( @Nonnull Collection<? extends FieldToSerialize> fields ) {
    Map<Integer, FieldToSerialize> fieldsWithConstructor = new HashMap<Integer, FieldToSerialize>();

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

    List<FieldToSerialize> argsSorted = new ArrayList<FieldToSerialize>();

    int index = 0;
    while ( !fieldsWithConstructor.isEmpty() ) {
      @Nullable FieldToSerialize entry = fieldsWithConstructor.remove( index );
      if ( entry == null ) {
        throw new IllegalStateException( "No entry found for index <" + index + ">" );
      }
      argsSorted.add( entry );
      index++;
    }

    return argsSorted;
  }
}
