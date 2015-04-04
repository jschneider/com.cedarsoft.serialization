package com.cedarsoft.serialization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.Location;
import java.text.MessageFormat;

/**
 * Is thrown if any kind of exception happens that is related to internal issues.
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SerializationException extends RuntimeException {
  @Nonnull
  private final Details details;
  @Nullable
  private final Location location;
  @Nonnull
  private final Object[] arguments;

  public SerializationException( @Nonnull Details details, @Nonnull Object... arguments ) {
    this( details, null, arguments );
  }

  public SerializationException( @Nonnull Details details, @Nullable Location location, @Nonnull Object... arguments ) {
    this( null, details, location, arguments );
  }

  public SerializationException( @Nullable Exception cause, @Nonnull Details details, @Nullable Location location, @Nonnull Object... arguments ) {
    super( createMessage( details, location, arguments ), cause );
    this.details = details;
    this.location = location;
    this.arguments = arguments.clone();
  }

  @Nullable
  public Location getLocation() {
    return location;
  }

  @Nonnull
  public Details getDetails() {
    return details;
  }

  @Nonnull
  public Object[] getArguments() {
    return arguments.clone();
  }

  /**
   * @noinspection RefusedBequest
   */
  @Override
  public String getLocalizedMessage() {
    return details.getMessage( arguments );
  }

  @Nonnull
  private static String createMessage( @Nonnull Details details, @Nullable Location location, @Nonnull Object[] arguments ) {
    String messageWithoutLocation = "[" + details.name() + "] " + details.getMessage( arguments );
    if ( location == null ) {
      return messageWithoutLocation;
    }

    return messageWithoutLocation + " @" + location;
  }

  public enum Details {
    INVALID_VERSION( "Invalid version. Expected {0} but was {1}." ),
    INVALID_NAME_SPACE( "Invalid name space. Expected <{0}> but was <{1}>." ),
    INVALID_START_ELEMENT( "Expected START_ELEMENT but was <{0}>." ),
    INVALID_TYPE("Invalid type. Expected <{0}> but was <{1}>."),
    NOT_CONSUMED_EVERYTHING( "Not consumed everything in <{0}>." ),
    XML_EXCEPTION( "XML problem occurred {0}." ),
    ;

    @Nonnull
    private final String message;

    Details( @Nonnull String message ) {
      this.message = message;
    }

    @Nonnull
    public String getMessage( @Nonnull Object[] arguments ) {
      return MessageFormat.format( message, arguments );
    }
  }
}
