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

    private static void printTypes(Type[] types, PrintWriter writer, boolean names, String prefix) {
        printTypes(Arrays.stream(types).map(Type::getTypeName).map(s -> {
            if (s.startsWith(prefix) && (s.length() == prefix.length() || s.charAt(prefix.length()) == '$'
                    || s.charAt(prefix.length()) == '<')) {
                return "SomeClass" + s.substring(prefix.length());
            } else {
                return s;
            }
        }).filter(s-> !s.contains("$")).toArray(String[]::new), writer, names);
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
                if (t.getBounds()[0].equals(Object.class)) return t.getName();
                else return t.getName() + " extends " + t.getBounds()[0].getTypeName();
            }).toArray(String[]::new), writer, false);
            writer.print("> ");
        }
    }

    private static void printClass(Class<?> someClass, PrintWriter writer, String indent, String prefix) {
        boolean required = false;
        if (indent.equals("0")) {
            indent = "";
            required = true;
        }
        var superClass = someClass.getSuperclass();
        writer.print(indent);

        printModifiers(writer, someClass.getModifiers());

        if (someClass.isInterface()) {
            writer.print("interface ");
        } else {
            writer.print("class ");
        }

        if (required) {
            writer.print("SomeClass");
        } else {
            writer.print(someClass.getSimpleName());
        }

        printTypeParameters(Arrays.stream(someClass.getTypeParameters())
                .toArray(TypeVariable[]::new), writer);

        if (superClass != null && !(Object.class).equals(superClass)) {
            writer.print("extends ");
            writer.print(superClass.getSimpleName());
            writer.print(" ");
        }

        writer.println("{");

        for (var classInside: someClass.getDeclaredClasses()) {
            printClass(classInside, writer, indent + "    ", prefix);
        }

        for (var field: someClass.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            writer.print(indent + "    ");
            printModifiers(writer, field.getModifiers());
            writer.print(field.getGenericType().getTypeName());
            writer.println(" " + field.getName() + ";");
        }

        for (var constructor: someClass.getDeclaredConstructors()) {
            if (constructor.isSynthetic()) {
                continue;
            }
            writer.print(indent + "    ");
            printModifiers(writer, constructor.getModifiers());
            printTypeParameters(Arrays.stream(constructor.getTypeParameters())
                    .toArray(TypeVariable[]::new), writer);
            if (required) {
                writer.print("SomeClass");
            } else {
                writer.print(someClass.getSimpleName());
            }
            writer.print("(");
            printTypes(constructor.getGenericParameterTypes(), writer, true, prefix);
            writer.print(")");
            if (constructor.getExceptionTypes().length > 0) {
                writer.print(" throws ");
                printTypes(constructor.getExceptionTypes(), writer, false, prefix);
            }
            if (!someClass.isInterface()) {
                writer.println(" {}");
            } else {
                writer.println(";");
            }
        }

        for (var method: someClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            writer.print(indent + "    ");
            printModifiers(writer, method.getModifiers());
            printTypeParameters(Arrays.stream(method.getTypeParameters())
                    .toArray(TypeVariable[]::new), writer);
            var retType = method.getGenericReturnType().getTypeName();
            if (retType.startsWith(prefix) &&
                    (retType.length() == prefix.length() || retType.charAt(prefix.length()) == '$'
                            || retType.charAt(prefix.length()) == '<')) {
                retType = "SomeClass" + retType.substring(prefix.length());
            }
            writer.print(retType);
            writer.print(" ");
            writer.print(method.getName());
            writer.print("(");
            printTypes(method.getGenericParameterTypes(), writer, true, prefix);
            writer.print(")");
            if (method.getExceptionTypes().length > 0) {
                writer.print(" throws ");
                printTypes(method.getExceptionTypes(), writer, false, prefix);
            }
            if (someClass.isInterface()) {
                writer.println(";");
                continue;
            }
            writer.println(" {");
            writer.print(indent + "        return");
            var ret = method.getGenericReturnType();
            if (ret.equals(Void.TYPE)) {
                writer.println(";");
            } else if (isWrapperType(ret)) {
                writer.println(" 0;");
            } else {
                writer.println(" null;");
            }
            writer.println(indent + "    }");
        }

        writer.println(indent + "}");
    }

    /**
     * Prints given class if "SomeClass.java" with name "SomeClass".
     * Printed version does not contain field initialization.
     * Instead of method implementation simple return statements are printed.
     * @param someClass class to print
     * @throws IOException
     */
    public static void printStructure(Class<?> someClass) throws IOException {
        var file = new File("SomeClass.java");
        file.createNewFile();
        try (var writer = new PrintWriter(file)) {
            printClass(someClass, writer, "0", someClass.getName());
        }
    }

    private static boolean unique(Class<?> a, Class<?> b) {
        boolean same = true;
        for (var c1: a.getDeclaredConstructors()) {
            boolean exist = false;
            for (var c2: b.getDeclaredConstructors()) {
                if (c1.getName().equals(c2.getName()) &&
                        Arrays.equals(c1.getTypeParameters(), c2.getTypeParameters()) &&
                        Arrays.equals(c1.getGenericParameterTypes(), c2.getGenericParameterTypes()) &&
                        Arrays.equals(c1.getGenericExceptionTypes(), c2.getGenericExceptionTypes()) &&
                        c1.getModifiers() == c2.getModifiers()) {
                    exist = true;
                }
            }
            if (!exist) {
                System.out.println(c1.toGenericString());
                same = false;
            }
        }
        for (var c1: a.getMethods()) {
            boolean exist = false;
            for (var c2: b.getMethods()) {
                if (c1.getName().equals(c2.getName()) &&
                        Arrays.equals(c1.getTypeParameters(), c2.getTypeParameters()) &&
                        Arrays.equals(c1.getGenericParameterTypes(), c2.getGenericParameterTypes()) &&
                        Arrays.equals(c1.getGenericExceptionTypes(), c2.getGenericExceptionTypes()) &&
                        c1.getGenericReturnType().equals(c2.getGenericReturnType()) &&
                        c1.getModifiers() == c2.getModifiers()) {
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
                if (c1.getName().equals(c2.getName()) &&
                        c1.getGenericType().equals(c2.getGenericType()) &&
                        c1.getModifiers() == c2.getModifiers()) {
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
        boolean same;
        System.out.println("Unique methods and fields in first class:");
        System.out.println("------------------------------------------");
        same = unique(a, b);
        System.out.println("------------------------------------------");
        System.out.println("Unique methods and fields in second class:");
        System.out.println("------------------------------------------");
        same &= unique(b, a);
        System.out.println("------------------------------------------");
        return same;
    }
}