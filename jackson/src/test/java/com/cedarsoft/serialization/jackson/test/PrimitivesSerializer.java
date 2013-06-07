package com.cedarsoft.serialization.jackson.test;

import com.cedarsoft.serialization.jackson.BooleanSerializer;
import com.cedarsoft.serialization.jackson.CharacterSerializer;
import com.cedarsoft.serialization.jackson.FloatSerializer;
import com.cedarsoft.serialization.jackson.StringSerializer;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

public class PrimitivesSerializer extends com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<Primitives> {
  public static final String PROPERTY_FOO_1 = "foo1";
  public static final String PROPERTY_FOO_2 = "foo2";
  public static final String PROPERTY_FOO_3 = "foo3";
  public static final String PROPERTY_FOO_4 = "foo4";
  public static final String PROPERTY_FOO_5 = "foo5";
  public static final String PROPERTY_FOO_6 = "foo6";
  public static final String PROPERTY_FOO_7 = "foo7";
  public static final String PROPERTY_FOO_8 = "foo8";
  public static final String PROPERTY_FOO_9 = "foo9";

  public PrimitivesSerializer( @Nonnull com.cedarsoft.serialization.jackson.IntegerSerializer integerSerializer, @Nonnull com.cedarsoft.serialization.jackson.ShortSerializer shortSerializer,
                               @Nonnull com.cedarsoft.serialization.jackson.ByteSerializer byteSerializer, @Nonnull com.cedarsoft.serialization.jackson.LongSerializer longSerializer,
                               @Nonnull com.cedarsoft.serialization.jackson.DoubleSerializer doubleSerializer, @Nonnull FloatSerializer floatSerializer,
                               @Nonnull CharacterSerializer characterSerializer, @Nonnull BooleanSerializer booleanSerializer, @Nonnull StringSerializer stringSerializer ) {
    super( "primitives_serializer", com.cedarsoft.version.VersionRange.from( 1, 0, 0 ).to() );
    getDelegatesMappings().add( integerSerializer ).responsibleFor( Integer.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( shortSerializer ).responsibleFor( Short.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( byteSerializer ).responsibleFor( Byte.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( longSerializer ).responsibleFor( Long.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( doubleSerializer ).responsibleFor( Double.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( floatSerializer ).responsibleFor( Float.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( characterSerializer ).responsibleFor( Character.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( booleanSerializer ).responsibleFor( Boolean.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    getDelegatesMappings().add( stringSerializer ).responsibleFor( String.class ).map( 1, 0, 0 ).toDelegateVersion( 1, 0, 0 );
    assert getDelegatesMappings().verify();
  }

  @Override
  public void serialize( @Nonnull com.fasterxml.jackson.core.JsonGenerator serializeTo, @Nonnull Primitives object, @Nonnull com.cedarsoft.version.Version formatVersion ) throws IOException, com.cedarsoft.version.VersionException {
    verifyVersionWritable( formatVersion );
    serialize( object.getFoo1(), Integer.class, PROPERTY_FOO_1, serializeTo, formatVersion );
    serialize( object.getFoo2(), Short.class, PROPERTY_FOO_2, serializeTo, formatVersion );
    serialize( object.getFoo3(), Byte.class, PROPERTY_FOO_3, serializeTo, formatVersion );
    serialize( object.getFoo4(), Long.class, PROPERTY_FOO_4, serializeTo, formatVersion );
    serialize( object.getFoo5(), Double.class, PROPERTY_FOO_5, serializeTo, formatVersion );
    serialize( object.getFoo6(), Float.class, PROPERTY_FOO_6, serializeTo, formatVersion );
    serialize( object.getFoo7(), Character.class, PROPERTY_FOO_7, serializeTo, formatVersion );
    serialize( object.isFoo8(), Boolean.class, PROPERTY_FOO_8, serializeTo, formatVersion );
    serialize( object.getFoo9(), String.class, PROPERTY_FOO_9, serializeTo, formatVersion );
  }

  @Override
  public
  @Nonnull
  Primitives deserialize( @Nonnull com.fasterxml.jackson.core.JsonParser deserializeFrom, @Nonnull com.cedarsoft.version.Version formatVersion ) throws IOException, com.cedarsoft.version.VersionException {
    verifyVersionWritable( formatVersion );

    int foo1 = -1;
    short foo2 = -1;
    byte foo3 = -1;
    long foo4 = -1;
    double foo5 = -1;
    float foo6 = -1;
    char foo7 = ( char ) -1;
    boolean foo8 = false;
    String foo9 = null;

    com.cedarsoft.serialization.jackson.JacksonParserWrapper parser = new com.cedarsoft.serialization.jackson.JacksonParserWrapper( deserializeFrom );
    while ( parser.nextToken() == com.fasterxml.jackson.core.JsonToken.FIELD_NAME ) {
      String currentName = parser.getCurrentName();

      if ( currentName.equals( PROPERTY_FOO_1 ) ) {
        parser.nextToken();
        foo1 = deserialize( Integer.class, formatVersion, deserializeFrom );
        continue;
      } else if ( currentName.equals( PROPERTY_FOO_2 ) ) {
        parser.nextToken();
        foo2 = deserialize( Short.class, formatVersion, deserializeFrom );
        continue;
      } else if ( currentName.equals( PROPERTY_FOO_3 ) ) {
        parser.nextToken();
        foo3 = deserialize( Byte.class, formatVersion, deserializeFrom );
        continue;
      } else if ( currentName.equals( PROPERTY_FOO_4 ) ) {
        parser.nextToken();
        foo4 = deserialize( Long.class, formatVersion, deserializeFrom );
        continue;
      } else if ( currentName.equals( PROPERTY_FOO_5 ) ) {
        parser.nextToken();
        foo5 = deserialize( Double.class, formatVersion, deserializeFrom );
        continue;
      } else if ( currentName.equals( PROPERTY_FOO_6 ) ) {
        parser.nextToken();
        foo6 = deserialize( Float.class, formatVersion, deserializeFrom );
        continue;
      } else if ( currentName.equals( PROPERTY_FOO_7 ) ) {
        parser.nextToken();
        foo7 = deserialize( Character.class, formatVersion, deserializeFrom );
        continue;
      } else if ( currentName.equals( PROPERTY_FOO_8 ) ) {
        parser.nextToken();
        foo8 = deserialize( Boolean.class, formatVersion, deserializeFrom );
        continue;
      } else if ( currentName.equals( PROPERTY_FOO_9 ) ) {
        parser.nextToken();
        foo9 = deserialize( String.class, formatVersion, deserializeFrom );
        continue;
      }
    }

    parser.verifyDeserialized( foo1, PROPERTY_FOO_1 );
    parser.verifyDeserialized( foo2, PROPERTY_FOO_2 );
    parser.verifyDeserialized( foo3, PROPERTY_FOO_3 );
    parser.verifyDeserialized( foo4, PROPERTY_FOO_4 );
    parser.verifyDeserialized( foo5, PROPERTY_FOO_5 );
    parser.verifyDeserialized( foo6, PROPERTY_FOO_6 );
    parser.verifyDeserialized( foo7, PROPERTY_FOO_7 );
    parser.verifyDeserialized( foo9, PROPERTY_FOO_9 );
    assert foo9 != null;

    parser.ensureObjectClosed();

    Primitives object = new Primitives( foo1, foo2, foo3, foo4, foo5, foo6, foo7, foo8, foo9 );
    return object;
  }
}