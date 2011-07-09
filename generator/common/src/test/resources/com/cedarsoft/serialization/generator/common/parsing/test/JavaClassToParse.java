package com.cedarsoft.serialization.generator.common.parsing.test;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * THIS IS A RESOURCE!!!!
 * <p/>
 * This class is used as a source for parsing
 */
public class JavaClassToParse {
  private final String id;

  private JavaClassToParse() {
    this( null );
  }

  public JavaClassToParse( String id ) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public interface InnerInterface {
    String getAString();
  }

  public class InnerClass {
    private static final int CONST = 7;
  }

  public static class InnerStaticClass extends ArrayList<String> implements EventListener, Comparable<InnerStaticClass> {
    @Nonnull
    private final List<String> stringList = new ArrayList<String>();
    private final List<? extends String> wildStringList = new ArrayList<String>();

    private final int a = 7;

    public InnerStaticClass( int num ) {
    }

    @Nonnull
    public List<String> getStringList() {
      return stringList;
    }

    public List<? extends String> getWildStringList() {
      return wildStringList;
    }

    public void doIt() {
    }

    @Override
    public int compareTo( InnerStaticClass o ) {
      return 0;
    }
  }

  public enum AnEnum {
    VALUE_A,
    VALUE_B,
  }
}
