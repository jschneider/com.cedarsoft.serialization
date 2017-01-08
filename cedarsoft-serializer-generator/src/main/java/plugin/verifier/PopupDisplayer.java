package plugin.verifier;

import com.intellij.openapi.editor.Editor;

import javax.swing.JList;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public interface PopupDisplayer {
  void displayPopupChooser( Editor editor, JList list, Runnable runnable );
}