package com.cedarsoft.serialization.ui;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.DelegateMappingTest;
import com.cedarsoft.serialization.DelegatesMappings;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 *
 */
public class DelegatesMappingVisualizerTest {
  private VersionRange mine;
  private DelegatesMappings<Object, Object, IOException> delegatesMappings;
  private DelegateMappingTest.MySerializer serializer;

  @BeforeMethod
  protected void setUp() throws Exception {
    mine = VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 );
    delegatesMappings = new DelegatesMappings<Object, Object, IOException>( mine );
    serializer = new DelegateMappingTest.MySerializer( new VersionRange( new Version( 7, 0, 0 ), new Version( 7, 5, 9 ) ) );
  }

  @Test
  public void testIt() throws IOException {
    delegatesMappings.add( serializer ).responsibleFor( Object.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).to( 1, 15, 0 ).toDelegateVersion( 7, 1, 0 )
      .map( 2, 0, 0 ).to( 2, 0, 0 ).toDelegateVersion( 7, 5, 9 )
      ;

    delegatesMappings.add( serializer ).responsibleFor( String.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 1, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).to( 1, 15, 0 ).toDelegateVersion( 7, 1, 0 )
      .map( 2, 0, 0 ).to( 2, 0, 0 ).toDelegateVersion( 7, 5, 9 )
      ;

    delegatesMappings.add( serializer ).responsibleFor( Integer.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 1, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 1, 12 )
      .map( 1, 0, 2 ).to( 1, 15, 0 ).toDelegateVersion( 7, 0, 91 )
      .map( 2, 0, 0 ).to( 2, 0, 0 ).toDelegateVersion( 7, 5, 9 )
      ;

    assertEquals( new DelegatesMappingVisualizer( delegatesMappings ).visualize(),
                  "         -->   Integer    Object    String\n" +
                    "------------------------------------------\n" +
                    "   1.0.0 -->     7.1.1     7.0.1     7.1.1\n" +
                    "   1.0.1 -->    7.1.12     7.0.2     7.0.2\n" +
                    "   1.0.2 -->    7.0.91     7.1.0     7.1.0\n" +
                    "  1.15.0 -->       |         |         |  \n" +
                    "   2.0.0 -->     7.5.9     7.5.9     7.5.9\n" +
                    "------------------------------------------\n" );
  }
}
