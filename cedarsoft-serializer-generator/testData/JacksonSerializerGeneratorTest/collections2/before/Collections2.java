import java.lang.String;
import java.util.List;

public class Collections2{
  private final List<String> foo1;

  public Collections2(Collection<? extends String> foo1) {
    this.foo1 = new ArrayList(foo1);
  }

  public List<String> getFoo1() {
    return foo1;
  }
}