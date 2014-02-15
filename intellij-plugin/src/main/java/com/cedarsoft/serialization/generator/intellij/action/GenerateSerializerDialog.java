package com.cedarsoft.serialization.generator.intellij.action;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
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
    JPanel holder = new JPanel( new BorderLayout() );

    //ask for the dialect
    {
      ComboBox dialectList = new ComboBox( dialectEnumComboBoxModel );
      holder.add( dialectList, BorderLayout.NORTH );
    }

    {
      //noinspection TypeMayBeWeakened
      JBList fieldList = new JBList( fieldCollectionListModel );
      fieldList.setCellRenderer( new DefaultPsiElementCellRenderer() );

      ToolbarDecorator decorator = ToolbarDecorator.createDecorator( fieldList );
      decorator.disableAddAction();

      JPanel panel = decorator.createPanel();
      holder.add( LabeledComponent.create( panel, "Select fields to serialize" ), BorderLayout.CENTER );
    }


    return holder;
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
