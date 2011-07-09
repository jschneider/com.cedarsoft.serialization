package com.cedarsoft.serialization.generator.stax.mate.test;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Foo {
  public static final String CONSTANT_DO_NOT_SERIALIZE = "asdf";

  /**
   * the comment for field width
   */
  private final double width;
  private final int height;
  private final String description;
  private final Integer anInt;
  private float floatField;

  @Nonnull
  private final List<Bar> bars = new ArrayList<Bar>();

  private Bar bar = new Bar( 1 );

  /**
   * the constructor
   *
   * @param description the description
   * @param width       the width
   * @param height      the height
   * @param anInt       the int
   * @param bars        the double values
   */
  public Foo( String description, double width, int height, Integer anInt, Collection<? extends Bar> bars ) {
    this.width = width;
    this.height = height;
    this.description = description;
    this.anInt = anInt;
    this.bars.addAll( bars );
  }

  @Nonnull
  public List<? extends Bar> getBars() {
    return Collections.unmodifiableList( bars );
  }

  public Bar getBar() {
    return bar;
  }

  public void setBar( Bar bar ) {
    this.bar = bar;
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
    if ( !( o instanceof Foo ) ) return false;

    Foo foo = ( Foo ) o;

    if ( Double.compare( foo.height, height ) != 0 ) return false;
    if ( Double.compare( foo.width, width ) != 0 ) return false;
    if ( description != null ? !description.equals( foo.description ) : foo.description != null ) return false;

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
