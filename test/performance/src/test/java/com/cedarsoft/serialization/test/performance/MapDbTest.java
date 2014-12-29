package com.cedarsoft.serialization.test.performance;

import org.assertj.core.api.MapAssert;
import org.junit.*;
import org.junit.rules.*;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.concurrent.ConcurrentNavigableMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class MapDbTest {
  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void testIt() throws Exception {
    BTreeMap<String, Integer> treeMap = DBMaker.newTempTreeMap();
    treeMap.put( "daKey", 7 );

    assertThat( treeMap ).hasSize( 1 );
    assertThat( treeMap ).includes( MapAssert.entry( "daKey", 7 ) );
  }

  @Test
  public void testMoreComplex() throws Exception {
    File file = tmp.newFile();

    DB db = DBMaker.newFileDB( file )
      .closeOnJvmShutdown()
      .encryptionEnable( "password" )
      .make();

    //a new collection
    ConcurrentNavigableMap<Integer, String> map = db.getTreeMap( "collectionName" );

    map.put( 1, "one" );
    map.put( 2, "two" );

    // map.keySet() is now [1,2]
    assertThat( map ).hasSize( 2 );

    db.commit();  //persist changes into disk

    map.put( 3, "three" );
    assertThat( map ).hasSize( 3 );
    // map.keySet() is now [1,2,3]
    db.rollback(); //revert recent changes
    // map.keySet() is now [1,2]

    assertThat( map ).hasSize( 2 );
    db.close();
  }
}
