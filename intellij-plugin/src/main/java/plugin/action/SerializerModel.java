package plugin.action;

import com.google.common.collect.ImmutableList;
import com.intellij.psi.PsiClass;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SerializerModel {
  @Nonnull
  private final ImmutableList<? extends FieldToSerializeEntry> fieldToSerializeEntries;

  @Nonnull
  private final ImmutableList<? extends DelegatingSerializerEntry> delegatingSerializerEntries;

  @Nonnull
  private final PsiClass classToSerialize;

  public SerializerModel( @Nonnull PsiClass classToSerialize, @Nonnull Collection<? extends FieldToSerializeEntry> fieldToSerializeEntries, @Nonnull Collection<? extends DelegatingSerializerEntry> delegatingSerializerEntries ) {
    this.classToSerialize = classToSerialize;
    this.fieldToSerializeEntries = ImmutableList.copyOf( fieldToSerializeEntries );
    this.delegatingSerializerEntries = ImmutableList.copyOf( delegatingSerializerEntries );
  }

  @Nonnull
  public PsiClass getClassToSerialize() {
    return classToSerialize;
  }

  @Nonnull
  public String generateSerializerClassName() {
    return getClassToSerialize().getName() + "Serializer";
  }

  @Nonnull
  public ImmutableList<? extends FieldToSerializeEntry> getFieldToSerializeEntries() {
    //noinspection ReturnOfCollectionOrArrayField
    return fieldToSerializeEntries;
  }

  @Nonnull
  public ImmutableList<? extends DelegatingSerializerEntry> getDelegatingSerializerEntries() {
    //noinspection ReturnOfCollectionOrArrayField
    return delegatingSerializerEntries;
  }
}
