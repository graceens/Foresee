package com.grace.foresee.permissions.compiler;

import com.google.common.base.Strings;
import com.grace.foresee.kit.reflect.Reflector;
import com.grace.foresee.permissions.annotation.Constant;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public class PermissionProxyGenerator {

    /**
     * 生成权限代理代码
     *
     * @param owner 注解所有者的信息
     * @return 封装了源代码信息的对象
     */
    public static PermissionProxySource generate(Owner owner) {
        String className = owner.getClassName() + Constant.PERMISSION_PROXY_SUFFIX;
        PermissionProxySource source = new PermissionProxySource(owner.getPackageName(), className);

        TypeSpec typeSpec = TypeSpec.classBuilder(source.getSimpleClassName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(PermissionProxy.class),
                        ClassName.get(owner.getPackageName(), owner.getSimpleClassName())))
                .addField(FieldSpec.builder(int.class, "REQUEST_CODE", Modifier.PRIVATE,
                        Modifier.STATIC, Modifier.FINAL)
                        .initializer("$L", owner.getRequestCode())
                        .build())
                .addMethod(generateMethod("onGranted", owner, owner.getGrantedMethodName()))
                .addMethod(generateMethod("onDenied", owner, owner.getDeniedMethodName()))
                .addMethod(generateBooleanMethod("onRationale", owner, owner.getRationaleMethodName()))
                .addMethod(generateMethod("onPermanentlyDenied", owner, owner.getPermanentlyDeniedMethodName()))
                .build();

        source.setCode(JavaFile.builder(owner.getPackageName(), typeSpec)
                .indent("    ")
                .build()
                .toString());

        return source;
    }

    /**
     * 生成方法
     *
     * @param methodName      方法名称
     * @param owner           注解所有者的信息
     * @param ownerMethodName 被注解的实际方法名称
     * @return MethodSpec
     */
    private static MethodSpec generateMethod(String methodName, Owner owner, String ownerMethodName) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(owner.getPackageName(), owner.getSimpleClassName()), "source")
                .addParameter(int.class, "requestCode")
                .addParameter(String[].class, "permissions");

        if (!Strings.isNullOrEmpty(ownerMethodName)) {
            builder.beginControlFlow("if($N == $N)", "requestCode", "REQUEST_CODE")
                    .addStatement("$T.with(source).call($S, new $T[]{permissions})", Reflector.class,
                            ownerMethodName, Object.class)
                    .endControlFlow();
        }

        return builder.build();
    }

    private static MethodSpec generateBooleanMethod(String methodName, Owner owner, String ownerMethodName) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(ClassName.get(owner.getPackageName(), owner.getSimpleClassName()), "source")
                .addParameter(int.class, "requestCode")
                .addParameter(String[].class, "permissions");

        if (!Strings.isNullOrEmpty(ownerMethodName)) {
            builder.beginControlFlow("if($N == $N)", "requestCode", "REQUEST_CODE")
                    .addStatement("return $T.with(source).call($S, new $T[]{permissions}).get()", Reflector.class,
                            ownerMethodName, Object.class)
                    .endControlFlow()
                    .addStatement("return false");
        } else {
            builder.addStatement("return false");
        }

        return builder.build();
    }
}
