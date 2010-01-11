package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.DelegatesMappings;
import org.testng.annotations.*;

/**
 *
 */
public class DelegatesTest {
  private House house;
  private Door door1;
  private Room hall;
  private Room kitchen;

  @BeforeMethod
  protected void setUp() throws Exception {
    house = new House( new Door( "Front door" ) );
    door1 = new Door( "door1" );
    hall = new Room( "hall" );
    kitchen = new Room( "kitchen" );


    hall.addWindow( new Window( "window1", 10, 10 ) );
    hall.addDoor( door1 );
    house.addRoom( hall );

    kitchen.addWindow( new Window( "window2", 20, 10 ) );
    kitchen.addWindow( new Window( "window3", 20, 10 ) );
    kitchen.addDoor( door1 );
    house.addRoom( kitchen );
  }

  @Test
  public void testDelegates() {
    DelegatesMappings mappings = new DelegatesMappings( VersionRange.from( Version.valueOf( 1, 0, 0 ) ).to( Version.valueOf( 2, 0, 0 ) ) );

    mappings.add( new Door.Serializer() ).responsibleFor( Door.class )
      .map( VersionRange.from( 1, 0, 0 ).to( 2, 0, 0 ) ).toDelegateVersion( Version.valueOf( 1, 0, 0 ) );

    mappings.add( new Door.Serializer() ).responsibleFor( Door.class )
      .map( 1, 0, 0 ).to( 2, 0, 0 ).toDelegateVersion( Version.valueOf( 1, 0, 0 ) );
  }
}
