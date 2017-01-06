package com.cedarsoft.serialization.generator.intellij.action;

import com.cedarsoft.serialization.generator.intellij.SerializerGenerator;
import com.cedarsoft.serialization.generator.intellij.SerializerResolver;
import com.cedarsoft.serialization.generator.intellij.SerializerTestsGenerator;
import com.cedarsoft.serialization.generator.intellij.jackson.JacksonSerializerGenerator;
import com.cedarsoft.serialization.generator.intellij.jackson.JacksonSerializerResolver;
import com.cedarsoft.serialization.generator.intellij.jackson.JacksonSerializerTestsGenerator;
import com.cedarsoft.serialization.generator.intellij.model.SerializerModel;
import com.cedarsoft.serialization.generator.intellij.model.SerializerModelFactory;
import com.cedarsoft.serialization.generator.intellij.stax.mate.StaxMateSerializerGenerator;
import com.cedarsoft.serialization.generator.intellij.stax.mate.StaxMateSerializerResolver;
import com.cedarsoft.serialization.generator.intellij.stax.mate.StaxMateSerializerTestsGenerator;
import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.PackageWrapper;
import com.intellij.refactoring.util.RefactoringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class GenerateSerializerAction extends AnAction {
  @Override
  public void update(AnActionEvent e) {
    super.update(e);
    e.getPresentation().setEnabled(getCurrentClass(e) != null);
    //if ( ActionPlaces.isPopupPlace( e.getPlace() ) ) {
    //  e.getPresentation().setVisible( enabled );
    //}
  }

  public static boolean isAcceptableFile(@Nonnull PsiFile file) {
    Language language = file.getLanguage();
    return JavaLanguage.INSTANCE.getID().equals(language.getID());
  }

  @Override
  public void actionPerformed(final AnActionEvent e) {
    @Nullable final PsiClass psiClass = getCurrentClass(e);
    if (psiClass == null) {
      throw new IllegalStateException("No class found");
    }

    final GenerateSerializerDialog generateSerializerDialog = new GenerateSerializerDialog(psiClass);
    generateSerializerDialog.show();

    if (!generateSerializerDialog.isOK()) {
      return;
    }

    //TODO add error handling, add progress dialog
    final Project project = getEventProject(e);
    assert project != null;

    new WriteCommandAction.Simple<Object>(project, "Generate Serializer") {
      @Override
      protected void run() throws Throwable {
        GenerateSerializerDialog.Dialect dialect = generateSerializerDialog.getSelectedDialect();
        List<? extends PsiField> selectedFields = generateSerializerDialog.getSelectedFields();

        //Calculate the target directories
        PsiDirectory serializerTargetDir = generateSerializerDialog.getSerializerDestination();
        PsiDirectory testsTargetDir = generateSerializerDialog.getTestsDestination();
        PsiDirectory testResourcesTargetDir = generateSerializerDialog.getTestResourcesDestination();


        SerializerResolver serializerResolver = createSerializerResolver(dialect, project);


        SerializerModelFactory serializerModelFactory = new SerializerModelFactory(serializerResolver, JavaCodeStyleManager.getInstance(project), JavaPsiFacade.getInstance(project));
        SerializerModel model = serializerModelFactory.create(psiClass, selectedFields);

        SerializerGenerator serializerGenerator = createSerializerGenerator(dialect, psiClass);
        PsiClass serializer = serializerGenerator.generate(model, serializerTargetDir);

        SerializerTestsGenerator testsGenerator = createSerializerTestsGenerator(dialect, psiClass);
        testsGenerator.generate(model, testsTargetDir, testResourcesTargetDir);
      }
    }.execute();

    //
    //    Project serializerProject = serializer.getProject();
    //    @Nullable PsiFile containingFile = serializer.getContainingFile();
    //
    //    if (containingFile == null) {
    //      throw new IllegalStateException("--> null file for " + serializer);
    //    }

    //Editor editor = CodeInsightUtil.positionCursor( serializerProject, containingFile, serializer.getLBrace() );
    //System.out.println( "Finished: " + psiClass );


    //JavaPsiFacade psiFacade = JavaPsiFacade.getInstance( project );
    //
    //final Application application = ApplicationManager.getApplication();
    //
    //@javax.annotation.Nullable PsiFile psiFile = PsiManager.getInstance( project ).findFile( file );
    //if ( psiFile == null ) {
    //  throw new IllegalStateException( "No psi file found for <" + file + ">" );
    //}
    //
    //
    //String name = file.getName();
    //PsiFile[] filesByName = FilenameIndex.getFilesByName( project, name, GlobalSearchScope.allScope( project ) );
    //
    //System.out.println( Arrays.toString( filesByName ) );

    //PsiFile psiFile = filesByName[0];
    //
    //psiFile.getVirtualFile();


    //AnnotatedMembersSearch.search( psiFacade.findClass( annotationName, globalSearchScope ) );
    //
    //
    //CommandProcessor.getInstance().executeCommand( project, new Runnable() {
    //  @Override
    //  public void run() {
    //    System.out.println( "Running Command" );
    //
    //    try {
    //      Thread.sleep( 5000 );
    //    } catch ( InterruptedException ie ) {
    //      throw new RuntimeException( ie );
    //    }
    //
    //    System.out.println( "finished..." );
    //  }
    //}, null, null );
  }

  @Nonnull
  private static SerializerResolver createSerializerResolver(@Nonnull GenerateSerializerDialog.Dialect dialect, @Nonnull Project project) {
    switch (dialect) {
      case JACKSON:
        return new JacksonSerializerResolver(project);
      case STAX_MATE:
        return new StaxMateSerializerResolver(project);
    }

    throw new IllegalArgumentException("Unsupported dialect " + dialect);
  }

  @Nonnull
  private static SerializerTestsGenerator createSerializerTestsGenerator(@Nonnull GenerateSerializerDialog.Dialect dialect, @Nonnull PsiClass psiClass) {
    switch (dialect) {
      case JACKSON:
        return new JacksonSerializerTestsGenerator(psiClass.getProject());
      case STAX_MATE:
        return new StaxMateSerializerTestsGenerator(psiClass.getProject());
    }

    throw new IllegalArgumentException("Unsupported dialect " + dialect);
  }

  @Nonnull
  private static SerializerGenerator createSerializerGenerator(@Nonnull GenerateSerializerDialog.Dialect dialect, @Nonnull PsiClass psiClass) {
    switch (dialect) {
      case JACKSON:
        return new JacksonSerializerGenerator(psiClass.getProject());
      case STAX_MATE:
        return new StaxMateSerializerGenerator(psiClass.getProject());
    }

    throw new IllegalArgumentException("Unsupported dialect " + dialect);
  }

  void generateSerializer(@Nonnull final PsiClass psiClass, @Nonnull List<? extends PsiField> selectedFields) {
    System.out.println("Generating Serializer for: ");
    for (PsiField selectedField : selectedFields) {
      System.out.println("\t" + selectedField.getName() + " - " + selectedField.getType().getPresentableText());
    }

    PsiFile psiFile = psiClass.getContainingFile();

    new WriteCommandAction.Simple(psiClass.getProject(), psiFile) {
      @Override
      protected void run() throws Throwable {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(getProject());
        JavaCodeStyleManager codeStyleManager = JavaCodeStyleManager.getInstance(getProject());

        //StringBuilder builder = new StringBuilder();
        //builder.append( "public void deserializeStuff(){" )
        //  .append( psiClass.getName() ).append( ".class.getName();" )
        //  .append( "}" );
        //
        //PsiElement method = psiClass.add( elementFactory.createMethodFromText( builder.toString(), psiClass ) );
        //
        //codeStyleManager.shortenClassReferences( method );


        PsiReferenceList implementsList = psiClass.getImplementsList();
        if (implementsList == null) {
          throw new IllegalStateException("no implements list found");
        }

        //PsiElement implementsReference = implementsList.add( elementFactory.createReferenceFromText( "Comparable<" + psiClass.getQualifiedName() + ">", psiClass ) );
        //codeStyleManager.shortenClassReferences( implementsReference );


        if (isUnderTestSources(psiClass)) {
          throw new IllegalStateException("Is a test source!");
        }


        Module srcModule = ModuleUtilCore.findModuleForPsiElement(psiClass);
        if (srcModule == null) {
          throw new IllegalStateException("No src module found");
        }

        PsiDirectory srcDir = psiClass.getContainingFile().getContainingDirectory();
        PsiPackage srcPackage = JavaDirectoryService.getInstance().getPackage(srcDir);


        //PsiClass serializerClass = elementFactory.createClass( psiClass.getQualifiedName() + "Serializer" );


        final Set<VirtualFile> testFolders = new HashSet<VirtualFile>();
        fillTestRoots(srcModule, testFolders);

        if (testFolders.isEmpty()) {
          throw new IllegalStateException("No test folders found");
        }

        VirtualFile testFolder = testFolders.iterator().next();


        final String packageName = "com.cedarsoft.test.hardcoded.test";
        PsiManager psiManager = PsiManager.getInstance(getProject());
        final PackageWrapper targetPackage = new PackageWrapper(psiManager, packageName);

        PsiDirectory targetPackageDir = RefactoringUtil.createPackageDirectoryInSourceRoot(targetPackage, testFolder);

        PsiClass test = createTest(getProject(), targetPackageDir, psiClass);
        Editor editor = CodeInsightUtil.positionCursor(getProject(), test.getContainingFile(), test.getLBrace());
      }
    }.execute();
  }

  private PsiClass createTest(@Nonnull Project project, @Nonnull PsiDirectory targetDir, @Nonnull PsiClass psiClass) {
    IdeDocumentHistory.getInstance(project).includeCurrentPlaceAsChangePlace();
    return JavaDirectoryService.getInstance().createClass(targetDir, psiClass.getName() + "SerializerTest");
  }

  protected static void fillTestRoots(@Nonnull Module srcModule, @Nonnull Set<VirtualFile> testFolders) {
    checkForTestRoots(srcModule, testFolders, new HashSet<Module>());
  }

  private static void checkForTestRoots(@Nonnull final Module srcModule, @Nonnull final Set<VirtualFile> testFolders, @Nonnull final Set<Module> processed) {
    final boolean isFirst = processed.isEmpty();
    if (!processed.add(srcModule)) {
      return;
    }

    final ContentEntry[] entries = ModuleRootManager.getInstance(srcModule).getContentEntries();
    for (ContentEntry entry : entries) {
      for (SourceFolder sourceFolder : entry.getSourceFolders()) {
        if (sourceFolder.isTestSource()) {
          final VirtualFile sourceFolderFile = sourceFolder.getFile();
          if (sourceFolderFile != null) {
            testFolders.add(sourceFolderFile);
          }
        }
      }
    }
    if (isFirst && !testFolders.isEmpty()) {
      return;
    }

    final HashSet<Module> modules = new HashSet<Module>();
    ModuleUtilCore.collectModulesDependsOn(srcModule, modules);
    for (Module module : modules) {

      checkForTestRoots(module, testFolders, processed);
    }
  }

  private static boolean isUnderTestSources(@Nonnull PsiClass psiClass) {
    ProjectRootManager projectRootManager = ProjectRootManager.getInstance(psiClass.getProject());
    VirtualFile file = psiClass.getContainingFile().getVirtualFile();
    if (file == null) {
      return false;
    }
    return projectRootManager.getFileIndex().isInTestSourceContent(file);
  }

  @Nullable
  private static PsiClass getCurrentClass(@Nonnull AnActionEvent e) {
    @Nullable Project project = PlatformDataKeys.PROJECT.getData(e.getDataContext());
    if (project == null) {
      return null;
    }

    @Nullable PsiFile psiFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    if (psiFile == null) {
      return null;
    }

    @Nullable Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());
    if (editor == null) {
      return null;
    }


    PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
    return PsiTreeUtil.getParentOfType(element, PsiClass.class);
  }
}
