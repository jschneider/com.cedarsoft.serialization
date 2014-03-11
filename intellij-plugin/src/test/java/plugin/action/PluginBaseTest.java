package plugin.action;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public abstract class PluginBaseTest extends LightCodeInsightFixtureTestCase {
  @NotNull
  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return new Jdk17MockProjectDescriptor();
  }

  @Override
  protected String getTestDataPath() {
    String className = getClass().getName();

    int lastIndex = className.lastIndexOf( '.' );
    String relevantName = className.substring( lastIndex + 1 );

    return new File( "testData/" + relevantName ).getAbsolutePath();
  }

  protected void configure() throws IOException {
    String name = getTestName( false ) + ".java";

    File configurationFile = new File( getTestDataPath() + "/" + name );
    if ( !configurationFile.isFile() ) {
      throw new IOException( "Could not find configuration file at <" + configurationFile.getCanonicalPath() + ">" );
    }

    myFixture.configureByFile( name );
  }

}
