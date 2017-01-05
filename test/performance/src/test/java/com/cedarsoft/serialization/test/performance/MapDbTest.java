package com.cedarsoft.serialization.test.performance;

import org.junit.*;
import org.junit.rules.*;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

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
    DB db = DBMaker.memoryDB().make();


    BTreeMap<String, Integer> treeMap = db
      .treeMap("daTreeMap")
      .keySerializer(Serializer.STRING)
      .valueSerializer(Serializer.INTEGER)
      .create();

    treeMap.put( "daKey", 7 );

    assertThat( treeMap ).hasSize( 1 );
    assertThat( treeMap ).containsEntry( "daKey", 7 );
  }

  @Test
  public void testMoreComplex() throws Exception {
    File file = new File(tmp.getRoot(), "db.file");
    assertThat(file).doesNotExist();

    DB db = DBMaker.fileDB(file)
      .closeOnJvmShutdown()
      .transactionEnable()
      .make();

    //a new collection
    ConcurrentNavigableMap<Integer, String> map = db
      .treeMap("collectionName")
      .keySerializer(Serializer.INTEGER)
      .valueSerializer(Serializer.STRING)
      .create();

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
