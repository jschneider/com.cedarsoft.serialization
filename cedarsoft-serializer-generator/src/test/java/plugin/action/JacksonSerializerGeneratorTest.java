package plugin.action;

import com.cedarsoft.serialization.generator.intellij.jackson.JacksonSerializerGenerator;
import com.cedarsoft.serialization.generator.intellij.jackson.JacksonSerializerResolver;
import com.cedarsoft.serialization.generator.intellij.model.SerializerModel;
import com.cedarsoft.serialization.generator.intellij.model.SerializerModelFactory;
import com.google.common.collect.ImmutableList;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiField;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.MultiFileTestCase;
import org.junit.*;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class JacksonSerializerGeneratorTest extends MultiFileTestCase {
  @Override
  protected Sdk getTestProjectJdk() {
    return new Jdk17MockProjectDescriptor().getSdk();
  }

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
  public void testCollections() throws Throwable {
    doTest( new PerformAction() {
      @Override
      public void performAction( VirtualFile rootDir, VirtualFile rootAfter ) throws Exception {
        PsiClass classToSerialize = myJavaFacade.findClass( getTestName( false ), GlobalSearchScope.allScope( getProject() ) );
        assertThat( classToSerialize ).isNotNull();
        assertThat( classToSerialize.getQualifiedName() ).isEqualTo( getTestName( false ) );

        SerializerModelFactory serializerModelFactory = new SerializerModelFactory(new JacksonSerializerResolver( getProject() ), JavaCodeStyleManager.getInstance( getProject() ), JavaPsiFacade.getInstance(getProject()));
        SerializerModel model = serializerModelFactory.create( classToSerialize, ImmutableList.copyOf(classToSerialize.getAllFields() ) );

        PsiDirectory dir = classToSerialize.getContainingFile().getContainingDirectory();

        JacksonSerializerGenerator generator = new JacksonSerializerGenerator( getProject() );
        PsiClass serializer = generator.generate( model, dir );
        assertThat( serializer.getName() ).isEqualTo( "CollectionsSerializer" );
      }
    } );
  }

  @Test
  public void testCollections2() throws Throwable {
    doTest( new PerformAction() {
      @Override
      public void performAction( VirtualFile rootDir, VirtualFile rootAfter ) throws Exception {
        PsiClass classToSerialize = myJavaFacade.findClass( getTestName( false ), GlobalSearchScope.allScope( getProject() ) );
        assertThat( classToSerialize ).isNotNull();
        assertThat( classToSerialize.getQualifiedName() ).isEqualTo( getTestName( false ) );

        SerializerModelFactory serializerModelFactory = new SerializerModelFactory(new JacksonSerializerResolver( getProject() ), JavaCodeStyleManager.getInstance( getProject() ), JavaPsiFacade.getInstance(getProject()));
        SerializerModel model = serializerModelFactory.create( classToSerialize, ImmutableList.copyOf(classToSerialize.getAllFields() ) );

        PsiDirectory dir = classToSerialize.getContainingFile().getContainingDirectory();

        JacksonSerializerGenerator generator = new JacksonSerializerGenerator( getProject() );
        PsiClass serializer = generator.generate( model, dir );
        assertThat( serializer.getName() ).isEqualTo( "Collections2Serializer" );
      }
    } );
  }

  @Test
  public void testSimple() throws Throwable {
    doTest( new PerformAction() {
      @Override
      public void performAction( VirtualFile rootDir, VirtualFile rootAfter ) throws Exception {
        PsiClass simple = myJavaFacade.findClass( getTestName( false ), GlobalSearchScope.allScope( getProject() ) );
        assertThat( simple ).isNotNull();
        assertThat( simple.getQualifiedName() ).isEqualTo( getTestName( false ) );

        SerializerModelFactory serializerModelFactory = new SerializerModelFactory(new JacksonSerializerResolver( getProject() ), JavaCodeStyleManager.getInstance( getProject() ), JavaPsiFacade.getInstance(getProject()));
        SerializerModel model = serializerModelFactory.create( simple, ImmutableList.of( simple.findFieldByName( "foo", false ) ) );

        PsiDirectory dir = simple.getContainingFile().getContainingDirectory();

        JacksonSerializerGenerator generator = new JacksonSerializerGenerator( getProject() );
        PsiClass serializer = generator.generate( model, dir );
        assertThat( serializer.getName() ).isEqualTo( "SimpleSerializer" );
      }
    } );
  }

  @Test
  public void testWithPackage() throws Throwable {
    doTest( new PerformAction() {
      @Override
      public void performAction( VirtualFile rootDir, VirtualFile rootAfter ) throws Exception {
        PsiClass simple = myJavaFacade.findClass("com.cedarsoft.test.WithPackage", GlobalSearchScope.allScope( getProject() ) );
        assertThat( simple ).isNotNull();
        assertThat( simple.getQualifiedName() ).isEqualTo( "com.cedarsoft.test.WithPackage" );

        SerializerModelFactory serializerModelFactory = new SerializerModelFactory(new JacksonSerializerResolver( getProject() ), JavaCodeStyleManager.getInstance( getProject() ), JavaPsiFacade.getInstance(getProject()));
        SerializerModel model = serializerModelFactory.create( simple, ImmutableList.of( simple.findFieldByName( "foo", false ) ) );

        PsiDirectory dir = simple.getContainingFile().getContainingDirectory();

        JacksonSerializerGenerator generator = new JacksonSerializerGenerator( getProject() );
        PsiClass serializer = generator.generate( model, dir );
        assertThat( serializer.getName() ).isEqualTo( "WithPackageSerializer" );
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

        SerializerModelFactory serializerModelFactory = new SerializerModelFactory(new JacksonSerializerResolver( getProject() ), JavaCodeStyleManager.getInstance( getProject() ), JavaPsiFacade.getInstance(getProject()));
        SerializerModel model = serializerModelFactory.create( simple, ImmutableList.of( simple.findFieldByName( "foo", false ) ) );

        PsiDirectory dir = simple.getContainingFile().getContainingDirectory();

        JacksonSerializerGenerator generator = new JacksonSerializerGenerator( getProject() );
        PsiClass serializer = generator.generate( model, dir );
        assertThat( serializer.getName() ).isEqualTo( "SetterSerializer" );
      }
    } );
  }

  @Test
  public void testPrimitives() throws Throwable {
    doTest( new PerformAction() {
      @Override
      public void performAction( VirtualFile rootDir, VirtualFile rootAfter ) throws Exception {
        PsiClass foo = myJavaFacade.findClass( getTestName( false ), GlobalSearchScope.allScope( getProject() ) );
        assertThat( foo ).isNotNull();
        assertThat( foo.getQualifiedName() ).isEqualTo( getTestName( false ) );

        SerializerModelFactory serializerModelFactory = new SerializerModelFactory(new JacksonSerializerResolver( getProject() ), JavaCodeStyleManager.getInstance( getProject() ), JavaPsiFacade.getInstance(getProject()));
        SerializerModel model = serializerModelFactory.create( foo, ImmutableList.<PsiField>copyOf( foo.getAllFields() ) );

        PsiDirectory dir = foo.getContainingFile().getContainingDirectory();

        JacksonSerializerGenerator generator = new JacksonSerializerGenerator( getProject() );
        PsiClass serializer = generator.generate( model, dir );
        assertThat( serializer.getName() ).isEqualTo( "PrimitivesSerializer" );
      }
    } );
  }
}
