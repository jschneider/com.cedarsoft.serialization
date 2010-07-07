package unit.basic;

/**
 *
 */
public class TestDomainObject {
  private final String description;
  private final int id;

  public TestDomainObject( String description, int id ) {
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
