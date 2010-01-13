package com.cedarsoft.serialization.ui;

import com.cedarsoft.Version;
import com.cedarsoft.serialization.DelegateMapping;
import com.cedarsoft.serialization.DelegatesMappings;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public class DelegatesMappingVisualizer {
  @NotNull
  @NonNls
  private static final String COL_SEPARATOR = "  ";
  @NotNull
  @NonNls
  private static final String FIRST_COLUMN_SEPARATOR = " -->";
  private static final int COL_WIDTH = 8;
  @NotNull
  @NonNls
  private static final String COL_VERSION_REPEAT = "|  ";
  @NotNull

  private final DelegatesMappings<?, ?, ?> mappings;

  public DelegatesMappingVisualizer( @NotNull DelegatesMappings<?, ?, ?> mappings ) {
    this.mappings = mappings;
  }

  @NotNull
  @NonNls
  public String visualize() throws IOException {
    StringWriter writer = new StringWriter();
    visualize( writer );
    return writer.toString();
  }

  public void visualize( @NotNull Writer out ) throws IOException {
    List<Column> columns = new ArrayList<Column>();

    //The versions
    SortedSet<Version> keyVersions = new TreeSet<Version>();
    for ( DelegateMapping mapping : mappings.getMappings().values() ) {
      keyVersions.add( mapping.getVersionRange().getMin() );
      keyVersions.add( mapping.getVersionRange().getMax() );

      for ( DelegateMapping.Entry entry : mapping.getEntries() ) {
        keyVersions.add( entry.getVersionRange().getMin() );
        keyVersions.add( entry.getVersionRange().getMax() );
      }
    }

    //The keys
    List<Class<?>> keys = new ArrayList<Class<?>>( mappings.getMappings().keySet() );
    Collections.sort( keys, new Comparator<Class<?>>() {
      @Override
      public int compare( Class<?> o1, Class<?> o2 ) {
        return o1.getName().compareTo( o2.getName() );
      }
    } );

    for ( Class<?> key : keys ) {
      DelegateMapping mapping = mappings.getMapping( key );

      List<Version> versions = new ArrayList<Version>();
      for ( Version keyVersion : keyVersions ) {
        versions.add( mapping.resolveVersion( keyVersion ) );
      }

      columns.add( new Column( key, versions ) );
    }

    writeHeadline( columns, out );
    writeSeparator( columns.size(), out );
    writeContent( new ArrayList<Version>( keyVersions ), columns, out );
    writeSeparator( columns.size(), out );
  }

  private void writeContent( @NotNull List<? extends Version> keyVersions, @NotNull List<? extends Column> columns, @NotNull Writer out ) throws IOException {
    for ( int i = 0, keyVersionsSize = keyVersions.size(); i < keyVersionsSize; i++ ) {
      Version keyVersion = keyVersions.get( i );
      out.write( extend( keyVersion.format() ) );
      out.write( FIRST_COLUMN_SEPARATOR );


      //Now write the columns
      for ( Column column : columns ) {
        out.write( COL_SEPARATOR );
        out.write( extend( column.lines.get( i ) ) );
      }
      out.write( "\n" );
    }
  }

  private static void writeSeparator( int columnsSize, @NotNull Writer out ) throws IOException {
    int count = COL_WIDTH;
    count += FIRST_COLUMN_SEPARATOR.length();
    count += COL_SEPARATOR.length() * columnsSize;
    count += COL_WIDTH * columnsSize;

    out.write( StringUtils.repeat( "-", count ) + "\n" );
  }

  protected void writeHeadline( @NotNull Iterable<? extends Column> columns, @NotNull Writer out ) throws IOException {
    out.write( extend( "" ) );//first column
    out.write( FIRST_COLUMN_SEPARATOR );

    for ( Column column : columns ) {
      out.write( COL_SEPARATOR ); //delimiter
      out.write( extend( column.header ) );
    }

    out.write( "\n" );
  }

  @NotNull
  @NonNls
  private static String extend( @NonNls @NotNull String string ) {
    if ( string.length() > COL_WIDTH ) {
      return string.substring( 0, COL_WIDTH );
    }

    return StringUtils.leftPad( string, COL_WIDTH );
  }

  @NotNull
  @NonNls
  private static String getRepresentation( @NotNull Class<?> type ) {
    String[] parts = type.getName().split( "\\." );
    return parts[parts.length - 1];
  }


  public static class Column {
    @NotNull
    @NonNls
    private final String header;
    @NotNull
    private final List<String> lines = new ArrayList<String>();

    public Column( @NotNull Class<?> type, @NotNull List<? extends Version> versions ) {
      this.header = getRepresentation( type );

      Version lastVersion = null;
      for ( Version version : versions ) {
        if ( version.equals( lastVersion ) ) {
          lines.add( COL_VERSION_REPEAT );
        } else {
          lines.add( version.format() );
        }
        lastVersion = version;
      }
    }
  }
}
