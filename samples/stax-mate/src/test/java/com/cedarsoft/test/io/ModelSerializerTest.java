package com.cedarsoft.test.io;

import com.cedarsoft.serialization.AbstractXmlSerializerMultiTest;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.test.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ModelSerializerTest extends AbstractXmlSerializerMultiTest<Model> {
  @NotNull
  @Override
  protected Serializer<Model> getSerializer() {
    //We create a serializer. This one is very easy. But sometimes it needs a little bit of work...
    return new ModelSerializer();
  }

  @NotNull
  @Override
  protected Iterable<? extends Model> createObjectsToSerialize() {
    //Just create a few examples of objects that shall be serialized
    return Arrays.asList(
      new Model( "Toyota" ),
      new Model( "GM" ),
      new Model( "Volkswagen" ),
      new Model( "Renault" )
    );
  }

  @NotNull
  @Override
  protected List<? extends String> getExpectedSerialized() {
    //We just return the sole part of the xml that should be compared.
    //For comparison XML-Unit is used, so there is no need to take care of formatting etc.

    //Note: The xml serializers write a version information to the xml. This has been left out here!
    return Arrays.asList(
      "<model>Toyota</model>",
      "<model>GM</model>",
      "<model>Volkswagen</model>",
      "<model>Renault</model>"
    );
  }

  @Override
  protected void verifyDeserialized( @NotNull List<? extends Model> deserialized ) {
    //We *might* override this method and verify the deserialized objects on our own
    //The default implementation simply calls "equals" for each single object.
    super.verifyDeserialized( deserialized );
  }
}
