package com.grace.foresee;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.grace.foresee.kit.reflect.Reflector;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ReflectorTest {
    @Test
    public void newInstanceTest() {
        A a = Reflector.with("com.grace.foresee.A")
                .create()
                .get();
        Assert.assertNotNull(a);
    }

    @Test
    public void newInstanceUseArgsTest() {
        A a = Reflector.with("com.grace.foresee.A")
                .create("123")
                .get();
        Assert.assertNotNull(a);
    }

    @Test
    public void fieldTest() {
        String a = Reflector.with("com.grace.foresee.A")
                .create()
                .field("a")
                .get();
        Assert.assertEquals("abc", a);
    }

    @Test
    public void staticFieldTest() {
        int n = Reflector.with(A.class)
                .field("n")
                .get();
        Assert.assertEquals(1, n);
    }

    @Test
    public void fieldsTest() {
        Map<String, Reflector> fields = Reflector.with(A.class)
                .create()
                .fields();
        Assert.assertThat(fields.size(), Matchers.greaterThan(0));
    }

    @Test
    public void methodTest() {
        Reflector.with(A.class)
                .create()
                .call("print", new Object[]{null});
    }

    @Test
    public void proxyTest() {
        B proxy = Reflector.with(A.class)
                .create()
                .proxy(B.class);
        proxy.print();
    }
}
