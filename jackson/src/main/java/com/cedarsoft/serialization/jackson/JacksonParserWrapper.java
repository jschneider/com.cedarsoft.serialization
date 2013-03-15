/**
 * Copyright (C) cedarsoft GmbH.
 *
 * Licensed under the GNU General Public License version 3 (the "License")
 * with Classpath Exception; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *         http://www.cedarsoft.org/gpl3ce
 *         (GPL 3 with Classpath Exception)
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation. cedarsoft GmbH designates this
 * particular file as subject to the "Classpath" exception as provided
 * by cedarsoft GmbH in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */
package com.cedarsoft.serialization.jackson;

import com.cedarsoft.version.Version;
import com.cedarsoft.serialization.jackson.AbstractJacksonSerializer;
import com.cedarsoft.serialization.jackson.JacksonSerializer;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.FormatSchema;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.type.TypeReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonParserWrapper {
  @Nonnull
  private final JsonParser parser;

  public JacksonParserWrapper( @Nonnull JsonParser parser ) {
    this.parser = parser;
  }

  public void startObject() throws IOException, JsonParseException {
    nextToken( JsonToken.START_OBJECT );
  }

  public void endObject() throws IOException, JsonParseException {
    nextToken( JsonToken.END_OBJECT );
  }

  /**
   * Verifies the next field has the given name and prepares for read (by calling parser.nextToken).
   *
   * @param fieldName the field name
   * @throws IOException
   */
  public void nextFieldValue( @Nonnull String fieldName ) throws IOException {
    nextField( fieldName );
    parser.nextToken();
  }

  /**
   * Verifies that the next field starts.
   * When the content of the field shall be accessed, it is necessary to call parser.nextToken() afterwards.
   *
   * @param fieldName the field name
   * @throws IOException
   */
  public void nextField( @Nonnull String fieldName ) throws IOException {
    nextToken( JsonToken.FIELD_NAME );
    String currentName = parser.getCurrentName();

    if ( !fieldName.equals( currentName ) ) {
      throw new JsonParseException( "Invalid field. Expected <" + fieldName + "> but was <" + currentName + ">", parser.getCurrentLocation() );
    }
  }

  public void nextToken( @Nonnull JsonToken expected ) throws IOException {
    parser.nextToken();
    verifyCurrentToken( expected );
  }

  public void verifyCurrentToken( @Nonnull JsonToken expected ) throws JsonParseException {
    JsonToken current = parser.getCurrentToken();
    if ( current != expected ) {
      throw new JsonParseException( "Invalid token. Expected <" + expected + "> but got <" + current + ">", parser.getCurrentLocation() );
    }
  }

  public void closeObject() throws IOException {
    nextToken( JsonToken.END_OBJECT );
  }

  public void ensureObjectClosed() throws JsonParseException {
    AbstractJacksonSerializer.ensureObjectClosed( parser );
  }

  public void ensureParserClosed() throws IOException {
    AbstractJacksonSerializer.ensureParserClosed( parser );
  }

  //Delegating

  public int getValueAsInt( int defaultValue ) throws IOException, JsonParseException {
    return getParser().getValueAsInt( defaultValue );
  }

  public long getValueAsLong() throws IOException, JsonParseException {
    return getParser().getValueAsLong();
  }

  public long getValueAsLong( long defaultValue ) throws IOException, JsonParseException {
    return getParser().getValueAsLong( defaultValue );
  }

  public double getValueAsDouble() throws IOException, JsonParseException {
    return getParser().getValueAsDouble();
  }

  public double getValueAsDouble( double defaultValue ) throws IOException, JsonParseException {
    return getParser().getValueAsDouble( defaultValue );
  }

  public boolean getValueAsBoolean() throws IOException, JsonParseException {
    return getParser().getValueAsBoolean();
  }

  public boolean getValueAsBoolean( boolean defaultValue ) throws IOException, JsonParseException {
    return getParser().getValueAsBoolean( defaultValue );
  }

  public <T> T readValueAs( Class<T> valueType ) throws IOException, JsonProcessingException {
    return getParser().readValueAs( valueType );
  }

  public <T> T readValueAs( TypeReference<?> valueTypeRef ) throws IOException, JsonProcessingException {
    return getParser().<T>readValueAs( valueTypeRef );
  }

  public JsonNode readValueAsTree() throws IOException, JsonProcessingException {
    return getParser().readValueAsTree();
  }

  @Nonnull
  public JsonParser getParser() {
    return parser;
  }

  public ObjectCodec getCodec() {
    return getParser().getCodec();
  }

  public void setCodec( ObjectCodec c ) {
    getParser().setCodec( c );
  }

  public void setSchema( FormatSchema schema ) {
    getParser().setSchema( schema );
  }

  public boolean canUseSchema( FormatSchema schema ) {
    return getParser().canUseSchema( schema );
  }

  public org.codehaus.jackson.Version version() {
    return getParser().version();
  }

  public Object getInputSource() {
    return getParser().getInputSource();
  }

  public void close() throws IOException {
    getParser().close();
  }

  public int releaseBuffered( OutputStream out ) throws IOException {
    return getParser().releaseBuffered( out );
  }

  public int releaseBuffered( Writer w ) throws IOException {
    return getParser().releaseBuffered( w );
  }

  public JsonParser enable( JsonParser.Feature f ) {
    return getParser().enable( f );
  }

  public JsonParser disable( JsonParser.Feature f ) {
    return getParser().disable( f );
  }

  public JsonParser configure( JsonParser.Feature f, boolean state ) {
    return getParser().configure( f, state );
  }

  public boolean isEnabled( JsonParser.Feature f ) {
    return getParser().isEnabled( f );
  }

  public void setFeature( JsonParser.Feature f, boolean state ) {
    getParser().setFeature( f, state );
  }

  public void enableFeature( JsonParser.Feature f ) {
    getParser().enableFeature( f );
  }

  public void disableFeature( JsonParser.Feature f ) {
    getParser().disableFeature( f );
  }

  public boolean isFeatureEnabled( JsonParser.Feature f ) {
    return getParser().isFeatureEnabled( f );
  }

  public JsonToken nextToken() throws IOException, JsonParseException {
    return getParser().nextToken();
  }

  public JsonToken nextValue() throws IOException, JsonParseException {
    return getParser().nextValue();
  }

  public JsonParser skipChildren() throws IOException, JsonParseException {
    return getParser().skipChildren();
  }

  public boolean isClosed() {
    return getParser().isClosed();
  }

  public JsonToken getCurrentToken() {
    return getParser().getCurrentToken();
  }

  public boolean hasCurrentToken() {
    return getParser().hasCurrentToken();
  }

  public void clearCurrentToken() {
    getParser().clearCurrentToken();
  }

  public String getCurrentName() throws IOException, JsonParseException {
    return getParser().getCurrentName();
  }

  public JsonStreamContext getParsingContext() {
    return getParser().getParsingContext();
  }

  public JsonLocation getTokenLocation() {
    return getParser().getTokenLocation();
  }

  public JsonLocation getCurrentLocation() {
    return getParser().getCurrentLocation();
  }

  public JsonToken getLastClearedToken() {
    return getParser().getLastClearedToken();
  }

  public boolean isExpectedStartArrayToken() {
    return getParser().isExpectedStartArrayToken();
  }

  public String getText() throws IOException, JsonParseException {
    return getParser().getText();
  }

  public char[] getTextCharacters() throws IOException, JsonParseException {
    return getParser().getTextCharacters();
  }

  public int getTextLength() throws IOException, JsonParseException {
    return getParser().getTextLength();
  }

  public int getTextOffset() throws IOException, JsonParseException {
    return getParser().getTextOffset();
  }

  public boolean hasTextCharacters() {
    return getParser().hasTextCharacters();
  }

  public Number getNumberValue() throws IOException, JsonParseException {
    return getParser().getNumberValue();
  }

  public JsonParser.NumberType getNumberType() throws IOException, JsonParseException {
    return getParser().getNumberType();
  }

  public byte getByteValue() throws IOException, JsonParseException {
    return getParser().getByteValue();
  }

  public short getShortValue() throws IOException, JsonParseException {
    return getParser().getShortValue();
  }

  public int getIntValue() throws IOException, JsonParseException {
    return getParser().getIntValue();
  }

  public long getLongValue() throws IOException, JsonParseException {
    return getParser().getLongValue();
  }

  public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
    return getParser().getBigIntegerValue();
  }

  public float getFloatValue() throws IOException, JsonParseException {
    return getParser().getFloatValue();
  }

  public double getDoubleValue() throws IOException, JsonParseException {
    return getParser().getDoubleValue();
  }

  public BigDecimal getDecimalValue() throws IOException, JsonParseException {
    return getParser().getDecimalValue();
  }

  public boolean getBooleanValue() throws IOException, JsonParseException {
    return getParser().getBooleanValue();
  }

  public Object getEmbeddedObject() throws IOException, JsonParseException {
    return getParser().getEmbeddedObject();
  }

  public byte[] getBinaryValue( Base64Variant b64variant ) throws IOException, JsonParseException {
    return getParser().getBinaryValue( b64variant );
  }

  public byte[] getBinaryValue() throws IOException, JsonParseException {
    return getParser().getBinaryValue();
  }
}
