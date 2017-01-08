package plugin.verifier;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SerializerVerifierImpl implements SerializerVerifier {
  @Override
  public boolean isSerializer( @NotNull PsiClass psiClass ) {
    //TODO improve
    return psiClass.getName().endsWith( "Serializer" );
  }
}
