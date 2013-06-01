package plugin.action;

import com.intellij.codeInsight.NullableNotNullManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiCapturedWildcardType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeVisitor;
import com.intellij.psi.PsiWildcardType;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.codeStyle.VariableKind;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.TypeConversionUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple class that generates a jackson serializer
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonSerializerGenerator {
  public static final String JACKSON_SERIALIZER_IFACE_NAME = "com.cedarsoft.serialization.jackson.JacksonSerializer";
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
  public PsiClass generate( @Nonnull final PsiClass classToSerialize, @Nonnull final List<? extends PsiField> selectedFields ) {
    final PsiFile psiFile = classToSerialize.getContainingFile();

    //The directory the serializer is generated in
    final PsiDirectory directory = selectTargetDir( classToSerialize );

    final PsiClass[] serializerClass = new PsiClass[1];
    new WriteCommandAction.Simple( classToSerialize.getProject(), psiFile ) {
      @Override
      protected void run() throws Throwable {
        serializerClass[0] = JavaDirectoryService.getInstance().createClass( directory, generateSerializerClassName( classToSerialize.getName() ) );
        fillSerializerClass( classToSerialize, selectedFields, serializerClass[0] );

        //Now beautify the code
        codeStyleManager.reformat( serializerClass[0] );
        javaCodeStyleManager.shortenClassReferences( serializerClass[0] );
        javaCodeStyleManager.optimizeImports( serializerClass[0].getContainingFile() );
      }
    }.execute();

    return serializerClass[0];
  }

  @Nonnull
  protected String generateSerializerClassName( @Nonnull String psiClassName ) {
    return psiClassName + "Serializer";
  }

  @Nonnull
  protected PsiDirectory selectTargetDir( @Nonnull PsiClass psiClass ) {
    //TODO implement me!
    return psiClass.getContainingFile().getParent();
  }

  @Nonnull
  public PsiClass fillSerializerClass( @Nonnull PsiClass classToSerialize, @Nonnull List<? extends PsiField> selectedFields, @Nonnull PsiClass serializerClass ) {
    //Add extends abstract base class
    {
      PsiJavaCodeReferenceElement extendsRef = elementFactory.createReferenceFromText( "com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<" + classToSerialize.getName() + ">", classToSerialize );

      PsiReferenceList extendsList = serializerClass.getExtendsList();
      assert extendsList != null;
      extendsList.add( extendsRef );
    }


    FieldAccessProvider fieldAccessProvider = new FieldAccessProvider( classToSerialize );

    Collection<? extends FieldToSerializeEntry> fieldToSerializeEntries = calculateFieldToSerializeEntries( selectedFields, fieldAccessProvider );
    Collection<? extends DelegatingSerializerEntry> delegatingSerializers = calculateSerializerDelegates( fieldToSerializeEntries );

    addPropertyConstants( fieldToSerializeEntries, serializerClass );

    serializerClass.add( generateConstructor( serializerClass, delegatingSerializers ) );
    serializerClass.add( generateSerializeMethod( classToSerialize, serializerClass, fieldToSerializeEntries ) );
    serializerClass.add( generateDeserializeMethod( classToSerialize, serializerClass, fieldToSerializeEntries ) );


    //StringBuilder builder = new StringBuilder();
    //builder.append( "public void deserializeStuff(){" )
    //  .append( psiClass.getName() ).append( ".class.getName();" )
    //  .append( "}" );
    //
    //PsiElement method = psiClass.add( elementFactory.createMethodFromText( builder.toString(), psiClass ) );
    //
    //codeStyleManager.shortenClassReferences( method );


    PsiReferenceList implementsList = classToSerialize.getImplementsList();
    if ( implementsList == null ) {
      throw new IllegalStateException( "no implements list found" );
    }

    //PsiElement implementsReference = implementsList.add( elementFactory.createReferenceFromText( "Comparable<" + psiClass.getQualifiedName() + ">", psiClass ) );
    //codeStyleManager.shortenClassReferences( implementsReference );


    //PsiDirectory srcDir = psiClass.getContainingFile().getContainingDirectory();
    //PsiPackage srcPackage = JavaDirectoryService.getInstance().getPackage( srcDir );


    //PsiClass serializerClass = elementFactory.createClass( psiClass.getQualifiedName() + "Serializer" );

    return serializerClass;
  }

  @javax.annotation.Nullable
  private static PsiMethod findLongestConstructor( @Nonnull PsiClass classToSerialize ) {
    PsiMethod bestConstructor = null;

    for ( PsiMethod constructor : classToSerialize.getConstructors() ) {
      if ( bestConstructor == null ) {
        bestConstructor = constructor;
        continue;
      }

      if ( constructor.getParameterList().getParameters().length > bestConstructor.getParameterList().getParameters().length ) {
        bestConstructor = constructor;
      }
    }

    return bestConstructor;
  }

  private void addPropertyConstants( @Nonnull Collection<? extends FieldToSerializeEntry> fieldToSerializeEntries, @Nonnull PsiClass serializerClass ) {
    for ( FieldToSerializeEntry entry : fieldToSerializeEntries ) {
      serializerClass.add( elementFactory.createFieldFromText( "public static final String " + entry.getPropertyConstantName() + "=\"" + entry.getFieldName() + "\";", serializerClass ) );
    }
  }

  @Nonnull
  private Collection<? extends FieldToSerializeEntry> calculateFieldToSerializeEntries( @Nonnull List<? extends PsiField> selectedFields, @Nonnull final FieldAccessProvider fieldAccessProvider ) {
    List<FieldToSerializeEntry> entries = new ArrayList<FieldToSerializeEntry>();

    for ( final PsiField selectedField : selectedFields ) {
      @javax.annotation.Nullable FieldToSerializeEntry entry = selectedField.getType().accept( new PsiTypeVisitor<FieldToSerializeEntry>() {
        @Nullable
        @Override
        public FieldToSerializeEntry visitClassType( PsiClassType classType ) {
          return new FieldToSerializeEntry( classType, selectedField.getName(), fieldAccessProvider.getFieldAccess( selectedField ) );
        }

        @Nullable
        @Override
        public FieldToSerializeEntry visitPrimitiveType( PsiPrimitiveType primitiveType ) {
          return new FieldToSerializeEntry( primitiveType, selectedField.getName(), fieldAccessProvider.getFieldAccess( selectedField ) );
        }

        @Nullable
        @Override
        public FieldToSerializeEntry visitWildcardType( PsiWildcardType wildcardType ) {
          System.out.println( "--> FIX ME: " + wildcardType );
          return super.visitWildcardType( wildcardType );
        }

        @Nullable
        @Override
        public FieldToSerializeEntry visitCapturedWildcardType( PsiCapturedWildcardType capturedWildcardType ) {
          System.out.println( "--> FIX ME: " + capturedWildcardType );
          return super.visitCapturedWildcardType( capturedWildcardType );
        }
      } );


      if ( entry == null ) {
        throw new IllegalStateException( "No entry created for <" + selectedField + ">" );
      }
      entries.add( entry );
    }

    return entries;
  }

  @Nonnull
  private Collection<? extends DelegatingSerializerEntry> calculateSerializerDelegates( @Nonnull Collection<? extends FieldToSerializeEntry> fieldEntries ) {
    Map<PsiType, DelegatingSerializerEntry> delegatingSerializersMap = new LinkedHashMap<PsiType, DelegatingSerializerEntry>();

    for ( FieldToSerializeEntry fieldEntry : fieldEntries ) {
      if ( fieldEntry.isPrimitive() ) {
        continue;
      }

      DelegatingSerializerEntry entry = new DelegatingSerializerEntry( fieldEntry.getFieldType() );
      delegatingSerializersMap.put( entry.getSerializedType(), entry );
    }

    return delegatingSerializersMap.values();
  }

  /**
   * Generates a constructor
   *
   * @param serializerClass             the serializer class
   * @param delegatingSerializerEntries the delegating serializers
   * @return the generated constructor
   */
  @Nonnull
  private PsiMethod generateConstructor( @Nonnull PsiClass serializerClass, @Nonnull Collection<? extends DelegatingSerializerEntry> delegatingSerializerEntries ) {
    StringBuilder constructorBuilder = new StringBuilder();
    constructorBuilder.append( "public " ).append( serializerClass.getName() ).append( "(" );

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
      constructorBuilder.append( "getDelegatesMappings().add( " ).append( entry.getSerializerParamName() ).append( " ).responsibleFor( " ).append( entry.getSerializedType().getCanonicalText() ).append( ".class )" ).append( ".map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );" );
    }
    if ( !delegatingSerializerEntries.isEmpty() ) {
      constructorBuilder.append( "assert getDelegatesMappings().verify();" );
    }

    constructorBuilder.append( "}" );

    return elementFactory.createMethodFromText( constructorBuilder.toString(), null );
  }


  @Nonnull
  private PsiElement generateSerializeMethod( @Nonnull PsiClass classToSerialize, @Nonnull PsiClass serializerClass, @Nonnull Collection<? extends FieldToSerializeEntry> fields ) {
    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( "@Override public void serialize (" )
      .append( notNull() )
      .append( "com.fasterxml.jackson.core.JsonGenerator serializeTo, " )
      .append( notNull() )
      .append( classToSerialize.getQualifiedName() ).append( " object," )
      .append( notNull() )
      .append( "com.cedarsoft.version.Version formatVersion" )
      .append( "){" );

    methodBuilder.append( "verifyVersionWritable( formatVersion );" );

    for ( FieldToSerializeEntry field : fields ) {
      methodBuilder.append( "serialize(object." ).append( field.getAccessor() ).append( "," ).append( field.getFieldType().getCanonicalText() ).append( ".class, " ).append( field.getPropertyConstantName() ).append( " , serializeTo, formatVersion);" );
    }

    methodBuilder.append( "}" );

    return elementFactory.createMethodFromText( methodBuilder.toString(), serializerClass );
  }

  @Nonnull
  private PsiElement generateDeserializeMethod( @Nonnull PsiClass classToSerialize, @Nonnull PsiClass serializerClass, @Nonnull Collection<? extends FieldToSerializeEntry> fields ) {
    StringBuilder methodBuilder = new StringBuilder();

    methodBuilder.append( "@Override public void deserialize(" )
      .append( notNull() )
      .append( "com.fasterxml.jackson.core.JsonParser deserializeFrom, " )
      .append( notNull() )
      .append( "com.cedarsoft.version.Version formatVersion" )
      .append( "){" );

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
          .append( "parser.nextToken( com.fasterxml.jackson.core.JsonToken.START_OBJECT );" )

          .append( field.getFieldName() ).append( "=deserialize(" )
          .append( field.getFieldType().getCanonicalText() ).append( ".class" )
          .append( ", formatVersion, deserializeFrom" )
          .append( ");" )

          .append( "continue;" )
          .append( "}" )
        ;
      }

      methodBuilder.append( "}" );
    }

    methodBuilder.append( "\n\n" );

    //Verify deserialization
    for ( FieldToSerializeEntry field : fields ) {
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
      FieldAccess fieldAccess = field.getFieldAccess();
      if ( !fieldAccess.isSetterAccess() ) {
        continue;
      }

      methodBuilder.append( "object." ).append( ( ( SetterFieldAccess ) fieldAccess ).getSetter() ).append( "(" ).append( field.getFieldName() ).append( ");" );
    }

    methodBuilder.append( " return object;" );
    methodBuilder.append( "}" );
    return elementFactory.createMethodFromText( methodBuilder.toString(), serializerClass );
  }

  @Nonnull
  private List<FieldToSerializeEntry> findConstructorArgs( @Nonnull Collection<? extends FieldToSerializeEntry> fields ) {
    Map<Integer, FieldToSerializeEntry> fieldsWithConstructor = new HashMap<Integer, FieldToSerializeEntry>();

    for ( FieldToSerializeEntry entry : fields ) {
      FieldAccess fieldAccess = entry.getFieldAccess();
      if ( !fieldAccess.isConstructorAccess() ) {
        continue;
      }

      int index = ( ( ConstructorFieldAccess ) fieldAccess ).getParameterIndex();
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
   * Returns the jackson serializer for the given serializedType
   *
   * @param typeToSerialize the serializedType that shall be serialized
   * @return the found jackson serializer
   */
  @Nonnull
  protected PsiType findJacksonSerializerFor( @Nonnull final PsiType typeToSerialize ) {
    //Fix scope: GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(  )
    PsiClass serializerClass = javaPsiFacade.findClass( JACKSON_SERIALIZER_IFACE_NAME, GlobalSearchScope.allScope( project ) );
    if ( serializerClass != null ) {
      final PsiType jacksonSerializerWithTypeParam = elementFactory.createTypeFromText( JACKSON_SERIALIZER_IFACE_NAME + "<" + typeToSerialize.getCanonicalText() + ">", null );

      final PsiType[] foundSerializerType = new PsiType[1];
      ClassInheritorsSearch.search( serializerClass ).forEach( new Processor<PsiClass>() {
        @Override
        public boolean process( PsiClass psiClass ) {
          //Skip interfaces and abstract classes
          if ( psiClass.isInterface() || psiClass.hasModifierProperty( PsiModifier.ABSTRACT ) ) {
            return true;
          }

          //Is it a serializer?
          PsiClassType currentSerializerType = elementFactory.createType( psiClass );
          if ( !jacksonSerializerWithTypeParam.isAssignableFrom( currentSerializerType ) ) {
            return true;
          }

          //Verify the exact type param
          PsiClassType jacksonSerializerImplType = findJacksonSerializerImplFor( currentSerializerType );
          if ( jacksonSerializerImplType == null ) {
            return true;
          }

          PsiType[] parameters = jacksonSerializerImplType.getParameters();
          if ( parameters.length != 1 ) {
            return true;
          }

          PsiType parameter = parameters[0];
          if ( !parameter.equals( typeToSerialize ) ) {
            return true;
          }

          foundSerializerType[0] = currentSerializerType;
          return false;
        }

        @javax.annotation.Nullable
        private PsiClassType findJacksonSerializerImplFor( @Nonnull PsiClassType serializerType ) {
          for ( PsiType superType : serializerType.getSuperTypes() ) {
            PsiClass psiClass = ( ( PsiClassType ) superType ).resolve();
            assert psiClass != null;
            String qualifiedName = psiClass.getQualifiedName();

            if ( JACKSON_SERIALIZER_IFACE_NAME.equals( qualifiedName ) ) {
              return ( PsiClassType ) superType;
            }

            @javax.annotation.Nullable PsiClassType oneDown = findJacksonSerializerImplFor( ( PsiClassType ) superType );
            if ( oneDown != null ) {
              return oneDown;
            }
          }

          return null;
        }
      } );

      if ( foundSerializerType[0] != null ) {
        return foundSerializerType[0];
      }
    }

    //Fallback: Create a new pseudo serializer class
    return elementFactory.createTypeByFQClassName( typeToSerialize.getPresentableText() + "Serializer" );
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

  public class FieldToSerializeEntry {
    @Nonnull
    private final PsiType fieldType;
    @Nonnull
    private final String fieldName;
    @Nonnull
    private final FieldAccess fieldAccess;
    @Nonnull
    private final String accessor;
    @Nonnull
    private final String propertyConstant;
    @Nonnull
    private final String defaultValue;

    public FieldToSerializeEntry( @Nonnull PsiType fieldType, @Nonnull String fieldName, @Nonnull FieldAccess fieldAccess ) {
      this.fieldType = fieldType;
      this.fieldName = fieldName;
      this.fieldAccess = fieldAccess;

      this.accessor = "get" + StringUtil.capitalizeWithJavaBeanConvention( fieldName ) + "()";
      this.propertyConstant = "PROPERTY_" + javaCodeStyleManager.suggestVariableName( VariableKind.STATIC_FINAL_FIELD, fieldName, null, null ).names[0];

      if ( isPrimitive() ) {
        defaultValue = "-1";
      } else {
        defaultValue = "null";
      }
    }

    @Nonnull
    public FieldAccess getFieldAccess() {
      return fieldAccess;
    }

    @Nonnull
    public PsiType getFieldType() {
      return fieldType;
    }

    @Nonnull
    public String getFieldName() {
      return fieldName;
    }

    @Nonnull
    public String getAccessor() {
      return accessor;
    }

    @Nonnull
    public String getPropertyConstantName() {
      return propertyConstant;
    }

    public final boolean isPrimitive() {
      return TypeConversionUtil.isPrimitiveAndNotNull( fieldType );
    }

    @Nonnull
    public String getDefaultValue() {
      return defaultValue;
    }
  }

  public class DelegatingSerializerEntry {
    @Nonnull
    private final PsiType serializedType;
    @Nonnull
    private final PsiType delegatingSerializerType;
    @Nonnull
    private final String serializerParamName;

    public DelegatingSerializerEntry( @Nonnull PsiType typeToSerialize ) {
      this.serializedType = typeToSerialize;
      delegatingSerializerType = findJacksonSerializerFor( typeToSerialize );
      serializerParamName = javaCodeStyleManager.suggestVariableName( VariableKind.PARAMETER, null, null, delegatingSerializerType ).names[0];
    }

    @Nonnull
    public PsiType getDelegatingSerializerType() {
      return delegatingSerializerType;
    }

    @Nonnull
    public PsiType getSerializedType() {
      return serializedType;
    }

    @Nonnull
    public String getSerializerParamName() {
      return serializerParamName;
    }
  }

  public class FieldAccessProvider {
    @Nonnull
    private final PsiClass classToSerialize;
    @Nullable
    private final PsiMethod constructor;

    public FieldAccessProvider( @Nonnull PsiClass classToSerialize ) {
      this.classToSerialize = classToSerialize;
      constructor = findLongestConstructor( classToSerialize );
    }

    @Nullable
    public PsiMethod getConstructor() {
      return constructor;
    }

    @Nonnull
    public PsiClass getClassToSerialize() {
      return classToSerialize;
    }

    @Nonnull
    public FieldAccess getFieldAccess( @Nonnull PsiField field ) {
      @Nullable ConstructorFieldAccess constructorFieldAccess = getConstructorAccess( field );
      if ( constructorFieldAccess != null ) {
        return constructorFieldAccess;
      }

      return findGetter( field );
    }

    @Nullable
    private ConstructorFieldAccess getConstructorAccess( @Nonnull PsiField field ) {
      if ( constructor == null ) {
        return null;
      }
      for ( PsiParameter psiParameter : constructor.getParameterList().getParameters() ) {
        PsiType type = psiParameter.getType();
        String name = psiParameter.getName();

        if ( !field.getName().equals( name ) ) {
          continue;
        }

        if ( !field.getType().equals( type ) ) {
          continue;
        }

        return new ConstructorFieldAccess( constructor.getParameterList().getParameterIndex( psiParameter ) );
      }

      return null;
    }

    @Nonnull
    private SetterFieldAccess findGetter( @Nonnull PsiField field ) {
      @Nullable PsiMethod setter = PropertyUtil.findPropertySetter( classToSerialize, field.getName(), false, true );
      if ( setter != null ) {
        return new SetterFieldAccess( setter.getName() );
      }
      return new SetterFieldAccess( PropertyUtil.suggestSetterName( field.getName() ) );
    }
  }

  public interface FieldAccess {
    boolean isConstructorAccess();

    boolean isSetterAccess();
  }

  public static class SetterFieldAccess implements FieldAccess {
    @Nonnull
    private final String setter;

    public SetterFieldAccess( @Nonnull String setter ) {
      this.setter = setter;
    }

    @Nonnull
    public String getSetter() {
      return setter;
    }

    @Override
    public boolean isConstructorAccess() {
      return false;
    }

    @Override
    public boolean isSetterAccess() {
      return true;
    }
  }

  public static class ConstructorFieldAccess implements FieldAccess {
    private final int parameterIndex;

    public ConstructorFieldAccess( int parameterIndex ) {
      this.parameterIndex = parameterIndex;
    }

    public int getParameterIndex() {
      return parameterIndex;
    }

    @Override
    public boolean isConstructorAccess() {
      return true;
    }

    @Override
    public boolean isSetterAccess() {
      return false;
    }
  }
}
