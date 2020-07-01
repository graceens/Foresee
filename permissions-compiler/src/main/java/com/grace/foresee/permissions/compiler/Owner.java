package com.grace.foresee.permissions.compiler;

import com.grace.foresee.permissions.annotation.Constant;

public class Owner {
    private String packageName;
    private String className;
    private String simpleClassName;
    private String grantedMethodName;
    private String deniedMethodName;
    private String rationaleMethodName;
    private String permanentlyDeniedMethodName;
    private int requestCode;

    public Owner(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
        this.simpleClassName = className.substring(packageName.length()).replace(".", "");
        this.requestCode = Constant.REQUEST_CODE;
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

    public String getGrantedMethodName() {
        return grantedMethodName;
    }

    public void setGrantedMethodName(String grantedMethodName) {
        this.grantedMethodName = grantedMethodName;
    }

    public String getDeniedMethodName() {
        return deniedMethodName;
    }

    public void setDeniedMethodName(String deniedMethodName) {
        this.deniedMethodName = deniedMethodName;
    }

    public String getRationaleMethodName() {
        return rationaleMethodName;
    }

    public void setRationaleMethodName(String rationaleMethodName) {
        this.rationaleMethodName = rationaleMethodName;
    }

    public String getPermanentlyDeniedMethodName() {
        return permanentlyDeniedMethodName;
    }

    public void setPermanentlyDeniedMethodName(String permanentlyDeniedMethodName) {
        this.permanentlyDeniedMethodName = permanentlyDeniedMethodName;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}
