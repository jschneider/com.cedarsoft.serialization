package plugin.action;

import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiCapturedWildcardType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDirectory;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

  public JacksonSerializerGenerator( @Nonnull Project project ) {
    this.project = project;

    codeStyleManager = CodeStyleManager.getInstance( project );
    javaCodeStyleManager = JavaCodeStyleManager.getInstance( project );
    elementFactory = JavaPsiFacade.getElementFactory( project );
    javaPsiFacade = JavaPsiFacade.getInstance( project );
    shortNamesCache = PsiShortNamesCache.getInstance( project );
  }

  public void generate( @Nonnull final PsiClass psiClass, @Nonnull final List<? extends PsiField> selectedFields ) {
    final PsiFile psiFile = psiClass.getContainingFile();

    //The directory the serializer is generated in
    final PsiDirectory directory = selectTargetDir( psiClass );

    new WriteCommandAction.Simple( psiClass.getProject(), psiFile ) {
      @Override
      protected void run() throws Throwable {
        PsiClass serializerClass = JavaDirectoryService.getInstance().createClass( directory, generateSerializerClassName( psiClass.getName() ) );
        fillSerializerClass( psiClass, selectedFields, serializerClass );

        //Now beautify the code
        codeStyleManager.reformat( serializerClass );
        javaCodeStyleManager.shortenClassReferences( serializerClass );
        javaCodeStyleManager.optimizeImports( serializerClass.getContainingFile() );

        Editor editor = CodeInsightUtil.positionCursor( getProject(), serializerClass.getContainingFile(), serializerClass.getLBrace() );
      }
    }.execute();
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
  public PsiClass fillSerializerClass( @Nonnull PsiClass psiClass, @Nonnull List<? extends PsiField> selectedFields, @Nonnull PsiClass serializerClass ) {
    //Add extends abstract base class
    {
      PsiJavaCodeReferenceElement extendsRef = elementFactory.createReferenceFromText( "com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<" + psiClass.getName() + ">", psiClass );

      PsiReferenceList extendsList = serializerClass.getExtendsList();
      assert extendsList != null;
      extendsList.add( extendsRef );
    }


    List<DelegatingSerializerEntry> delegatingSerializers = new ArrayList<DelegatingSerializerEntry>();
    for ( PsiField selectedField : selectedFields ) {
      @javax.annotation.Nullable DelegatingSerializerEntry entry = selectedField.getType().accept( new PsiTypeVisitor<DelegatingSerializerEntry>() {
        @Nullable
        @Override
        public DelegatingSerializerEntry visitClassType( PsiClassType classType ) {
          return new DelegatingSerializerEntry( classType );
        }

        @Nullable
        @Override
        public DelegatingSerializerEntry visitWildcardType( PsiWildcardType wildcardType ) {
          System.out.println( "--> FIX ME: " + wildcardType );
          return super.visitWildcardType( wildcardType );
        }

        @Nullable
        @Override
        public DelegatingSerializerEntry visitCapturedWildcardType( PsiCapturedWildcardType capturedWildcardType ) {
          System.out.println( "--> FIX ME: " + capturedWildcardType );
          return super.visitCapturedWildcardType( capturedWildcardType );
        }
      } );

      if ( entry != null ) {
        delegatingSerializers.add( entry );
      }
    }
    //TODO fill delegating serialiezers

    PsiMethod constructor = generateConstructor( serializerClass, delegatingSerializers );

    serializerClass.add( constructor );


    //StringBuilder builder = new StringBuilder();
    //builder.append( "public void deserializeStuff(){" )
    //  .append( psiClass.getName() ).append( ".class.getName();" )
    //  .append( "}" );
    //
    //PsiElement method = psiClass.add( elementFactory.createMethodFromText( builder.toString(), psiClass ) );
    //
    //codeStyleManager.shortenClassReferences( method );


    PsiReferenceList implementsList = psiClass.getImplementsList();
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

  /**
   * Generates a constructor
   *
   * @param serializerClass       the serializer class
   * @param delegatingSerializers the delegating serializers
   * @return the generated constructor
   */
  @Nonnull
  private PsiMethod generateConstructor( @Nonnull PsiClass serializerClass, @Nonnull List<DelegatingSerializerEntry> delegatingSerializers ) {
    StringBuilder constructorBuilder = new StringBuilder();
    constructorBuilder.append( "public " ).append( serializerClass.getName() ).append( "(" );

    //Add serializers
    for ( Iterator<DelegatingSerializerEntry> iterator = delegatingSerializers.iterator(); iterator.hasNext(); ) {
      DelegatingSerializerEntry delegatingSerializerEntry = iterator.next();

      PsiType delegatingSerializerType = delegatingSerializerEntry.getDelegatingSerializerType();
      String paramName = javaCodeStyleManager.suggestVariableName( VariableKind.PARAMETER, null, null, delegatingSerializerType ).names[0];

      constructorBuilder.append( delegatingSerializerType.getCanonicalText() ).append( " " ).append( paramName );

      if ( iterator.hasNext() ) {
        constructorBuilder.append( "," );
      }
    }

    constructorBuilder.append( "){" )
      .append( "super(\"" ).append( createType( serializerClass.getName() ) ).append( "\", com.cedarsoft.version.VersionRange.from(1,0,0).to());" );


    constructorBuilder.append( "}" );

    return elementFactory.createMethodFromText( constructorBuilder.toString(), null );
  }

  /**
   * Returns the jackson serializer for the given type
   *
   * @param typeToSerialize the type that shall be serialized
   * @return the found jackson serializer
   */
  @Nonnull
  protected PsiType findJacksonSerializerFor( @Nonnull PsiType typeToSerialize ) {
    //Fix scope: GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(  )
    PsiClass serializerClass = javaPsiFacade.findClass( JACKSON_SERIALIZER_IFACE_NAME, GlobalSearchScope.allScope( project ) );
    if ( serializerClass == null ) {
      throw new IllegalStateException( "No class found for <" + JACKSON_SERIALIZER_IFACE_NAME + ">. Please fix your workspace." );
    }


    final PsiType a = elementFactory.createTypeFromText( JACKSON_SERIALIZER_IFACE_NAME + "<" + typeToSerialize.getCanonicalText() + ">", null );

    final PsiType[] foundSerializerType = new PsiType[1];
    ClassInheritorsSearch.search( serializerClass ).forEach( new Processor<PsiClass>() {
      @Override
      public boolean process( PsiClass psiClass ) {
        //Skip interfaces and abstract classes
        if ( psiClass.isInterface() || psiClass.hasModifierProperty( PsiModifier.ABSTRACT ) ) {
          return true;
        }

        PsiClassType currentSerializerType = elementFactory.createType( psiClass );
        if ( a.isAssignableFrom( currentSerializerType ) ) {
          foundSerializerType[0] = currentSerializerType;
          return false;
        }

        return true;
      }
    } );

    if ( foundSerializerType[0] != null ) {
      return foundSerializerType[0];
    }

    //Fallback: Create a new pseudo serializer class
    return elementFactory.createTypeByFQClassName( typeToSerialize.getPresentableText() + "Serializer" );
  }

  /**
   * Creates the json type for the given class name
   *
   * @param className the class name
   * @return the json type
   */
  @Nonnull
  private String createType( @Nonnull String className ) {
    return javaCodeStyleManager.suggestVariableName( VariableKind.STATIC_FINAL_FIELD, className, null, null ).names[0].toLowerCase( Locale.getDefault() );
  }

  public class DelegatingSerializerEntry {
    @Nonnull
    private final PsiType type;
    @Nonnull
    private final PsiType delegatingSerializerType;

    public DelegatingSerializerEntry( @Nonnull PsiType typeToSerialize ) {
      this.type = typeToSerialize;
      delegatingSerializerType = findJacksonSerializerFor( typeToSerialize );
    }

    @Nonnull
    public PsiType getDelegatingSerializerType() {
      return delegatingSerializerType;
    }

    @Nonnull
    public PsiType getType() {
      return type;
    }
  }

}
