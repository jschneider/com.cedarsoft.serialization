package com.cedarsoft.serialization;

import javax.annotation.Nonnull;
import java.text.MessageFormat;

/**
 * Is thrown if any kind of exception happens that is related to internal issues.
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SerializationException extends RuntimeException {
  @Nonnull
  private final Details details;
  @Nonnull
  private final Object[] arguments;

  public SerializationException( @Nonnull Details details, @Nonnull Object... arguments ) {
    super( "["+details.name() + "] " + details.getMessage( arguments ) );
    this.details = details;
    this.arguments = arguments.clone();
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

  public enum Details {
    INVALID_VERSION( "Invalid version. Expected {0} but was {1}." ),
    INVALID_NAME_SPACE("Invalid name space. Expected <{0}> but was <{1}>.")

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
