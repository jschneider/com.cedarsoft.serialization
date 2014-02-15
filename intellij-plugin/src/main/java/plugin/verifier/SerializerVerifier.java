package plugin.verifier;

import com.intellij.psi.PsiClass;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface SerializerVerifier {
  boolean isSerializer( @Nonnull PsiClass psiClass );
}