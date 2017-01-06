package plugin.verifier;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface PsiManagerFactory {
  @Nonnull
  PsiManager getPsiManager( @Nonnull Project project );
}