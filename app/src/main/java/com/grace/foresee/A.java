package com.grace.foresee;

import com.grace.foresee.logger.Logger;

public class A implements B{
    private String a;
    private static int n = 1;

    public A() {
        a = "abc";
    }

    public A(String a) {
        this.a = a;
    }

    public void print() {
        Logger.i(a);
    }

    public void print(String s) {
        Logger.i(s);
    }

    public static void sPrint() {
        Logger.i("static method");
    }
}
