/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.serialization.test.performance;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionRange;
import com.cedarsoft.serialization.AbstractSerializer;
import com.cedarsoft.serialization.stax.mate.AbstractStaxMateSerializer;
import org.apache.commons.lang.time.StopWatch;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class StaxMateDelegatePerformance {
  private static final int COUNT = 100000;

  public static void main( String[] args ) {
    System.out.println( "Starting Performance test" );

    System.out.println();
    System.out.println( "With Delegate" );
    new StaxMateDelegatePerformance().benchWithDelegate();
    System.out.println();
    System.out.println( "Hard coded dep" );
    new StaxMateDelegatePerformance().benchHardCoded();
    System.out.println();
  }

  private void benchWithDelegate() {
    runBenchmark( new Runnable() {
                    @Override
                    public void run() {
                      try {
                        SMInputFactory inf = new SMInputFactory( XMLInputFactory.newInstance( "com.ctc.wstx.stax.WstxInputFactory", getClass().getClassLoader() ) );
                        benchRoundTrip( inf.getStaxFactory(), new FileTypeSerializerDelegates( new ExtensionSerializer() ) );
                      } catch ( Exception e ) {
                        throw new RuntimeException( e );
                      }
                    }
                  }, 4 );
  }

  private void benchHardCoded() {
    runBenchmark( new Runnable() {
                    @Override
                    public void run() {
                      try {
                        SMInputFactory inf = new SMInputFactory( XMLInputFactory.newInstance( "com.ctc.wstx.stax.WstxInputFactory", getClass().getClassLoader() ) );
                        benchRoundTrip( inf.getStaxFactory(), new FileTypeSerializerHardCoded( new ExtensionSerializer() ) );
                      } catch ( Exception e ) {
                        throw new RuntimeException( e );
                      }
                    }
                  }, 4 );
  }

  private void benchRoundTrip( XMLInputFactory staxFactory, @Nonnull AbstractStaxMateSerializer<FileType> serializer ) throws IOException {
    FileType fileType = new FileType( "jpg", new Extension( ".", "jpg", true ), false );

    for ( int i = 0; i < COUNT; i++ ) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      serializer.serialize( fileType, out );

      FileType deserialized = serializer.deserialize( new ByteArrayInputStream( out.toByteArray() ) );
      assertNotNull( deserialized );
    }
  }

  private void runBenchmark( @Nonnull Runnable runnable, final int count ) {
    //Warmup
    runnable.run();
    runnable.run();
    runnable.run();

    List<Long> times = new ArrayList<Long>();

    for ( int i = 0; i < count; i++ ) {
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      runnable.run();
      stopWatch.stop();

      times.add( stopWatch.getTime() );
    }

    System.out.println( "-----------------------" );
    for ( Long time : times ) {
      System.out.println( "--> " + time );
    }
    System.out.println( "-----------------------" );
  }

  public static class FileTypeSerializerDelegates extends AbstractStaxMateSerializer<FileType> {
    @Nonnull

    private static final String ATTRIBUTE_DEPENDENT = "dependent";
    @Nonnull

    private static final String ELEMENT_ID = "id";
    @Nonnull

    private static final String ELEMENT_EXTENSION = "extension";


    public FileTypeSerializerDelegates( @Nonnull ExtensionSerializer extensionSerializer ) {
      super( "fileType", "http://collustra.cedarsoft.com/fileType", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );

      add( extensionSerializer ).responsibleFor( Extension.class )
        .map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 )
      ;

      getDelegatesMappings().verify();
    }

    @Override
    public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull FileType object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      serializeTo.addAttribute( ATTRIBUTE_DEPENDENT, String.valueOf( object.isDependent() ) );
      serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_ID ).addCharacters( object.getId() );

      SMOutputElement extensionElement = serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_EXTENSION );
      serialize( object.getExtension(), Extension.class, extensionElement, formatVersion );
    }

    @Nonnull
    @Override
    public FileType deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      boolean dependent = Boolean.parseBoolean( deserializeFrom.getAttributeValue( null, ATTRIBUTE_DEPENDENT ) );
      String id = getChildText( deserializeFrom, ELEMENT_ID );


      nextTag( deserializeFrom, ELEMENT_EXTENSION );
      Extension extension = deserialize( Extension.class, formatVersion, deserializeFrom );

      closeTag( deserializeFrom );

      return new FileType( id, extension, dependent );
    }
  }

  public static class FileTypeSerializerHardCoded extends AbstractStaxMateSerializer<FileType> {
    @Nonnull
    private static final Version EXTENSION_FORMAT_VERSION = new Version( 1, 0, 0 );
    @Nonnull

    private static final String ATTRIBUTE_DEPENDENT = "dependent";
    @Nonnull

    private static final String ELEMENT_ID = "id";
    @Nonnull

    private static final String ELEMENT_EXTENSION = "extension";

    @Nonnull
    private final ExtensionSerializer extensionSerializer;

    public FileTypeSerializerHardCoded( @Nonnull ExtensionSerializer extensionSerializer ) {
      super( "fileType", "http://collustra.cedarsoft.com/fileType", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
      this.extensionSerializer = extensionSerializer;
      AbstractSerializer.verifyDelegatingSerializerVersion( extensionSerializer, EXTENSION_FORMAT_VERSION );
    }

    @Override
    public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull FileType object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      serializeTo.addAttribute( ATTRIBUTE_DEPENDENT, String.valueOf( object.isDependent() ) );
      serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_ID ).addCharacters( object.getId() );

      SMOutputElement extensionElement = serializeTo.addElement( serializeTo.getNamespace(), ELEMENT_EXTENSION );
      extensionSerializer.serialize( extensionElement, object.getExtension(), EXTENSION_FORMAT_VERSION );
    }

    @Nonnull
    @Override
    public FileType deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      boolean dependent = Boolean.parseBoolean( deserializeFrom.getAttributeValue( null, ATTRIBUTE_DEPENDENT ) );
      String id = getChildText( deserializeFrom, ELEMENT_ID );


      nextTag( deserializeFrom, ELEMENT_EXTENSION );
      Extension extension = extensionSerializer.deserialize( deserializeFrom, EXTENSION_FORMAT_VERSION );

      closeTag( deserializeFrom );

      return new FileType( id, extension, dependent );
    }
  }

  public static class ExtensionSerializer extends AbstractStaxMateSerializer<Extension> {
    @Nonnull

    private static final String ATTRIBUTE_DELIMITER = "delimiter";
    @Nonnull

    private static final String ATTRIBUTE_DEFAULT = "default";

    public ExtensionSerializer() {
      super( "extension", "http://www.cedarsoft.com/file/extension", new VersionRange( new Version( 1, 0, 0 ), new Version( 1, 0, 0 ) ) );
    }

    @Override
    public void serialize( @Nonnull SMOutputElement serializeTo, @Nonnull Extension object, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionWritable( formatVersion );
      serializeTo.addAttribute( ATTRIBUTE_DEFAULT, String.valueOf( object.isDefault() ) );
      serializeTo.addAttribute( ATTRIBUTE_DELIMITER, object.getDelimiter() );
      serializeTo.addCharacters( object.getExtension() );
    }

    @Nonnull
    @Override
    public Extension deserialize( @Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion ) throws IOException, XMLStreamException {
      assert isVersionReadable( formatVersion );
      boolean isDefault = Boolean.parseBoolean( deserializeFrom.getAttributeValue( null, ATTRIBUTE_DEFAULT ) );
      String delimiter = deserializeFrom.getAttributeValue( null, ATTRIBUTE_DELIMITER );

      deserializeFrom.next();
      String extension = deserializeFrom.getText();

      closeTag( deserializeFrom );

      return new Extension( delimiter, extension, isDefault );
    }
  }

}
