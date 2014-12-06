package plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class PsiTest extends PluginBaseTest {
  public void testAddMethod() throws Exception {
    configure();

    PsiElement elementAtCaret = myFixture.getFile().findElementAt( myFixture.getCaretOffset() );

    final PsiClass psiClass = PsiTreeUtil.getParentOfType( elementAtCaret, PsiClass.class );
    assertThat( psiClass ).isNotNull();
    assertThat( psiClass.getQualifiedName() ).isEqualTo( "AddMethod" );

    new WriteCommandAction.Simple( getProject(), psiClass.getContainingFile() ) {
      @Override
      protected void run() throws Throwable {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory( getProject() );

        PsiField field = psiClass.findFieldByName( "foo", false );
        assertThat( field ).isNotNull();

        PsiElement method = psiClass.add( elementFactory.createMethodFromText( "public java.util.List foo(){return " + field.getName() + "+\"Bar\";}", psiClass ) );

        JavaCodeStyleManager codeStyleManager = JavaCodeStyleManager.getInstance( getProject() );
        codeStyleManager.shortenClassReferences( method );
        //psiClass.add( comment );
      }
    }.execute();


    myFixture.checkResultByFile( getTestName( false ) + "After.java" );
  }

  public void testAddClass() throws Exception {
    configure();

    final PsiElement elementAtCaret = myFixture.getFile().findElementAt( myFixture.getCaretOffset() );

    final PsiClass psiClass = PsiTreeUtil.getParentOfType( elementAtCaret, PsiClass.class );
    assertThat( psiClass ).isNotNull();
    final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory( getProject() );

    new WriteCommandAction.Simple( getProject() ) {
      @Override
      protected void run() throws Throwable {
        PsiFile psiFile = psiClass.getContainingFile();
        PsiDirectory dir = psiFile.getParent();

        PsiClass daNewClass = JavaDirectoryService.getInstance().createClass( dir, "DaNewClassName" );
      }
    }.execute();

    myFixture.checkResultByFile( getTestName( false ) + "After.java" );
  }

  //public void testAddComment() throws Throwable {
  //  PsiElement elementAtCaret = myFixture.getFile().findElementAt( myFixture.getCaretOffset() );
  //
  //  final PsiClass psiClass = PsiTreeUtil.getParentOfType( elementAtCaret, PsiClass.class );
  //  assertThat( psiClass ).isNotNull();
  //  assertThat( psiClass.getQualifiedName() ).isEqualTo( "before1" );
  //
  //  new WriteCommandAction.Simple( getProject(), psiClass.getContainingFile() ) {
  //    @Override
  //    protected void run() throws Throwable {
  //      PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory( getProject() );
  //      final PsiComment comment = elementFactory.createCommentFromText( "//added a comment", psiClass );
  //
  //      psiClass.addBefore( comment, psiClass.findFieldByName( "foo", false ) );
  //      //psiClass.add( comment );
  //    }
  //  }.execute();
  //}

}
