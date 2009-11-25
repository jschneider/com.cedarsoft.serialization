package com.cedarsoft.serialization;

import com.cedarsoft.StillContainedException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Override;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class InMemorySerializedObjectsAccess implements SerializedObjectsAccess {
  @NotNull
  @NonNls
  private final Map<String, byte[]> serialized = new HashMap<String, byte[]>();

  @Override
  @NotNull
  public InputStream getInputStream( @NotNull @NonNls String id ) {
    byte[] found = serialized.get( id );
    if ( found == null ) {
      throw new IllegalArgumentException( "No stored data found for <" + id + ">" );
    }
    return new ByteArrayInputStream( found );
  }

  @Override
  @NotNull
  public Set<? extends String> getStoredIds() {
    return serialized.keySet();
  }

  @Override
  @NotNull
  public OutputStream openOut( @NotNull @NonNls final String id ) {
    byte[] stored = serialized.get( id );
    if ( stored != null ) {
      throw new StillContainedException( id );
    }

    return new ByteArrayOutputStream() {
      @Override
      public void close() throws IOException {
        super.close();
        serialized.put( id, toByteArray() );
      }
    };
  }

  public void clear() {
    serialized.clear();
  }
}
