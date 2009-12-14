package com.cedarsoft.test;

/**
 *
 */
//START SNIPPET: fieldsAndConstructors
public class Extra {
  private final String description;

  private final Money price;

  public Extra( String description, Money price ) {
    this.description = description;
    this.price = price;
  }

  //END SNIPPET: fieldsAndConstructors

  public Money getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof Extra ) ) return false;

    Extra extra = ( Extra ) o;

    if ( description != null ? !description.equals( extra.description ) : extra.description != null ) return false;
    if ( price != null ? !price.equals( extra.price ) : extra.price != null ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = description != null ? description.hashCode() : 0;
    result = 31 * result + ( price != null ? price.hashCode() : 0 );
    return result;
  }
}
