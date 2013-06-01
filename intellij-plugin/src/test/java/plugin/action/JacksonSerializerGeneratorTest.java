package plugin.action;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.MultiFileTestCase;
import org.junit.*;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonSerializerGeneratorTest extends MultiFileTestCase {
  @Override
  protected String getTestDataPath() {
    return new File( "testData" ).getAbsolutePath();
  }

  @Override
  protected String getTestRoot() {
    String className = getClass().getName();

    int lastIndex = className.lastIndexOf( '.' );
    String relevantName = className.substring( lastIndex + 1 );

    return "/" + relevantName + "/";
  }

  @Test
  public void testSimple() throws Throwable {
    doTest( new PerformAction() {
      @Override
      public void performAction( VirtualFile rootDir, VirtualFile rootAfter ) throws Exception {
        PsiClass simple = myJavaFacade.findClass( getTestName( false ), GlobalSearchScope.allScope( getProject() ) );
        assertThat( simple ).isNotNull();
        assertThat( simple.getQualifiedName() ).isEqualTo( getTestName( false ) );

        JacksonSerializerGenerator generator = new JacksonSerializerGenerator( getProject() );
        PsiClass serializer = generator.generate( simple, ImmutableList.of( simple.findFieldByName( "foo", false ) ) );
        assertThat( serializer.getQualifiedName() ).isEqualTo( "SimpleSerializer" );
      }
    } );
  }

  @Test
  public void testSetter() throws Throwable {
    doTest( new PerformAction() {
      @Override
      public void performAction( VirtualFile rootDir, VirtualFile rootAfter ) throws Exception {
        PsiClass simple = myJavaFacade.findClass( getTestName( false ), GlobalSearchScope.allScope( getProject() ) );
        assertThat( simple ).isNotNull();
        assertThat( simple.getQualifiedName() ).isEqualTo( getTestName( false ) );

        JacksonSerializerGenerator generator = new JacksonSerializerGenerator( getProject() );
        PsiClass serializer = generator.generate( simple, ImmutableList.of( simple.findFieldByName( "foo", false ) ) );
        assertThat( serializer.getQualifiedName() ).isEqualTo( "SetterSerializer" );
      }
    } );
  }
}
