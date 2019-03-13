package hse.hw06.reflector;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class, which contains only methods to work with classes - print given class, print differences between classes
 */
public class Reflector {

    private static final Set<Type> WRAPPER_TYPES = new HashSet<>(Arrays.asList(
            Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE,
            Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE));
    private static boolean isWrapperType(Type type) {
        return WRAPPER_TYPES.contains(type);
    }

    private static void printModifiers(PrintWriter writer, int mod) {
        if (Modifier.isPublic(mod)) {
            writer.print("public ");
        }
        if (Modifier.isProtected(mod)) {
            writer.print("protected ");
        }
        if (Modifier.isPrivate(mod)) {
            writer.print("private ");
        }
        if (Modifier.isStatic(mod)) {
            writer.print("static ");
        }
    }

    private static void printTypes(Type[] types, PrintWriter writer, boolean names) {
        printTypes(Arrays.stream(types).map(s -> s.getTypeName().replace("$", "."))
                .toArray(String[]::new), writer, names);
    }

    private static void printTypes(String[] types, PrintWriter writer, boolean names) {
        int curNumber = 0;
        for (var type: types) {
            curNumber++;
            writer.print(type);
            if (names) {
                writer.print(" arg");
                writer.print(curNumber);
            }
            if (curNumber < types.length) {
                writer.print(", ");
            }
        }
    }

    private static void printTypeParameters(TypeVariable[] types, PrintWriter writer) {
        if (types.length > 0) {
            writer.print("<");
            printTypes(Arrays.stream(types).map(t -> {
                if (t.getBounds()[0].equals(Object.class)) {
                    return t.getName();
                } else {
                    return t.getName() + " extends " + t.getBounds()[0].getTypeName();
                }
            }).toArray(String[]::new), writer, false);
            writer.print(">");
        }
    }

    private static void printClassDeclaration(Class<?> someClass, PrintWriter writer, String indent) {
        writer.print(indent);
        printModifiers(writer, someClass.getModifiers());
        if (someClass.isInterface()) {
            writer.print("interface ");
        } else {
            writer.print("class ");
        }
        writer.print(someClass.getSimpleName());
        printTypeParameters(Arrays.stream(someClass.getTypeParameters())
                .toArray(TypeVariable[]::new), writer);
        writer.print(" ");
        var superClass = someClass.getSuperclass();
        if (superClass != null && !(Object.class).equals(superClass)) {
            writer.print("extends ");
            writer.print(superClass.getSimpleName());
            writer.print(" ");
        }
        if (someClass.getGenericInterfaces().length > 0) {
            writer.print("implements ");
            printTypes(Arrays.stream(someClass.getGenericInterfaces()).map(s -> s.getTypeName()
                    .replace('$', '.')).toArray(String[]::new), writer, false);
        }
        writer.println("{");
    }

    private static void printExecutable(Executable method, PrintWriter writer, String indent) {
        if (method.isSynthetic()) {
            return;
        }
        writer.print(indent + "    ");
        printModifiers(writer, method.getModifiers());
        printTypeParameters(Arrays.stream(method.getTypeParameters())
                .toArray(TypeVariable[]::new), writer);
        if (method.getTypeParameters().length > 0) {
            writer.print(" ");
        }
        if (method instanceof Method) {
            writer.print(((Method) method).getGenericReturnType().getTypeName());
            writer.print(" ");
            writer.print(method.getName());
        } else {
            writer.print(method.getDeclaringClass().getSimpleName());
        }
        writer.print("(");
        printTypes(method.getGenericParameterTypes(), writer, true);
        writer.print(")");
        if (method.getExceptionTypes().length > 0) {
            writer.print(" throws ");
            printTypes(method.getExceptionTypes(), writer, false);
        }
        if (method.getDeclaringClass().isInterface()) {
            writer.println(";");
            return;
        }
        if (!(method instanceof Method)) {
            writer.println(" {}");
            return;
        }
        writer.println(" {");
        writer.print(indent + "        return");
        var ret = ((Method) method).getGenericReturnType();
        if (ret.equals(Void.TYPE)) {
            writer.println(";");
        } else if (isWrapperType(ret)) {
            writer.println(" 0;");
        } else {
            writer.println(" null;");
        }
        writer.println(indent + "    }");
    }

    private static void printField(Field field, PrintWriter writer, String indent) {
        if (field.isSynthetic()) {
            return;
        }
        writer.print(indent + "    ");
        printModifiers(writer, field.getModifiers());
        writer.print(field.getGenericType().getTypeName());
        writer.println(" " + field.getName() + ";");
    }

    private static void printClass(Class<?> someClass, PrintWriter writer, String indent) {
        printClassDeclaration(someClass, writer, indent);
        for (var classInside: someClass.getDeclaredClasses()) {
            printClass(classInside, writer, indent + "    ");
        }
        for (var field: someClass.getDeclaredFields()) {
            printField(field, writer, indent);
        }
        for (var constructor: someClass.getDeclaredConstructors()) {
            printExecutable(constructor, writer, indent);
        }
        for (var method: someClass.getDeclaredMethods()) {
            printExecutable(method, writer, indent);
        }
        writer.println(indent + "}");
    }

    /**
     * Prints given class if "SomeClass.java" with name "SomeClass".
     * Printed version does not contain field initialization.
     * Instead of method implementation simple return statements are printed.
     * @param someClass class to print
     */
    public static void printStructure(Class<?> someClass) throws IOException {
        var packageName = someClass.getPackageName();
        var file = new File(packageName.replace('.', '/')
                + "/" + someClass.getSimpleName() + ".java");
        file.createNewFile();
        try (var writer = new PrintWriter(file)) {
            if (!packageName.equals("")) {
                writer.println("package " + packageName + ";");
                writer.println();
            }
            printClass(someClass, writer, "");
        }
    }

    private static boolean unique(Class<?> a, Class<?> b) {
        boolean same = true;
        for (var c1: a.getDeclaredConstructors()) {
            boolean exist = false;
            for (var c2: b.getDeclaredConstructors()) {
                if (c1.toGenericString().equals(c2.toGenericString())) {
                    exist = true;
                }
            }
            if (!exist) {
                System.out.println(c1.toGenericString());
                same = false;
            }
        }
        for (var c1: a.getDeclaredMethods()) {
            boolean exist = false;
            for (var c2: b.getDeclaredMethods()) {
                if (c1.toGenericString().equals(c2.toGenericString())) {
                    exist = true;
                }
            }
            if (!exist) {
                System.out.println(c1.toGenericString());
                same = false;
            }
        }
        for (var c1: a.getDeclaredFields()) {
            boolean exist = false;
            for (var c2: b.getDeclaredFields()) {
                if (c1.toGenericString().equals(c2.toGenericString())) {
                    exist = true;
                }
            }
            if (!exist) {
                System.out.println(c1.toGenericString());
                same = false;
            }
        }
        return same;
    }

    /**
     * prints all methods and fields, which somehow(except enclosing class name) differs in two given classes
     * @param a first class
     * @param b second class
     * @return true if there is no difference, otherwise false
     */
    public static boolean diffClasses(Class<?> a, Class<?> b) {
        System.out.println("Unique methods and fields in first class:");
        System.out.println("------------------------------------------");
        boolean same = unique(a, b);
        System.out.println("------------------------------------------");
        System.out.println("Unique methods and fields in second class:");
        System.out.println("------------------------------------------");
        same &= unique(b, a);
        System.out.println("------------------------------------------");
        return same;
    }
}