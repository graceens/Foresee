package com.grace.foresee.permissions.compiler;

import com.google.auto.service.AutoService;
import com.grace.foresee.permissions.annotation.Denied;
import com.grace.foresee.permissions.annotation.Granted;
import com.grace.foresee.permissions.annotation.PermanentlyDenied;
import com.grace.foresee.permissions.annotation.Rationale;
import com.grace.foresee.permissions.annotation.RequestCode;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class PermissionsProcessor extends AbstractProcessor {
    private Elements mElements;
    private Filer mFiler;
    private Messager mMessager;

    private Map<String, Owner> mOwnerMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mElements = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mOwnerMap.clear();

        // 收集被注解的类、方法、参数等信息
        if (!handleAnnotation(roundEnvironment, RequestCode.class)) {
            return false;
        }
        if (!handleAnnotation(roundEnvironment, Granted.class)) {
            return false;
        }
        if (!handleAnnotation(roundEnvironment, Denied.class)) {
            return false;
        }
        if (!handleAnnotation(roundEnvironment, Rationale.class)) {
            return false;
        }
        if (!handleAnnotation(roundEnvironment, PermanentlyDenied.class)) {
            return false;
        }

        for (Map.Entry<String, Owner> entry : mOwnerMap.entrySet()) {
            Writer writer = null;
            try {
                Owner owner = entry.getValue();
                // 生成代码
                PermissionProxySource source = PermissionProxyGenerator.generate(owner);
                // 写入文件
                writer = mFiler.createSourceFile(source.getClassName()).openWriter();
                writer.write(source.getCode());
            } catch (IOException e) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "Write file failed: " +
                        e.getMessage());
            }finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        return true;
    }

    /**
     * 处理注解（获取方法信息，并装载到mOwnerMap当中
     * @param environment RoundEnvironment
     * @param cls 注解的类型
     * @return 是否处理成功
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean handleAnnotation(RoundEnvironment environment, Class<? extends Annotation> cls) {
        Set<? extends Element> elements = environment.getElementsAnnotatedWith(cls);
        for (Element element : elements) {
            if (element.getModifiers().contains(Modifier.ABSTRACT)) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "Cannot have an abstract modifier");
                return false;
            }

            Owner owner;
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                PackageElement packageElement = mElements.getPackageOf(typeElement);
                String packageName = packageElement.getQualifiedName().toString();
                String className = typeElement.getQualifiedName().toString();

                owner = mOwnerMap.get(className);
                if (owner == null) {
                    owner = new Owner(packageName, className);

                    mOwnerMap.put(className, owner);
                }
                Annotation annotation = typeElement.getAnnotation(cls);
                if (annotation instanceof RequestCode) {
                    int requestCode = ((RequestCode) annotation).value();
                    owner.setRequestCode(requestCode);
                }
            } else if (element.getKind() == ElementKind.METHOD) {
                // 获得方法元素
                ExecutableElement executableElement = (ExecutableElement) element;
                // 获得方法所在的类元素
                TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
                // 获得方法所在的包元素
                PackageElement packageElement = mElements.getPackageOf(typeElement);
                String packageName = packageElement.getQualifiedName().toString();
                String className = typeElement.getQualifiedName().toString();
                String methodName = executableElement.getSimpleName().toString();

                if (cls == Rationale.class) {
                    // 检查返回类型
                    TypeMirror returnType = executableElement.getReturnType();
                    if (returnType.getKind() != TypeKind.BOOLEAN) {
                        String format = "The method [%s] return type must be boolean";
                        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(format, methodName));
                        return false;
                    }
                }

                // 检查形参
                List<? extends VariableElement> parameters = executableElement.getParameters();
                if (parameters == null || parameters.size() < 1) {
                    String format = "The method [%s] marked by annotation %s must have an unique " +
                            "parameter [String[]]";
                    mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(format, methodName,
                            cls.getSimpleName()));
                    return false;
                }

                owner = mOwnerMap.get(className);
                if (owner == null) {
                    owner = new Owner(packageName, className);

                    mOwnerMap.put(className, owner);
                }

                if (cls == Granted.class) {
                    owner.setGrantedMethodName(methodName);
                } else if (cls == Denied.class) {
                    owner.setDeniedMethodName(methodName);
                } else if(cls == Rationale.class){
                    owner.setRationaleMethodName(methodName);
                }else if (cls == PermanentlyDenied.class) {
                    owner.setPermanentlyDeniedMethodName(methodName);
                }
            } else {
                String format = "Annotation[%s] declaration error!";
                mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(format,
                        cls.getSimpleName()));
                return false;
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Granted.class.getCanonicalName());
        annotations.add(Denied.class.getCanonicalName());
        annotations.add(Rationale.class.getCanonicalName());
        annotations.add(PermanentlyDenied.class.getCanonicalName());
        annotations.add(RequestCode.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
