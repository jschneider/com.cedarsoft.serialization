package plugin.psi;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface PsiHelper {
  @Nullable
  PsiFile getPsiFileFromEditor( Editor editor, Project project );

  @Nullable
  PsiClass getPsiClassFromEditor( Editor editor, Project project );

  PsiShortNamesCache getPsiShortNamesCache( Project project );

  @Nullable
  PsiDirectory getDirectoryFromModuleAndPackageName( Module module, String packageName );

  void navigateToClass( @Nonnull PsiClass psiClass );

  @Nullable
  String checkIfClassCanBeCreated( PsiDirectory targetDirectory, String className );

  @Nullable
  PsiPackage getPackage( PsiDirectory psiDirectory );

  JavaDirectoryService getJavaDirectoryService();

  JavaPsiFacade getJavaPsiFacade( Project project );

  CommandProcessor getCommandProcessor();

  Application getApplication();

  @Nullable
  Module findModuleForPsiClass( PsiClass psiClass, Project project );
}