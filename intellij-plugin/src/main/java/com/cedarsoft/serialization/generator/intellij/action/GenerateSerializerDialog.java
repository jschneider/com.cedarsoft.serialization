package com.cedarsoft.serialization.generator.intellij.action;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.ide.util.DirectoryChooser;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.MoveDestination;
import com.intellij.refactoring.PackageWrapper;
import com.intellij.refactoring.move.moveClassesOrPackages.DestinationFolderComboBox;
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.ReferenceEditorComboWithBrowseButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import net.miginfocom.swing.MigLayout;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class GenerateSerializerDialog extends DialogWrapper {
  @Nonnull
  private final CollectionListModel<PsiField> fieldCollectionListModel;
  @Nonnull
  private final EnumComboBoxModel<Dialect> dialectEnumComboBoxModel = new EnumComboBoxModel<Dialect>( Dialect.class );

  @Nonnull
  private final PsiClass psiClass;
  private final ReferenceEditorComboWithBrowseButton referenceEditor;

  @Nonnull
  private final DestinationFolderComboBox serializerDestinationBox = new DestinationFolderComboBox() {
    @Override
    public String getTargetPackage() {
      return referenceEditor.getText();
    }
  };

  @Nonnull
  private final DestinationFolderComboBox testsDestinationBox = new DestinationFolderComboBox() {
    @Override
    public String getTargetPackage() {
      return referenceEditor.getText();
    }
  };

  @Nonnull
  private final DestinationFolderComboBox testResourcesDestinationBox = new DestinationFolderComboBox() {
    @Override
    public String getTargetPackage() {
      return referenceEditor.getText();
    }
  };

  public GenerateSerializerDialog( @Nonnull PsiClass psiClass ) {
    super( psiClass.getProject() );
    this.psiClass = psiClass;
    setTitle( "Generate Serializer" );

    fieldCollectionListModel = new CollectionListModel<PsiField>( psiClass.getAllFields() );
    referenceEditor = new PackageNameReferenceEditorCombo( guessPackage(), psiClass.getProject(), "generate.serializer.recent.packages", "Select Serializer target package" );

    serializerDestinationBox.setData( psiClass.getProject(), psiClass.getContainingFile().getContainingDirectory(), referenceEditor.getChildComponent() );
    testsDestinationBox.setData( psiClass.getProject(), psiClass.getContainingFile().getContainingDirectory(), referenceEditor.getChildComponent() ); //TODO test
    testResourcesDestinationBox.setData( psiClass.getProject(), psiClass.getContainingFile().getContainingDirectory(), referenceEditor.getChildComponent() ); //TODO resources

    init();
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    JPanel holder = new JPanel( new MigLayout("wrap 2, fill", "[][fill,grow]", "[][fill,grow][][][][]") );

    //ask for the dialect
    {
      ComboBox dialectList = new ComboBox( dialectEnumComboBoxModel );
      holder.add( new JLabel( "Dialect:" ) );
      holder.add( dialectList );
    }

    {
      //noinspection TypeMayBeWeakened
      JBList fieldList = new JBList( fieldCollectionListModel );
      fieldList.setCellRenderer( new DefaultPsiElementCellRenderer() );

      ToolbarDecorator decorator = ToolbarDecorator.createDecorator( fieldList );
      decorator.disableAddAction();

      JPanel panel = decorator.createPanel();
      holder.add( LabeledComponent.create( panel, "Select fields to serialize" ), "span 2, grow" );
    }

    holder.add( new JLabel( "Target package:" ) );
    holder.add( referenceEditor );
    holder.add( new JLabel( "Serializer:" ) );
    holder.add( serializerDestinationBox );
    holder.add( new JLabel( "Tests:" ) );
    holder.add( testsDestinationBox );
    holder.add( new JLabel( "Test-Resources:" ) );
    holder.add( testResourcesDestinationBox );

    return holder;
  }

  @Nonnull
  private String guessPackage() {
    PsiJavaFile psiJavaFile = ( PsiJavaFile ) psiClass.getContainingFile();
    return psiJavaFile.getPackageName() + ".io";
    //JavaPsiFacade.getInstance( psiJavaFile.getProject() ).findPackage( psiJavaFile.getPackageName() );
  }

  public String getTargetPackageName() {
    return referenceEditor.getText();
  }

  @Nonnull
  public PsiDirectory getSerializerDestination() {
    return (( DirectoryChooser.ItemWrapper ) serializerDestinationBox.getComboBox().getSelectedItem()).getDirectory();
  }

  @Nonnull
  public PsiDirectory getTestsDestination() {
    return (( DirectoryChooser.ItemWrapper ) testsDestinationBox.getComboBox().getSelectedItem()).getDirectory();
  }

  @Nonnull
  public PsiDirectory getTestResourcesDestination() {
    return (( DirectoryChooser.ItemWrapper ) testResourcesDestinationBox.getComboBox().getSelectedItem()).getDirectory();
  }

  @Nonnull
  public List<? extends PsiField> getSelectedFields() {
    return fieldCollectionListModel.getItems();
  }

  @Nonnull
  public Dialect getSelectedDialect() {
    @Nullable Dialect selectedItem = dialectEnumComboBoxModel.getSelectedItem();
    if ( selectedItem == null ) {
      throw new IllegalStateException( "No dialect selected" );
    }
    return selectedItem;
  }

  @Nonnull
  public PsiClass getPsiClass() {
    return psiClass;
  }


  public enum Dialect {
    JACKSON,
    STAX_MATE
  }
}
