package plugin.action;

import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;

/**
* @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
*/
public class Jdk17MockProjectDescriptor extends DefaultLightProjectDescriptor {
  @Override
  public Sdk getSdk() {
    return JavaSdk.getInstance().createJdk( "1.7", "mockJDK-1.7", false );
  }
}
