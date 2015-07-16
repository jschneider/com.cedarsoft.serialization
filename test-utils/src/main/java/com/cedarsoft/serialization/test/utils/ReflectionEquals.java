package com.cedarsoft.serialization.test.utils;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

public class ReflectionEquals extends BaseMatcher<Object> {
  private final Object wanted;

  public ReflectionEquals( Object wanted ) {
    this.wanted = wanted;
  }

  @Override
  public boolean matches( Object actual ) {
    return EqualsBuilder.reflectionEquals( wanted, actual );
  }

  @Override
  public void describeTo( Description description ) {
    description.appendText( "refEq(" + wanted + ")" );
  }
}