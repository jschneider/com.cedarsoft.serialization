package com.cedarsoft.serialization;

import com.cedarsoft.file.BaseName;
import com.cedarsoft.serialization.BaseNameSerializer;
import com.cedarsoft.serialization.stax.AbstractStaxMateSerializer;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class BaseNameSerializerTest extends AbstractXmlSerializerTest<BaseName> {
  @NotNull
  @Override
  protected AbstractStaxMateSerializer<BaseName> getSerializer() {
    return new BaseNameSerializer();
  }

  @NotNull
  @Override
  protected BaseName createObjectToSerialize() {
    return new BaseName( "asdf" );
  }

  @NotNull
  @Override
  protected String getExpectedSerialized() {
    return "<baseName>asdf</baseName>";
  }
}
