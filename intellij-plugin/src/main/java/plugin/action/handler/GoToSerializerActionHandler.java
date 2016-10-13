package plugin.action.handler;

import plugin.psi.PsiHelper;
import plugin.psi.PsiHelperImpl;
import plugin.verifier.PopupDisplayer;
import plugin.verifier.PopupListFactory;
import plugin.verifier.SerializerFinder;
import plugin.verifier.SerializerVerifier;
import plugin.verifier.SerializerVerifierImpl;
import com.intellij.ClassFinder;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.swing.JList;
import java.io.File;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class GoToSerializerActionHandler extends EditorActionHandler {
  @Nonnull
  private final PsiHelper psiHelper;
  @Nonnull
  private final SerializerVerifier serializerVerifier;
  @Nonnull
  private final SerializerFinder serializerFinder;
  @Nonnull
  private final PopupDisplayer popupDisplayer;
  @Nonnull
  private final PopupListFactory popupListFactory;

  public GoToSerializerActionHandler() throws IOException {
    this( new PsiHelperImpl(), new SerializerVerifierImpl(), new SerializerFinder(new ClassFinder( new File( "." ), "com", true ) ), new PopupDisplayer() {
            @Override
            public void displayPopupChooser( Editor editor, JList list, Runnable runnable ) {
            }
          }, new PopupListFactory() {
            @Override
            public JList getPopupList() {
              throw new UnsupportedOperationException();
            }
          }
    );
  }

  @SuppressWarnings( "PMD.ExcessiveParameterList" )
  public GoToSerializerActionHandler( @NotNull PsiHelper psiHelper, @NotNull SerializerVerifier serializerVerifier, @NotNull SerializerFinder serializerFinder, @NotNull PopupDisplayer popupDisplayer, @NotNull PopupListFactory popupListFactory ) {
    this.psiHelper = psiHelper;
    this.serializerVerifier = serializerVerifier;
    this.serializerFinder = serializerFinder;
    this.popupDisplayer = popupDisplayer;
    this.popupListFactory = popupListFactory;
  }

  @Override
  public void execute( Editor editor, DataContext dataContext ) {
    Project project = ( Project ) dataContext.getData( DataKeys.PROJECT.getName() );
    PsiClass psiClassFromEditor = psiHelper.getPsiClassFromEditor( editor, project );
    if ( psiClassFromEditor != null ) {
      navigateOrDisplay( editor, psiClassFromEditor, dataContext );
    }
  }

  private void navigateOrDisplay( Editor editor, PsiClass psiClassFromEditor, DataContext dataContext ) {
    boolean isBuilder = serializerVerifier.isSerializer( psiClassFromEditor );
    PsiClass classToGo = findClassToGo( psiClassFromEditor, isBuilder );
    if ( classToGo != null ) {
      psiHelper.navigateToClass( classToGo );
    } else if ( !isBuilder ) {
      displayPopup( editor, psiClassFromEditor, dataContext );
    }
  }

  private void displayPopup( final Editor editor, final PsiClass psiClassFromEditor, final DataContext dataContext ) {
    throw new UnsupportedOperationException();
    //JList popupList = popupListFactory.getPopupList();
    //Project project = ( Project ) dataContext.getData( DataKeys.PROJECT.getName() );
    //displayChoosersRunnable.setEditor( editor );
    //displayChoosersRunnable.setProject( project );
    //displayChoosersRunnable.setPsiClassFromEditor( psiClassFromEditor );
    //popupDisplayer.displayPopupChooser( editor, popupList, displayChoosersRunnable );
  }

  @Nullable
  private PsiClass findClassToGo( @Nonnull PsiClass psiClassFromEditor, boolean isSerializer ) {
    if ( isSerializer ) {
      return serializerFinder.findClassForBuilder( psiClassFromEditor );
    }
    return serializerFinder.findBuilderForClass( psiClassFromEditor );
  }
}