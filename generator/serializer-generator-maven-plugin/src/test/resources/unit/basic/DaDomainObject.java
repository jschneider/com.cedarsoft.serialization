package unit.basic;

/**
 *
 */
public class DaDomainObject {
  private final String description;
  private final int id;

  public DaDomainObject( String description, int id ) {
    this.description = description;
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public int getId() {
    return id;
  }
}
