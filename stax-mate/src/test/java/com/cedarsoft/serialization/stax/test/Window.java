package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.UnsupportedVersionException;
import com.cedarsoft.Version;
import com.cedarsoft.VersionException;
import com.cedarsoft.VersionRange;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.codehaus.staxmate.out.SMOutputElement;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;

/**
 *
 */
public class Window {
  private final double width;
  private final double height;
  private final String description;

  public Window( String description, double width, double height ) {
    this.width = width;
    this.height = height;
    this.description = description;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Window ) ) return false;

    Window window = ( Window ) o;

    if ( Double.compare( window.height, height ) != 0 ) return false;
    if ( Double.compare( window.width, width ) != 0 ) return false;
    if ( description != null ? !description.equals( window.description ) : window.description != null ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = width != +0.0d ? Double.doubleToLongBits( width ) : 0L;
    result = ( int ) ( temp ^ ( temp >>> 32 ) );
    temp = height != +0.0d ? Double.doubleToLongBits( height ) : 0L;
    result = 31 * result + ( int ) ( temp ^ ( temp >>> 32 ) );
    result = 31 * result + ( description != null ? description.hashCode() : 0 );
    return result;
  }

  public static class Serializer extends AbstractStaxMateSerializer<Window> {
    public Serializer() {
      super( "window", "window", new VersionRange( new Version( 1, 0, 0 ), new Version( 2, 0, 0 ) ) );
    }

    @Override
    public void serialize( @NotNull SMOutputElement serializeTo, @NotNull Window object ) throws IOException, XMLStreamException {
      serializeTo.addAttribute( "width", String.valueOf( object.getWidth() ) );
      serializeTo.addAttribute( "height", String.valueOf( object.getHeight() ) );

      serializeTo.addElementWithCharacters( serializeTo.getNamespace(), "description", object.getDescription() );
    }

    @NotNull
    @Override
    public Window deserialize( @NotNull XMLStreamReader deserializeFrom, @NotNull Version formatVersion ) throws IOException, VersionException, XMLStreamException {
      if ( formatVersion.equals( Version.valueOf( 2, 0, 0 ) ) ) {
        double width = Double.parseDouble( deserializeFrom.getAttributeValue( null, "width" ) );
        double height = Double.parseDouble( deserializeFrom.getAttributeValue( null, "height" ) );

        String description = getChildText( deserializeFrom, "description" );

        closeTag( deserializeFrom );

        return new Window( description, width, height );
      } else if ( formatVersion.equals( Version.valueOf( 1, 0, 0 ) ) ) {
        double width = Double.parseDouble( getChildText( deserializeFrom, "width" ) );
        double height = Double.parseDouble( getChildText( deserializeFrom, "height" ) );

        String description = getChildText( deserializeFrom, "description" );

        closeTag( deserializeFrom );

        return new Window( description, width, height );
      } else {
        throw new UnsupportedVersionException( formatVersion, getFormatVersionRange() );
      }
    }
  }
}
