package com.cedarsoft.serialization;

import com.cedarsoft.UnsupportedVersionException;
import com.cedarsoft.UnsupportedVersionRangeException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.testng.AssertJUnit.*;

/**
 *
 */
public class DelegateMappingTest {
  @Test
  public void testVerify() {
    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ), delegate.getFormatVersionRange() );

    try {
      mapping.verify();
      fail("Where is the Exception");
    } catch ( Exception ignore ) {
    }

    mapping.addMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );

    try {
      mapping.verify();
      fail("Where is the Exception");
    } catch ( Exception ignore ) {
    }

    mapping.addMapping( new VersionRange( new Version( 0, 5, 1 ), new Version( 1, 0, 0 ) ), new Version( 1, 0, 1 ) );
    mapping.addMapping( new VersionRange( new Version( 1, 0, 1 ), new Version( 2, 2, 7 ) ), new Version( 2, 0, 0 ) );

    mapping.verify();
  }

  @Test
  public void testBasic() {
    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ), delegate.getFormatVersionRange() );


    //Version 0.5.0 of me --> 1.0.0
    mapping.addMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
    //Version 0.5.1 - 1.0.0 of me --> 1.0.1
    mapping.addMapping( new VersionRange( new Version( 0, 5, 1 ), new Version( 1, 0, 0 ) ), new Version( 1, 0, 1 ) );
    //Version 1.0.1 - 2.2.7 of me --> 2.0.0
    mapping.addMapping( new VersionRange( new Version( 1, 0, 1 ), new Version( 2, 2, 7 ) ), new Version( 2, 0, 0 ) );


    assertEquals( mapping.resolveVersion( new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
    assertEquals( mapping.resolveVersion( new Version( 0, 5, 1 ) ), new Version( 1, 0, 1 ) );
    assertEquals( mapping.resolveVersion( new Version( 0, 9, 1 ) ), new Version( 1, 0, 1 ) );
    assertEquals( mapping.resolveVersion( new Version( 1, 0, 0 ) ), new Version( 1, 0, 1 ) );
    assertEquals( mapping.resolveVersion( new Version( 1, 0, 1 ) ), new Version( 2, 0, 0 ) );
  }

  @Test
  public void testDuplicate() {
    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ), delegate.getFormatVersionRange() );


    //Version 0.5.0 of me --> 1.0.0
    mapping.addMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
    try {
      mapping.addMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
      fail( "Where is the Exception" );
    } catch ( IllegalArgumentException ignore ) {
    }
  }

  @Test
  public void testDelegateWrongVersion() {
    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ), new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );

    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );

    try {
      mapping.addMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 0, 0, 1 ) );
      fail( "Where is the Exception" );
    } catch ( UnsupportedVersionException e ) {
      assertEquals( e.getActual(), new Version( 0, 0, 1 ) );
      assertEquals( e.getSupportedRange(), delegate.getFormatVersionRange() );
    }

    try {
      mapping.addMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 3, 0, 1 ) );
      fail( "Where is the Exception" );
    } catch ( UnsupportedVersionException e ) {
      assertEquals( e.getActual(), new Version( 3, 0, 1 ) );
      assertEquals( e.getSupportedRange(), delegate.getFormatVersionRange() );
    }
  }

  @Test
  public void testMyWrongVersion() {
    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ), new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );

    try {
      mapping.addMapping( new VersionRange( new Version( 0, 4, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
      fail( "Where is the Exception" );
    } catch ( UnsupportedVersionRangeException e ) {
      assertEquals( e.getActual(), new VersionRange( new Version( 0, 4, 0 ), new Version( 0, 5, 0 ) ) );
      assertEquals( e.getSupportedRange(), mapping.getVersionRange() );
    }

    try {
      mapping.addMapping( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 2, 8 ) ), new Version( 1, 0, 0 ) );
      fail( "Where is the Exception" );
    } catch ( UnsupportedVersionRangeException e ) {
      assertEquals( e.getActual(), new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 2, 8 ) ) );
      assertEquals( e.getSupportedRange(), mapping.getVersionRange() );
    }
  }

  //  @Test
  //  public void testBasic() {
  //    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ) );
  //
  //    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
  //
  //    //Version 0.5.0 of me --> 1.0.0
  //    mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
  //    //Version 0.5.1 - 1.0.0 of me --> 1.0.1
  //    mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 1 ), new Version( 1, 0, 0 ) ), new Version( 1, 0, 1 ) );
  //    //Version 1.0.1 - 2.2.7 of me --> 2.0.0
  //    mapping.addMapping( delegate, new VersionRange( new Version( 1, 0, 1 ), new Version( 2, 2, 7 ) ), new Version( 2, 0, 0 ) );
  //
  //
  //    assertEquals( mapping.resolveVersion( delegate, new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
  //    assertEquals( mapping.resolveVersion( delegate, new Version( 0, 5, 1 ) ), new Version( 1, 0, 1 ) );
  //    assertEquals( mapping.resolveVersion( delegate, new Version( 0, 9, 1 ) ), new Version( 1, 0, 1 ) );
  //    assertEquals( mapping.resolveVersion( delegate, new Version( 1, 0, 0 ) ), new Version( 1, 0, 1 ) );
  //    assertEquals( mapping.resolveVersion( delegate, new Version( 1, 0, 1 ) ), new Version( 2, 0, 0 ) );
  //  }
  //
  //  @Test
  //  public void testDuplicate() {
  //    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ) );
  //
  //    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
  //
  //    //Version 0.5.0 of me --> 1.0.0
  //    mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
  //    try {
  //      mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
  //      fail("Where is the Exception");
  //    } catch ( IllegalArgumentException ignore ) {
  //    }
  //
  //  }
  //
  //  @Test
  //  public void testDelegateWrongVersion() {
  //    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ) );
  //
  //    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
  //
  //    try {
  //      mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 0, 0, 1 ) );
  //      fail( "Where is the Exception" );
  //    } catch ( UnsupportedVersionException e ) {
  //      assertEquals( e.getActual(), new Version( 0, 0, 1 ) );
  //      assertEquals( e.getSupportedRange(), delegate.getFormatVersionRange() );
  //    }
  //
  //    try {
  //      mapping.addMapping( delegate, new VersionRange( new Version( 0, 5, 0 ), new Version( 0, 5, 0 ) ), new Version( 3, 0, 1 ) );
  //      fail( "Where is the Exception" );
  //    } catch ( UnsupportedVersionException e ) {
  //      assertEquals( e.getActual(), new Version( 3, 0, 1 ) );
  //      assertEquals( e.getSupportedRange(), delegate.getFormatVersionRange() );
  //    }
  //  }
  //
  //  @Test
  //  public void testMyWrongVersion() {
  //    DelegateMapping mapping = new DelegateMapping( new VersionRange( new Version( 0, 5, 0 ), new Version( 2, 2, 7 ) ) );
  //
  //    MySerializer delegate = new MySerializer( new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
  //
  //    try {
  //      mapping.addMapping( delegate, new VersionRange( new Version( 0, 4, 0 ), new Version( 0, 5, 0 ) ), new Version( 1, 0, 0 ) );
  //      fail( "Where is the Exception" );
  //    } catch ( UnsupportedVersionException e ) {
  //      assertEquals( e.getActual(), new Version( 0, 0, 1 ) );
  //      assertEquals( e.getSupportedRange(), mapping.getVersionRange() );
  //    }
  //
  //    try {
  //      mapping.addMapping( delegate, new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 2, 8 ) ), new Version( 1, 0, 0 ) );
  //      fail( "Where is the Exception" );
  //    } catch ( UnsupportedVersionException e ) {
  //      assertEquals( e.getActual(), new Version( 2, 2, 8 ) );
  //      assertEquals( e.getSupportedRange(), mapping.getVersionRange() );
  //    }
  //  }
  //

  public static class MySerializer extends AbstractSerializer<Object, Object, Object, IOException> {
    public MySerializer( @NotNull VersionRange formatVersionRange ) {
      super( formatVersionRange );
    }

    @Override
    public void serialize( @NotNull Object serializeTo, @NotNull Object object ) throws IOException, IOException {
    }

    @NotNull
    @Override
    public Object deserialize( @NotNull Object deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void serialize( @NotNull Object object, @NotNull OutputStream out ) throws IOException {
    }

    @NotNull
    @Override
    public Object deserialize( @NotNull InputStream in ) throws IOException, VersionException {
      throw new UnsupportedOperationException();
    }
  }
}
