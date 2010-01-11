package com.cedarsoft.serialization;

import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class ZoneInfoSerializer implements Serializer<DateTimeZone> {
  @Override
  public void serialize( @NotNull DateTimeZone object, @NotNull OutputStream out ) throws IOException {
    out.write( object.getID().getBytes() );
  }

  @NotNull
  @Override
  public DateTimeZone deserialize( @NotNull InputStream in ) throws IOException, VersionException {
    return DateTimeZone.forID( IOUtils.toString( in ) );
  }

  @NotNull
  @Override
  public Version getFormatVersion() {
    return new Version( 1, 0, 0 );
  }

  @NotNull
  @Override
  public VersionRange getFormatVersionRange() {
    return VersionRange.from( 1, 0, 0 ).to();
  }
}
