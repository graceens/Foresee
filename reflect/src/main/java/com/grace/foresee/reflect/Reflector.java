package com.grace.foresee.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Reflector {
    private final Object obj;
    private final boolean isClass;

    public static Reflector with(String name) {
        return new Reflector(getClass(name));
    }

    public static Reflector with(Object obj) {
        return new Reflector(obj);
    }

    public static Reflector with(Class<?> cls) {
        return new Reflector(cls);
    }

    /**
     * 调用反射对象的方法
     *
     * @param obj    反射对象
     * @param method 方法
     * @param args   方法参数
     * @return 如果方法返回值不是void，则返回封装了反射对象的Reflector，否则返回封装了返回对象的Reflector
     * @throws ReflectException 反射异常
     */
    public static Reflector with(Object obj, Method method, Object... args) throws ReflectException {
        try {
            accessible(method);

            if (method.getReturnType() == void.class) {
                method.invoke(obj, args);
                return with(obj);
            } else {
                return with(method.invoke(obj, args));
            }
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 通过构造器和构造参数实例化反射对象
     *
     * @param constructor 构造器
     * @param args        构造参数
     * @return Reflector
     * @throws ReflectException 反射异常
     */
    public static Reflector with(Constructor<?> constructor, Object... args) throws ReflectException {
        try {
            return with(accessible(constructor).newInstance(args));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 通过类路径获得Class
     *
     * @param name 类的路径
     * @return Class
     * @throws ReflectException 反射异常
     */
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 通过类路径获得Class
     *
     * @param name        类的路径
     * @param classLoader 类加载器
     * @return Class
     * @throws ReflectException 反射异常
     */
    public static Class<?> getClass(String name, ClassLoader classLoader) throws ReflectException {
        try {
            return Class.forName(name, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 通过Type获得具体的Class
     *
     * @param type Type
     * @return 具体的Class
     */
    public static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return getClass(parameterizedType.getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 通过Type数组获得Class数组
     *
     * @param types Type数组
     * @return Class数组
     */
    public static Class<?>[] getClasses(Type[] types) {
        if (types == null) {
            return null;
        }

        if (types.length == 0) {
            return new Class[0];
        }

        Class<?>[] classes = new Class[types.length];
        for (int i = 0; i < types.length; i++) {
            classes[i] = getClass(types[i]);
        }

        return classes;
    }

    /**
     * 将没有访问权限的对象设置为可访问
     *
     * @param accessible 访问对象
     * @param <T>        继承于AccessibleObject的类
     * @return 访问对象
     */
    public static <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null) {
            return null;
        }

        if (accessible instanceof Member) {
            Member member = (Member) accessible;
            if (Modifier.isPublic(member.getModifiers())
                    && Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                return accessible;
            }
        }

        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }
        return accessible;
    }

    /**
     * 去掉封装，返回实际的反射对象
     *
     * @param obj 可能为Reflector或普通的Object
     * @return 实际的反射对象
     */
    public static Object unwrap(Object obj) {
        if (obj instanceof Reflector) {
            return ((Reflector) obj).get();
        }
        return obj;
    }

    /**
     * 获取参数对象数组的Class数组
     *
     * @param args 参数对象数组
     * @return 参数对象数组的Class数组
     */
    public static Class<?>[] getTypes(Object... args) {
        if (args == null) {
            return new Class[0];
        }

        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] == null ? NULL.class : args[i].getClass();
        }

        return types;
    }

    /**
     * 检查是否基础类型，如果是基础类型，则返回基础类型的class，否则原样返回
     *
     * @param type 类型
     * @return 真实类型
     */
    public static Class<?> realType(Class<?> type) {
        if (type == null) {
            return null;
        } else if (type.isPrimitive()) {
            if (boolean.class == type) {
                return Boolean.class;
            } else if (int.class == type) {
                return Integer.class;
            } else if (long.class == type) {
                return Long.class;
            } else if (short.class == type) {
                return Short.class;
            } else if (byte.class == type) {
                return Byte.class;
            } else if (double.class == type) {
                return Double.class;
            } else if (float.class == type) {
                return Float.class;
            } else if (char.class == type) {
                return Character.class;
            } else if (void.class == type) {
                return Void.class;
            }
        }
        return type;
    }

    public Reflector(Object obj) {
        this.obj = obj;
        this.isClass = false;
    }

    public Reflector(Class<?> cls) {
        this.obj = cls;
        this.isClass = true;
    }

    /**
     * 获取封装的反射对象
     *
     * @param <T> 对象类型
     * @return 封装的反射对象
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) this.obj;
    }

    /**
     * 获得字段对象
     *
     * @param name 字段名称
     * @param <T>  对象类型
     * @return 字段对象
     */
    public <T> T get(String name) {
        return field(name).get();
    }

    /**
     * 设置字段值
     *
     * @param name  字段名称
     * @param value 字段值对象
     * @return Reflector
     * @throws ReflectException 反射异常
     */
    public Reflector set(String name, Object value) throws ReflectException {
        try {
            Field field = field0(name);
            accessible(field);
            field.set(this.obj, unwrap(value));
            return this;
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 通过字段名称获取字段对象
     *
     * @param name 字段名称
     * @return 包含了字段对象的Reflector
     * @throws ReflectException 反射异常
     */
    public Reflector field(String name) throws ReflectException {
        try {
            Field field = field0(name);
            return with(field.get(this.obj));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 通过字段名称获取字段
     *
     * @param name 字段名称
     * @return 字段
     * @throws ReflectException 反射异常
     */
    public Field field0(String name) throws ReflectException {
        Class<?> type = type();

        try {
            return type.getField(name);
        } catch (NoSuchFieldException e) {
            do {
                try {
                    return accessible(type.getDeclaredField(name));
                } catch (NoSuchFieldException ex) {
                    type = type.getSuperclass();
                }
            } while (type != null);

            throw new ReflectException(e);
        }
    }

    /**
     * 获得所有字段
     *
     * @return 所有字段
     */
    public Map<String, Reflector> fields() {
        Map<String, Reflector> result = new HashMap<>();
        Class<?> type = type();
        do {
            for (Field field : type.getDeclaredFields()) {
                // 如果是Class，获取静态字段；如果是对象，获取非静态字段
                if (!isClass ^ Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    if (!result.containsKey(name)) {
                        result.put(name, field(name));
                    }
                }
            }

            type = type.getSuperclass();
        } while (type != null);

        return result;
    }

    /**
     * 获得对象的Class
     *
     * @return 对象的Class
     */
    public Class<?> type() {
        if (isClass) {
            return (Class<?>) this.obj;
        } else {
            return this.obj.getClass();
        }
    }

    /**
     * 调用方法
     *
     * @param name 方法名称
     * @return 如果方法返回类型不是void，则Reflector.obj=返回类型对象，否则返回初始Reflector对象
     * @throws ReflectException 反射异常
     */
    public Reflector call(String name) throws ReflectException {
        return call(name, new Object[0]);
    }


    /**
     * 调用方法
     *
     * @param name 方法名称
     * @param args 方法参数
     * @return 如果方法返回类型不是void，则Reflector.obj=返回类型对象，否则返回初始Reflector对象
     * @throws ReflectException 反射异常
     */
    public Reflector call(String name, Object... args) throws ReflectException {
        Class<?>[] types = getTypes(args);

        try {
            Method method = exactMethod(name, types);
            return with(this.obj, method, args);
        } catch (NoSuchMethodException e) {
            try {
                Method method = similarMethod(name, types);
                return with(this.obj, method, args);
            } catch (NoSuchMethodException ex) {
                throw new ReflectException(ex);
            }
        }
    }

    /**
     * 通过方法名称和参数类型获取Method（包括所有的基类）
     *
     * @param name  方法名称
     * @param types 方法参数类型
     * @return Method
     * @throws NoSuchMethodException 方法没有找到异常
     */
    public Method exactMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();

        try {
            return type.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            do {
                try {
                    return type.getDeclaredMethod(name, types);
                } catch (NoSuchMethodException ex) {
                    type = type.getSuperclass();
                }
            } while (type != null);

            throw e;
        }
    }

    /**
     * 匹配相似的方法
     *
     * @param name  方法名称
     * @param types 方法参数类型
     * @return Method
     * @throws NoSuchMethodException 方法没有找到异常
     */
    public Method similarMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();

        for (Method method : type.getMethods()) {
            if (isSimilarSignature(method, name, types)) {
                return method;
            }
        }

        do {
            for (Method method : type.getDeclaredMethods()) {
                if (isSimilarSignature(method, name, types)) {
                    return method;
                }
            }

            type = type.getSuperclass();
        } while (type != null);

        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types) + " could be found with type " + type() + ".");
    }

    /**
     * 判断方法签名是否相似
     *
     * @param possiblyMatchingMethod 可能匹配的方法
     * @param desiredMethodName      想要匹配的方法名称
     * @param desiredParamTypes      想要匹配的方法参数
     * @return 方法签名是否相似
     */
    private boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName, Class<?>[] desiredParamTypes) {
        // 判断方法是否名称相同，并且参数类型匹配成功
        return possiblyMatchingMethod.getName().equals(desiredMethodName) && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }

    /**
     * 参数类型匹配
     *
     * @param declaredTypes 已经声明的参数类型
     * @param actualTypes   实际的参数类型
     * @return 是否匹配成功
     */
    private boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] == NULL.class) {
                    continue;
                }

                // 判断真实类型是否相同
                if (realType(declaredTypes[i]).isAssignableFrom(realType(actualTypes[i]))) {
                    continue;
                }

                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 通过无参构造函数创建对象
     *
     * @return 对象实例
     */
    public Reflector create() {
        return create(new Object[0]);
    }

    /**
     * 能过有参构造函数创建对象
     *
     * @param args 参数数组
     * @return 对象实例
     */
    public Reflector create(Object... args) {
        Class<?> type = type();
        Class<?>[] argsType = getTypes(args);

        try {
            Constructor<?> constructor = type.getDeclaredConstructor(argsType);
            return with(constructor, args);
        } catch (NoSuchMethodException e) {
            for (Constructor<?> constructor : type.getDeclaredConstructors()) {
                if (match(constructor.getParameterTypes(), argsType)) {
                    return with(constructor, args);
                }
            }

            throw new ReflectException(e);
        }
    }

    /**
     * 动态代理
     *
     * @param proxyType 被代理的Class
     * @param <P>       被代理的类型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <P> P proxy(Class<P> proxyType) {
        final boolean isMap = this.obj instanceof Map;
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                try {
                    return with(Reflector.this.obj).call(name, args);
                } catch (ReflectException e) {
                    if (isMap) {
                        Map<String, Object> map = (Map<String, Object>) Reflector.this.obj;
                        int length = args == null ? 0 : args.length;

                        if (length == 0 && name.startsWith("get")) {
                            return map.get(toLowerCaseFirstOne(name.substring(3)));
                        } else if (length == 0 && name.startsWith("is")) {
                            return map.get(toLowerCaseFirstOne(name.substring(2)));
                        } else if (length == 1 && name.startsWith("set")) {
                            map.put(toLowerCaseFirstOne(name.substring(3)), args[0]);
                            return null;
                        }
                    }

                    throw e;
                }
            }
        };
        return (P) Proxy.newProxyInstance(proxyType.getClassLoader(), new Class[]{proxyType}, handler);
    }

    /**
     * 将字符串的第一个字符转换成小写
     *
     * @param str 待转换字符串
     * @return 转换完成后的字符串
     */
    private String toLowerCaseFirstOne(String str) {
        int length = str.length();

        if (length == 0 || Character.isLowerCase(str.charAt(0))) {
            return str;
        } else if (length == 1) {
            return str.toLowerCase();
        } else {
            return Character.toLowerCase(str.charAt(0)) +
                    str.substring(1);
        }
    }

    @Override
    public int hashCode() {
        return this.obj.hashCode();
    }

    @Override
    public String toString() {
        return this.obj.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Reflector && this.obj.equals(((Reflector) o).get());
    }

    static class NULL {

    }
}