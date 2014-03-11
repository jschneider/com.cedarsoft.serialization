package com.cedarsoft.serialization.stax;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.cedarsoft.version.Version;
import com.cedarsoft.version.VersionException;
import com.cedarsoft.version.VersionRange;
import com.google.common.io.ByteStreams;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class PerformanceRunner {

  public static final int COUNT = 100000;

  public static void main(String[] args) throws Exception {
    AbstractStaxSerializer<Integer> serializer = new IntegerAbstractStaxSerializer();


    OutputStream out = ByteStreams.nullOutputStream();

    {
      long start = System.currentTimeMillis();
      for (int i = 0; i < COUNT; i++) {
        XMLOutputFactory xmlOutputFactory = StaxSupport.getXmlOutputFactory();
        XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(out);
        if (writer == null) {
          throw new IllegalStateException();
        }
        writer.close();
      }
      System.out.println("Took: " + (System.currentTimeMillis() - start));
    }

    {
      long start = System.currentTimeMillis();
      for (int i = 0; i < COUNT; i++) {
        XMLOutputFactory xmlOutputFactory = StaxSupport.getXmlOutputFactory();
        XMLStreamWriter writer = AbstractStaxSerializer.wrapWithIndent(xmlOutputFactory.createXMLStreamWriter(out));
        if (writer == null) {
          throw new IllegalStateException();
        }
        writer.close();
      }
      System.out.println("Took: " + (System.currentTimeMillis() - start));
    }

    {
      long start = System.currentTimeMillis();
      for (int i = 0; i < COUNT; i++) {
        XMLOutputFactory xmlOutputFactory = StaxSupport.getXmlOutputFactory();

        Class<?> indentingType = Class.forName("com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter");
        Constructor<?> constructor = indentingType.getConstructor(XMLStreamWriter.class);
        XMLStreamWriter writer = (XMLStreamWriter) constructor.newInstance(xmlOutputFactory.createXMLStreamWriter(out));
        if (writer == null) {
          throw new IllegalStateException();
        }
        writer.close();
      }
      System.out.println("Took: " + (System.currentTimeMillis() - start));
    }
  }

  private static class IntegerAbstractStaxSerializer extends AbstractStaxSerializer<Integer> {
    private IntegerAbstractStaxSerializer() {
      super("asdf", "asdfasdf", VersionRange.single(1, 0, 0));
    }

    @Override
    public void serialize(@Nonnull XMLStreamWriter serializeTo, @Nonnull Integer object, @Nonnull Version formatVersion) throws IOException, VersionException, XMLStreamException {
      throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public Integer deserialize(@Nonnull XMLStreamReader deserializeFrom, @Nonnull Version formatVersion) throws IOException, VersionException, XMLStreamException {
      throw new UnsupportedOperationException();
    }
  }
}
