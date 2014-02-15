import java.lang.String;

public class Primitives{
  private final int foo1;
  private final short foo2;
  private final byte foo3;
  private final long foo4;
  private final double foo5;
  private final float foo6;
  private final char foo7;
  private final boolean foo8;
  private final String foo9;

  public Primitives( int foo1, short foo2, byte foo3, long foo4, double foo5, float foo6, char foo7, boolean foo8, String foo9 ) {
    this.foo1 = foo1;
    this.foo2 = foo2;
    this.foo3 = foo3;
    this.foo4 = foo4;
    this.foo5 = foo5;
    this.foo6 = foo6;
    this.foo7 = foo7;
    this.foo8 = foo8;
    this.foo9 = foo9;
  }

  public int getFoo1() {
    return foo1;
  }

  public short getFoo2() {
    return foo2;
  }

  public byte getFoo3() {
    return foo3;
  }

  public long getFoo4() {
    return foo4;
  }

  public double getFoo5() {
    return foo5;
  }

  public float getFoo6() {
    return foo6;
  }

  public char getFoo7() {
    return foo7;
  }

  public boolean isFoo8() {
    return foo8;
  }

  public String getFoo9() {
    return foo9;
  }
}