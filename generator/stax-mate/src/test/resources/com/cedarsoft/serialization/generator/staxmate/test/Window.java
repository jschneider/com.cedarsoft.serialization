package com.cedarsoft.serialization.generator.staxmate.test;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Window {
  /**
   * the comment for field width
   */
  private final double width;
  private final int height;
  private final String description;
  private final Integer anInt;
  private float floatField;

  @NotNull
  private final List<Double> doubleValues = new ArrayList<Double>();

  /**
   * the constructor
   *
   * @param description  the descri
   * @param width        the width
   * @param height       the height
   * @param anInt        the int
   * @param doubleValues the double values
   */
  public Window( String description, double width, int height, Integer anInt, List<? extends Double> doubleValues ) {
    this.width = width;
    this.height = height;
    this.description = description;
    this.anInt = anInt;
    this.doubleValues.addAll( doubleValues );
  }

  @NotNull
  public List<Double> getDoubleValues() {
    return Collections.unmodifiableList( doubleValues );
  }

  public double getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public Integer getAnInt() {
    return anInt;
  }

  public String getDescription() {
    return description;
  }

  public float getFloatField() {
    return floatField;
  }

  public void setFloatField( float floatField ) {
    this.floatField = floatField;
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
}
