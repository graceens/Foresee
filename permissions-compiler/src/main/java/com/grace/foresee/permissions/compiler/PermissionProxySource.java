package com.grace.foresee.permissions.compiler;

public class PermissionProxySource {
    private String packageName;
    private String className;
    private String simpleClassName;
    private String code;

    public PermissionProxySource(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
        this.simpleClassName = className.substring(packageName.length()).replace(".",
                "");
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
