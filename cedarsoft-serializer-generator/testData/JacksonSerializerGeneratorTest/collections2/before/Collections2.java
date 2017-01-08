import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Collections2 {
  @Nonnull
  private final String name;
  @Nonnull
  private final List<Role> roles = new ArrayList<Role>();
  @Nonnull
  private final List<Email> emails = new ArrayList<Email>();
  @Nonnull
  private final UserDetails userDetails;
  @Nonnull
  private final Email singleEmail;

  public Collections2( @Nonnull String name, @Nonnull Collection<? extends Email> emails, Collection<? extends Role> roles, @Nonnull Email singleEmail ) {
    this( name, emails, roles, singleEmail, new UserDetails() );
  }

  public Collections2( @Nonnull String name, @Nonnull Collection<? extends Email> emails, Collection<? extends Role> roles, @Nonnull Email singleEmail, @Nonnull UserDetails userDetails ) {
    this.name = name;
    this.singleEmail = singleEmail;
    this.userDetails = userDetails;
    this.emails.addAll( emails );
    this.roles.addAll( roles );
  }

  @Nonnull
  public Email getSingleEmail() {
    return singleEmail;
  }

  @Nonnull
  public UserDetails getUserDetails() {
    return userDetails;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Nonnull
  public List<? extends Role> getRoles() {
    return Collections.unmodifiableList( roles );
  }

  @Nonnull
  public List<? extends Email> getEmails() {
    return Collections.unmodifiableList( emails );
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;

    User user = ( User ) o;

    if ( !emails.equals( user.emails ) ) return false;
    if ( !name.equals( user.name ) ) return false;
    if ( !roles.equals( user.roles ) ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + roles.hashCode();
    result = 31 * result + emails.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "User{" +
      "name='" + name + '\'' +
      ", roles=" + roles +
      ", emails=" + emails +
      ", userDetails=" + userDetails +
      '}';
  }
}
