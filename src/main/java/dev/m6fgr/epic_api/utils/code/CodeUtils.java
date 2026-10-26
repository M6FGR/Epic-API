package dev.m6fgr.epic_api.utils.code;

import org.apache.logging.log4j.util.StackLocatorUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CodeUtils {

    public static <T> T newEmptyInstance(Class<T> instanceCls) {
        return newEmptyInstance(instanceCls, AccessType.PUBLIC);
    }

    public static <T> T newInstance(Class<T> instanceCls, Object... constructorArgs) {
        return newInstance(instanceCls, AccessType.PUBLIC, constructorArgs);
    }

    public static <T> T newInstance(Class<T> instanceCls, AccessType accessType, Object... constructorArgs) {
        Object[] safeArgs = (constructorArgs == null) ? new Object[0] : constructorArgs;

        try {
            Constructor<T> constructor = findMatchingConstructor(instanceCls, accessType, safeArgs);
            constructor.setAccessible(true);
            return constructor.newInstance(safeArgs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + instanceCls.getName() + " with args " + Arrays.toString(safeArgs), e);
        }
    }

    public static <T> T newEmptyInstance(Class<T> instanceCls, AccessType accessType) {
        try {
            Constructor<T> constructor = accessType == AccessType.PUBLIC ? instanceCls.getConstructor() : instanceCls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + instanceCls.getName() + ", " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> findMatchingConstructor(Class<T> instanceCls, AccessType accessType, Object[] args) throws NoSuchMethodException {
        Constructor<?>[] constructors = accessType == AccessType.PUBLIC
                ? instanceCls.getConstructors()
                : instanceCls.getDeclaredConstructors();

        for (Constructor<?> c : constructors) {
            Class<?>[] paramTypes = c.getParameterTypes();
            if (paramTypes.length == args.length && isParamMatch(paramTypes, args)) {
                return (Constructor<T>) c;
            }
        }

        if (accessType == AccessType.PUBLIC) {
            for (Constructor<?> c : instanceCls.getDeclaredConstructors()) {
                Class<?>[] paramTypes = c.getParameterTypes();
                if (paramTypes.length == args.length && isParamMatch(paramTypes, args)) {
                    return (Constructor<T>) c;
                }
            }
        }

        throw new NoSuchMethodException("No matching constructor found for " + instanceCls.getSimpleName());
    }

    private static boolean isParamMatch(Class<?>[] paramTypes, Object[] args) {
        for (int i = 0; i < paramTypes.length; i++) {
            Object arg = args[i];
            Class<?> param = paramTypes[i];

            if (arg == null) {
                if (param.isPrimitive()) return false;
                continue;
            }

            Class<?> argType = arg.getClass();
            if (!param.isAssignableFrom(argType) && !isWrapperOfPrimitive(param, argType)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isWrapperOfPrimitive(Class<?> param, Class<?> argType) {
        if (!param.isPrimitive()) return false;
        return (param == int.class && argType == Integer.class)
                || (param == boolean.class && argType == Boolean.class)
                || (param == double.class && argType == Double.class)
                || (param == float.class && argType == Float.class)
                || (param == long.class && argType == Long.class)
                || (param == byte.class && argType == Byte.class)
                || (param == short.class && argType == Short.class)
                || (param == char.class && argType == Character.class);
    }

    public static <T> void forEach(T[] things, Consumer<T> lambdaForThing) {
        if (things == null || lambdaForThing == null) return;
        for (T thing : things) {
            lambdaForThing.accept(thing);
        }
    }

    public static <T> void forEach(T[] things, Runnable lambdaForThing) {
        if (things == null || lambdaForThing == null) return;
        for (T ignored : things) {
            runTask(lambdaForThing);
        }
    }

    public static <A, T> void forEach(T[] things, A thing, BiConsumer<A, T> lambdaForThing) {
        if (things == null || lambdaForThing == null) return;
        for (T eachThing : things) {
            lambdaForThing.accept(thing, eachThing);
        }
    }

    public static boolean hasAnnotationSuper(Class<?> cls, Class<? extends Annotation> annotationCls) {
        if (cls == null || annotationCls == null) return false;
        if (cls.isAnnotationPresent(annotationCls)) return true;
        Class<?> superClass = cls.getSuperclass();
        return superClass != null && hasAnnotationSuper(superClass, annotationCls);
    }

    public static boolean hasAnnotationSuper(Class<? extends Annotation> annotationCls) {
        return hasAnnotationSuper(getCallerClass(), annotationCls);
    }

    public static Class<?> getSuperClass(Class<?> cls) {
        return (cls != null) ? cls.getSuperclass() : null;
    }

    public static Class<?> getCallerClass() {
        return StackLocatorUtil.getCallerClass(2);
    }

    public static boolean isNotNull(Object something) {
        return something != null;
    }

    public static void runTask(Runnable task) {
        if (task != null) task.run();
    }

    public static Method[] getMethods(Class<?> cls, AccessType access) {
        if (cls == null) return new Method[0];
        try {
            return switch (access) {
                case PUBLIC -> cls.getMethods();
                case DECLARED -> cls.getDeclaredMethods();
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve methods for " + cls.getSimpleName(), e);
        }
    }

    public static Method[] getMethods(Class<?> cls) {
        return getMethods(cls, AccessType.PUBLIC);
    }

    public static Field[] getFields(Class<?> cls, AccessType access) {
        if (cls == null) return new Field[0];
        try {
            return switch (access) {
                case PUBLIC -> cls.getFields();
                case DECLARED -> cls.getDeclaredFields();
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve fields for " + cls.getSimpleName(), e);
        }
    }

    public static Field[] getFields(Class<?> cls) {
        return getFields(cls, AccessType.PUBLIC);
    }

    public static boolean isOverridingMethods(Class<?> cls, Class<?> superCls) {
        if (cls == null || superCls == null || cls.equals(superCls) || !superCls.isAssignableFrom(cls)) {
            return false;
        }

        Method[] declaredMethods = getMethods(cls, AccessType.DECLARED);
        Set<MethodSignature> superSignatures = new HashSet<>();

        Class<?> currentSuper = superCls;
        while (currentSuper != null && !currentSuper.equals(Object.class)) {
            Method[] superMethods = currentSuper.isInterface() ? currentSuper.getMethods() : getMethods(currentSuper, AccessType.DECLARED);
            for (Method m : superMethods) {
                if (isOverridableInSuper(m, cls)) {
                    superSignatures.add(new MethodSignature(m));
                }
            }
            if (superCls.isInterface()) break;
            currentSuper = currentSuper.getSuperclass();
        }

        if (superSignatures.isEmpty()) return false;

        for (Method method : declaredMethods) {
            if (isChildOverrideCandidate(method)) {
                if (superSignatures.contains(new MethodSignature(method))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOverridableInSuper(Method m, Class<?> childCls) {
        int modifiers = m.getModifiers();
        if (Modifier.isPrivate(modifiers) || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
            return false;
        }
        if (!Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers)) {
            return m.getDeclaringClass().getPackageName().equals(childCls.getPackageName());
        }
        return true;
    }

    public static boolean isChildOverrideCandidate(Method m) {
        int mods = m.getModifiers();
        return !Modifier.isStatic(mods) && !Modifier.isPrivate(mods);
    }

    public static boolean isNotAccessible(Method m) {
        int modifiers = m.getModifiers();
        return Modifier.isPrivate(modifiers) || Modifier.isProtected(modifiers);
    }

    public static <R> R construct(Supplier<R> factory) {
        return factory.get();
    }

    public static <A, R> R construct(A arg1, Function<A, R> factory) {
        return factory.apply(arg1);
    }

    public static <A, B, R> R construct(A arg1, B arg2, BiFunction<A, B, R> factory) {
        return factory.apply(arg1, arg2);
    }

    public static <A, B, C, R> R construct(A arg1, B arg2, C arg3, TriFunction<A, B, C, R> factory) {
        return factory.apply(arg1, arg2, arg3);
    }

    private record MethodSignature(String name, Class<?>[] parameterTypes) {
        public MethodSignature(Method method) {
            this(method.getName(), method.getParameterTypes());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MethodSignature other)) return false;
            return Objects.equals(name, other.name) && Arrays.equals(parameterTypes, other.parameterTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, Arrays.hashCode(parameterTypes));
        }
    }

    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    @FunctionalInterface
    public interface QuadConsumer<A, B, C, D> {
        void apply(A a, B b, C c, D d);
    }
}