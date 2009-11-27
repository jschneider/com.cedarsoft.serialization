package com.cedarsoft.test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Car {
  private final Model model;
  private final Color color;
  private final Money basePrice;
  private final List<Extra> extras = new ArrayList<Extra>();

  public Car( Model model, Color color, Money basePrice ) {
    this( model, color, basePrice, null );
  }

  public Car( Model model, Color color, Money basePrice, Collection<Extra> extras ) {
    this.model = model;
    this.color = color;
    this.basePrice = basePrice;

    if ( extras != null ) {
      this.extras.addAll( extras );
    }
  }

  public Money getBasePrice() {
    return basePrice;
  }

  public void addExtra( Extra extra ) {
    this.extras.add( extra );
  }

  public Color getColor() {
    return color;
  }

  public List<? extends Extra> getExtras() {
    return Collections.unmodifiableList( extras );
  }

  public Model getModel() {
    return model;
  }
}
