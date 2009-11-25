package com.cedarsoft.serialization;

import com.cedarsoft.StillContainedException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionRange;
import com.cedarsoft.registry.DefaultRegistry;
import com.cedarsoft.registry.Registry;
import com.cedarsoft.registry.RegistryFactory;
import com.cedarsoft.serialization.jdom.AbstractJDomSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

/**
 *
 */
public class RegistrySerializerTest {
  private RegistrySerializer<String, Registry<String>> serializer;
  private SerializedObjectsAccess access;

  @BeforeMethod
  public void setup() {
    access = new InMemorySerializedObjectsAccess();
    serializer = new RegistrySerializer<String, Registry<String>>( access, new AbstractJDomSerializer<String>( "text", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) ) {
      @Override
      @NotNull
      public Element serialize( @NotNull Element serializeTo, @NotNull String object ) {
        serializeTo.setText( object );
        return serializeTo;
      }

      @Override
      @NotNull
      public String deserialize( @NotNull Element deserializeFrom ) {
        return deserializeFrom.getTextNormalize();
      }
    }, new RegistrySerializer.IdResolver<String>() {
      @Override
      @NotNull
      public String getId( @NotNull String object ) {
        return object;
      }
    } );
  }

  @Test
  public void testDuplicates() throws IOException {
    Registry<String> registry = serializer.createConnectedRegistry( new MyRegistryFactory() );
    assertEquals( registry.getStoredObjects().size(), 0 );
    registry.store( "asdf" );
    assertEquals( registry.getStoredObjects().size(), 1 );
    try {
      registry.store( "asdf" );
      fail( "Where is the Exception" );
    } catch ( StillContainedException ignore ) {
    }
    assertEquals( registry.getStoredObjects().size(), 1 );
  }

  @Test
  public void testDeserialize() throws IOException {
    serializer.serialize( "1" );

    assertEquals( serializer.deserialize().size(), 1 );
    assertEquals( serializer.deserialize().get( 0 ), "1" );
  }

  @Test
  public void testConnected() throws IOException {
    serializer.serialize( "1" );

    Registry<String> registry = serializer.createConnectedRegistry( new MyRegistryFactory() );
    assertEquals( registry.getStoredObjects().size(), 1 );
    assertEquals( registry.getStoredObjects().get( 0 ), "1" );

    registry.store( "2" );
    assertEquals( registry.getStoredObjects().size(), 2 );

    assertEquals( access.getStoredIds().size(), 2 );
  }

  @Test
  public void testEmptyConstrucot() throws IOException {
    Registry<String> registry = new DefaultRegistry<String>( serializer.deserialize() );
    assertEquals( registry.getStoredObjects().size(), 0 );
  }

  @Test
  public void testMulti() throws IOException {
    assertEquals( access.getStoredIds().size(), 0 );

    serializer.serialize( "1" );

    assertEquals( access.getStoredIds().size(), 1 );
    try {
      serializer.serialize( "1" );
      fail( "Where is the Exception" );
    } catch ( Exception e ) {
    }

    Set<? extends String> ids = access.getStoredIds();
    assertEquals( ids.size(), 1 );
    assertTrue( ids.contains( "1" ) );

    assertEquals( serializer.getSerializer().deserialize( access.getInputStream( "1" ) ), "1" );
  }

  private static class MyRegistryFactory implements RegistryFactory<String, Registry<String>> {
    @Override
    @NotNull
    public Registry<String> createRegistry( @NotNull List<? extends String> objects, @NotNull Comparator<String> comparator ) {
      return new DefaultRegistry<String>( objects, comparator );
    }
  }
}


