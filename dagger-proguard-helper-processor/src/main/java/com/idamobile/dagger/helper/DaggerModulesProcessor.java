package com.idamobile.dagger.helper;

import dagger.Module;
import dagger.Provides;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.io.*;
import java.util.*;

@SupportedAnnotationTypes("dagger.Module")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class DaggerModulesProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            System.out.println("dagger-helper: searching for modules");
            Set<String> keepNames = new HashSet<String>();
            for (Element elem : roundEnv.getElementsAnnotatedWith(Inject.class)) {
                if (elem.getKind() == ElementKind.FIELD) {
                    TypeMirror typeMirror = elem.asType();
                    addIfNeeded(typeMirror, keepNames);
                    addEnclosingClassName(elem, keepNames);
                } else if (elem.getKind() == ElementKind.CONSTRUCTOR
                        || elem.getKind() == ElementKind.METHOD
                        || elem.getKind() == ElementKind.PARAMETER) {
                    addEnclosingClassName(elem, keepNames);
                }
            }
            for (Element elem : roundEnv.getElementsAnnotatedWith(Provides.class)) {
                if (elem.getKind() == ElementKind.METHOD) {
                    ExecutableElement executable = (ExecutableElement) elem;
                    TypeMirror returnType = executable.getReturnType();
                    addIfNeeded(returnType, keepNames);
                }
            }
            for (Element elem : roundEnv.getElementsAnnotatedWith(Module.class)) {
                Module module = elem.getAnnotation(Module.class);
                try {
                    module.injects();
                } catch (MirroredTypeException e) {
                    addFromMirrorsIfNeeded(Collections.singletonList(e.getTypeMirror()), keepNames);
                } catch (MirroredTypesException e) {
                    addFromMirrorsIfNeeded(e.getTypeMirrors(), keepNames);
                }
            }
            try {
                createProGuardFile(keepNames);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private void addFromMirrorsIfNeeded(List<? extends TypeMirror> typeMirrors, Set<String> keepNames) {
        if (typeMirrors != null && !typeMirrors.isEmpty()) {
            for (TypeMirror mirror : typeMirrors) {
                if (mirror != null) {
                    addIfNeeded(mirror, keepNames);
                }
            }
        }
    }

    private void addIfNeeded(TypeMirror type, Set<String> keepNames) {
        TypeParams params = new TypeParams(type, processingEnv);
        addIfNeeded(params, keepNames);

        Types typeUtils = processingEnv.getTypeUtils();
        TypeElement element = (TypeElement) typeUtils.asElement(type);
        while (element != null && !element.toString().equals(Object.class.getName())) {
            addIfNeeded(new TypeParams(element.asType(), processingEnv), keepNames);
            TypeMirror superclass = element.getSuperclass();
            if (superclass != null) {
                element = (TypeElement) typeUtils.asElement(superclass);
            } else {
                element = null;
            }
        }
    }

    private void addIfNeeded(TypeParams type, Set<String> keepNames) {
        if (type.isKeepRequaried()) {
            if (keepNames.add(type.getKeepName())) {
                System.out.println("dagger-helper: found new dependent type " + type.getName());
            }
        }
        for (TypeParams params : type.getGenerics()) {
            addIfNeeded(params, keepNames);
        }
    }

    private void addEnclosingClassName(Element elem, Set<String> keepNames) {
        Element enclosingClass = elem.getEnclosingElement();
        for (; enclosingClass != null && enclosingClass.getKind() != ElementKind.CLASS; enclosingClass = elem.getEnclosingElement());
        if (enclosingClass != null) {
            addIfNeeded(enclosingClass.asType(), keepNames);
        }
    }

    private void createProGuardFile(Set<String> keepNames) throws IOException {
        JavaFileManager.Location location = StandardLocation.SOURCE_OUTPUT;
        final String relativeName = UUID.randomUUID().toString();
        FileObject resource = processingEnv.getFiler().createResource(location, "", relativeName);

        final String name = resource.getName();
        final int targetIndexOf = name.indexOf("target");
        final int buildIndexOf = name.indexOf("build");
        final String basePath;
        if (targetIndexOf > 0) {
            basePath = name.substring(0, targetIndexOf);
        } else if (buildIndexOf > 0) {
            basePath = name.substring(0, buildIndexOf);
        } else {
            basePath = "";
            System.out.println("dagger-helper: could not get base path of: \"" + name + "\"");
        }
        File file = new File(basePath, "dagger-proguard-keepnames.cfg");
        if (file.exists() && keepNames.isEmpty()) {
            return;
        }

        System.out.println("dagger-helper: generating output file " + file.getAbsolutePath());
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write("# do not modify that file, it's rewriting on each build by dagger-proguard-helper and your changes will be removed");
            writer.newLine();
            for (String className : keepNames) {
                writer.write("-keepnames class ");
                writer.write(className);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}