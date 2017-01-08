import java.lang.String;
import java.util.List;

public class Collections2{
  private final List<String> foo1 = new ArrayList();
  private final List<Integer> foo2 = new ArrayList();

  public Collections2(Collection<? extends String> foo1) {
    this(foo1, new ArrayList<>());
  }

  public Collections2(Collection<? extends String> foo1, Collection<? extends Integer> foo2) {
    this.foo1.addAll(foo1);
    this.foo2.addAll(foo2);
  }

  public List<String> getFoo1() {
    return foo1;
  }

  public List<Integer> getFoo2() {
    return foo2;
  }
}