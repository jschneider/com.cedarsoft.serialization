package plugin.verifier;

import com.intellij.ClassFinder;
import com.intellij.psi.PsiClass;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SerializerFinder {
  @Nonnull
  public static final String SEARCH_PATTERN = "Serializer";
  public static final String EMPTY_STRING = "";

  @Nonnull
  private final ClassFinder classFinder;

  public SerializerFinder( @Nonnull ClassFinder classFinder ) {
    this.classFinder = classFinder;
  }

  public PsiClass findBuilderForClass( PsiClass psiClass ) {
    String searchName = psiClass.getName() + SEARCH_PATTERN;
    return findClass( psiClass, searchName );
  }

  public PsiClass findClassForBuilder( PsiClass psiClass ) {
    String searchName = psiClass.getName().replaceFirst( SEARCH_PATTERN, EMPTY_STRING );
    return findClass( psiClass, searchName );
  }

  private PsiClass findClass( PsiClass psiClass, String searchName ) {
    PsiClass result = null;
    if ( typeIsCorrect( psiClass ) ) {
      //result = classFinder.findClass( searchName, psiClass.getProject() );
    }
    return result;
  }

  private boolean typeIsCorrect( PsiClass psiClass ) {
    return !psiClass.isAnnotationType() && !psiClass.isEnum() && !psiClass.isInterface();
  }
}
