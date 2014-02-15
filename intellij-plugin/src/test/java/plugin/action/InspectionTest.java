package plugin.action;

import plugin.inspection.MyInspection;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class InspectionTest extends PluginBaseTest {

  public void testInspection() throws Exception {
    myFixture.enableInspections( MyInspection.class );
    myFixture.testHighlighting( true, false, false, "Inspection.java" );
  }

}
