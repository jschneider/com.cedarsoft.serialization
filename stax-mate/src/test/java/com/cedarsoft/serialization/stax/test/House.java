package com.cedarsoft.serialization.stax.test;

import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class House {
  @NotNull
  private final List<Room> rooms = new ArrayList<Room>();

  @NotNull
  private final Door frontDoor;

  public House( @NotNull Door frontDoor ) {
    this.frontDoor = frontDoor;
  }

  public House( @NotNull Door frontDoor, @NotNull Collection<? extends Room> rooms ) {
    this.frontDoor = frontDoor;
    this.rooms.addAll( rooms );
  }

  public void addRoom( @NotNull Room room ) {
    this.rooms.add( room );
  }

  @NotNull
  public List<? extends Room> getRooms() {
    return Collections.unmodifiableList( rooms );
  }

  @NotNull
  public Door getFrontDoor() {
    return frontDoor;
  }
}

