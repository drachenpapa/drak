package de.drachenpapa.drak;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Minimal reflection utility for tests – replaces Spring's ReflectionTestUtils
 * to avoid the spring-test dependency in a standalone Swing application.
 */
public final class TestReflectionUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED = Map.of(
        boolean.class, Boolean.class,
        byte.class, Byte.class,
        char.class, Character.class,
        double.class, Double.class,
        float.class, Float.class,
        int.class, Integer.class,
        long.class, Long.class,
        short.class, Short.class
    );

    private TestReflectionUtils() {
    }

    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set field '" + fieldName + "'", e);
        }
    }

    public static Object getField(Object target, String fieldName) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot get field '" + fieldName + "'", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object target, String methodName, Object... args) {
        try {
            Method method = findMethod(target.getClass(), methodName, args);
            method.setAccessible(true);
            return (T) method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) throw re;
            throw new RuntimeException("Method '" + methodName + "' threw an exception", cause);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot invoke method '" + methodName + "'", e);
        }
    }

    private static Field findField(Class<?> clazz, String name) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        assert clazz != null;
        throw new RuntimeException("Field not found: " + name + " in " + clazz.getName());
    }

    private static Method findMethod(Class<?> clazz, String name, Object[] args) {
        Class<?> current = clazz;
        while (current != null) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getName().equals(name) && method.getParameterCount() == args.length
                    && typesCompatible(method.getParameterTypes(), args)) {
                    return method;
                }
            }
            current = current.getSuperclass();
        }
        assert clazz != null;
        throw new RuntimeException("Method not found: " + name + " with " + args.length + " arg(s) in " + clazz.getName());
    }

    private static boolean typesCompatible(Class<?>[] paramTypes, Object[] args) {
        for (int i = 0; i < paramTypes.length; i++) {
            if (args[i] == null) continue;
            Class<?> expected = paramTypes[i].isPrimitive()
                ? PRIMITIVE_TO_BOXED.getOrDefault(paramTypes[i], paramTypes[i])
                : paramTypes[i];
            if (!expected.isAssignableFrom(args[i].getClass())) {
                return false;
            }
        }
        return true;
    }
}
