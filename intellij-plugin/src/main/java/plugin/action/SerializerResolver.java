package plugin.action;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SerializerResolver {
  public static final String JACKSON_SERIALIZER_IFACE_NAME = "com.cedarsoft.serialization.jackson.JacksonSerializer";

  @Nonnull
  private final JavaPsiFacade javaPsiFacade;
  @Nonnull
  private final PsiElementFactory elementFactory;

  @Nonnull
  private final Project project;

  public SerializerResolver( @Nonnull Project project ) {
    this.project = project;
    javaPsiFacade = JavaPsiFacade.getInstance( project );
    elementFactory = JavaPsiFacade.getElementFactory( project );
  }

  /**
   * Returns the jackson serializer for the given serializedType
   *
   * @param typeToSerialize the serializedType that shall be serialized
   * @return the found jackson serializer
   */
  @Nonnull
  public PsiType findJacksonSerializerFor( @Nonnull final PsiType typeToSerialize ) {
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

        @Nullable
        private PsiClassType findJacksonSerializerImplFor( @Nonnull PsiClassType serializerType ) {
          for ( PsiType superType : serializerType.getSuperTypes() ) {
            PsiClass psiClass = ( ( PsiClassType ) superType ).resolve();
            assert psiClass != null;
            String qualifiedName = psiClass.getQualifiedName();

            if ( JACKSON_SERIALIZER_IFACE_NAME.equals( qualifiedName ) ) {
              return ( PsiClassType ) superType;
            }

            @Nullable PsiClassType oneDown = findJacksonSerializerImplFor( ( PsiClassType ) superType );
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
    return elementFactory.createTypeByFQClassName( guessSerializerName( typeToSerialize ) );
  }


  @Nonnull
  public String guessSerializerName( @Nonnull PsiType typeToSerialize ) {
    if ( typeToSerialize instanceof PsiPrimitiveType ) {
      PsiClassType boxedType = ( ( PsiPrimitiveType ) typeToSerialize ).getBoxedType( PsiManager.getInstance( project ), GlobalSearchScope.allScope( project ) );
      if ( boxedType == null ) {
        throw new IllegalStateException( "No boxed type found for <" + typeToSerialize + ">" );
      }
      return boxedType.getPresentableText() + "Serializer";
    }

    return typeToSerialize.getPresentableText() + "Serializer";
  }
}
