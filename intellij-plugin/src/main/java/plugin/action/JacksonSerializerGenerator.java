package plugin.action;

import com.intellij.codeInsight.NullableNotNullManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
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
import com.intellij.util.Processor;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
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


    Collection<? extends FieldToSerializeEntry> fieldToSerializeEntries = calculateFieldToSerializeEntries( selectedFields );
    Collection<? extends DelegatingSerializerEntry> delegatingSerializers = calculateSerializerDelegates( fieldToSerializeEntries );

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

  @Nonnull
  private Collection<? extends FieldToSerializeEntry> calculateFieldToSerializeEntries( @Nonnull List<? extends PsiField> selectedFields ) {
    List<FieldToSerializeEntry> entries = new ArrayList<FieldToSerializeEntry>();

    for ( final PsiField selectedField : selectedFields ) {
      @javax.annotation.Nullable FieldToSerializeEntry entry = selectedField.getType().accept( new PsiTypeVisitor<FieldToSerializeEntry>() {
        @Nullable
        @Override
        public FieldToSerializeEntry visitClassType( PsiClassType classType ) {
          return new FieldToSerializeEntry( classType, selectedField.getName() );
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

    methodBuilder.append( "}" );

    return elementFactory.createMethodFromText( methodBuilder.toString(), serializerClass );
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
    private final PsiClassType fieldType;
    @Nonnull
    private final String fieldName;

    public FieldToSerializeEntry( @Nonnull PsiClassType fieldType, @Nonnull String fieldName ) {
      this.fieldType = fieldType;
      this.fieldName = fieldName;
    }

    @Nonnull
    public PsiClassType getFieldType() {
      return fieldType;
    }

    @Nonnull
    public String getFieldName() {
      return fieldName;
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

}
