package plugin.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class MyInspection extends LocalInspectionTool {

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor( @NotNull final ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session ) {
    //return super.buildVisitor( holder, isOnTheFly, session );
    return new JavaElementVisitor() {
      @Override
      public void visitField( @Nonnull PsiField field ) {
        super.visitField( field );
        if ( !field.getName().equals( "foo" ) ) {
          return;
        }

        if ( !field.getType().getCanonicalText().equals( CommonClassNames.JAVA_LANG_STRING ) ) {
          System.out.println( "Unexpected typoe: " + field.getType().getCanonicalText() );
          return;
        }

        holder.registerProblem( field, "Uups, something went wrong..." );
      }
    };
  }

}
