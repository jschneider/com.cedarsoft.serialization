package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.AssertJUnit.*;

/**
 *
 */
public class DelegatesMappingsTest {
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
  public void testVerify() {
    delegatesMappings.add( serializer ).responsibleFor( Object.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).to( 1, 5, 0 ).toDelegateVersion( 7, 1, 0 )
      ;

    try {
      delegatesMappings.verify();
      fail("Where is the Exception");
    } catch ( Exception ignore ) {
    }
  }

  @Test
  public void testIt() {
    delegatesMappings.add( serializer ).responsibleFor( Object.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
      .map( 1, 0, 1 ).toDelegateVersion( 7, 0, 2 )
      .map( 1, 0, 2 ).to( 1, 5, 0 ).toDelegateVersion( 7, 1, 0 )
      ;

    //    delegatesMappings.addMappingFor( serializer )
    //      .map( VersionRange.from( 1, 0, 0 ).single(), new Version( 7, 0, 1 ) )
    //      .map( VersionRange.from( 1, 0, 1 ).single(), new Version( 7, 0, 2 ) )
    //      .map( VersionRange.from( 1, 0, 2 ).to( 1, 5, 0 ), new Version( 7, 1, 0 ) );
    //

    assertEquals( delegatesMappings.resolveVersion( Object.class, new Version( 1, 0, 0 ) ), new Version( 7, 0, 1 ) );
    assertEquals( delegatesMappings.resolveVersion( Object.class, new Version( 1, 0, 1 ) ), new Version( 7, 0, 2 ) );
    assertEquals( delegatesMappings.resolveVersion( Object.class, new Version( 1, 0, 2 ) ), new Version( 7, 1, 0 ) );
    assertEquals( delegatesMappings.resolveVersion( Object.class, new Version( 1, 0, 3 ) ), new Version( 7, 1, 0 ) );
    assertEquals( delegatesMappings.resolveVersion( Object.class, new Version( 1, 5, 0 ) ), new Version( 7, 1, 0 ) );

    assertSame( delegatesMappings.getSerializer( Object.class ), serializer );
  }

  @Test
  public void testDuplicate() {
    delegatesMappings.add( serializer ).responsibleFor( String.class )
      .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 2 );

    try {
      delegatesMappings.add( serializer ).responsibleFor( String.class );
      fail( "Where is the Exception" );
    } catch ( IllegalArgumentException ignore ) {
    }
  }

  @Test
  public void testErrorHandling() {
    try {
      delegatesMappings.add( serializer ).responsibleFor( String.class )
        .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 1 )
        .map( 1, 0, 0 ).toDelegateVersion( 7, 0, 2 );
      fail( "Where is the Exception" );
    } catch ( IllegalArgumentException ignore ) {
    }

  }
}
