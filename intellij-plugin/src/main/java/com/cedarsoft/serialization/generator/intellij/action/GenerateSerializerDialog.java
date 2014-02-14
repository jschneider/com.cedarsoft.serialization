package com.cedarsoft.serialization.generator.intellij.action;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class GenerateSerializerDialog extends DialogWrapper {
  @Nonnull
  private final CollectionListModel<PsiField> fieldCollectionListModel;

  @Nonnull
  private final PsiClass psiClass;

  public GenerateSerializerDialog( @Nonnull PsiClass psiClass ) {
    super( psiClass.getProject() );
    this.psiClass = psiClass;
    setTitle( "Generate Serializer" );

    fieldCollectionListModel = new CollectionListModel<PsiField>( psiClass.getAllFields() );

    init();
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    //noinspection TypeMayBeWeakened
    JBList fieldList = new JBList( fieldCollectionListModel );
    fieldList.setCellRenderer( new DefaultPsiElementCellRenderer() );

    ToolbarDecorator decorator = ToolbarDecorator.createDecorator( fieldList );
    decorator.disableAddAction();

    JPanel panel = decorator.createPanel();

    return LabeledComponent.create( panel, "Select fields to serialize" );
  }

  @Nonnull
  public List<? extends PsiField> getSelectedFields() {
    return fieldCollectionListModel.getItems();
  }

  @Nonnull
  public PsiClass getPsiClass() {
    return psiClass;
  }
}
