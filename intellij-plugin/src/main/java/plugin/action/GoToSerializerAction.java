package plugin.action;

import plugin.action.handler.GoToSerializerActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorAction;

import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class GoToSerializerAction extends EditorAction {
  protected GoToSerializerAction() throws IOException {
    super( new GoToSerializerActionHandler() );
  }
}
