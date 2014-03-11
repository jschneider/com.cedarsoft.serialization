package plugin;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.search.GlobalSearchScope;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ContextAware {
  @Nonnull
  protected final DataContext dataContext;

  public ContextAware( @Nonnull DataContext dataContext ) {
    this.dataContext = dataContext;
  }

  @Nonnull
  public Project getProject() {
    Project project = CommonDataKeys.PROJECT.getData( dataContext );
    if ( project == null ) {
      throw new IllegalStateException( "no project found" );
    }

    return project;
  }

  @Nonnull
  public JavaPsiFacade getPsiFacade() {
    return JavaPsiFacade.getInstance( getProject() );
  }

  @Nonnull
  public GlobalSearchScope getGlobalSearchScope() {
    return GlobalSearchScope.allScope( getProject() );
  }

  @Nonnull
  public PsiElementFactory getPsiElementFactory() {
    return getPsiFacade().getElementFactory();
  }

  @Nonnull
  public DataContext getDataContext() {
    return dataContext;
  }

  @Nonnull
  public Application getApplication() {
    return ApplicationManager.getApplication();
  }
}
