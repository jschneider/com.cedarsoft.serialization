package com.cedarsoft.serialization.stax;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class CollectionsMapping {
  @NotNull
  private final Map<String, Entry<?>> entries = new HashMap<String, Entry<?>>();

  public Entry<?> getEntry( @NotNull @NonNls String tagName ) {
    Entry<?> resolved = entries.get( tagName );
    if ( resolved == null ) {
      throw new IllegalArgumentException( "No entry found for <" + tagName + ">" );
    }
    return resolved;
  }

  @NotNull
  public <T> CollectionsMapping append( @NotNull Class<T> type, @NotNull List<T> targetCollection, @NotNull @NonNls String tagName ) {
    entries.put( tagName, new Entry<T>( type, targetCollection, tagName ) );
    return this;
  }

  public static class Entry<T> {
    @NotNull
    private final Class<T> type;
    @NotNull
    private final List<T> targetCollection;
    @NotNull
    private final String tagName;

    public Entry( @NotNull Class<T> type, @NotNull List<T> targetCollection, @NotNull @NonNls String tagName ) {
      this.type = type;
      this.targetCollection = targetCollection;
      this.tagName = tagName;
    }

    @NotNull
    public Class<T> getType() {
      return type;
    }

    @NotNull
    public List<T> getTargetCollection() {
      return targetCollection;
    }

    @NotNull
    public String getTagName() {
      return tagName;
    }
  }
}
