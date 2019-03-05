package hse.hw06.reflector;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        printTypes(Arrays.stream(types).map(Type::getTypeName).toArray(String[]::new), writer, names);
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

    private static void printTypeParameters(String[] types, PrintWriter writer) {
        if (types.length > 0) {
            writer.print("<");
            printTypes(types, writer, false);
            writer.print("> ");
        }
    }

    private static void printClass(Class<?> someClass, PrintWriter writer, String indent) {
        boolean required = false;
        if (indent.equals("0")) {
            indent = "";
            required = true;
        }
        var superClass = someClass.getSuperclass();
        if (superClass != null && indent.equals("")) {
            printClass(superClass, writer, indent);
            writer.println();
        }
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
                .map(TypeVariable::getName).toArray(String[]::new), writer);
        writer.print(" ");
        if (superClass != null) {
            writer.print("extends ");
            writer.print(superClass.getSimpleName());
            writer.print(" ");
        }
        writer.println("{");
        for (var classInside: someClass.getDeclaredClasses()) {
            printClass(classInside, writer, indent + "    ");
        }
        for (var field: someClass.getDeclaredFields()) {
            writer.print(indent + "    ");
            printModifiers(writer, field.getModifiers());
            writer.print(field.getGenericType().getTypeName());
            writer.println(" " + field.getName() + ";");
        }
        for (var constructor: someClass.getDeclaredConstructors()) {
            writer.print(indent + "    ");
            printModifiers(writer, constructor.getModifiers());
            printTypeParameters(Arrays.stream(constructor.getTypeParameters())
                    .map(TypeVariable::getName).toArray(String[]::new), writer);
            if (required) {
                writer.print("SomeClass");
            } else {
                writer.print(someClass.getSimpleName());
            }
            writer.print("(");
            printTypes(constructor.getGenericParameterTypes(), writer, true);
            writer.print(") ");
            if (constructor.getExceptionTypes().length > 0) {
                writer.print("throws ");
                printTypes(constructor.getExceptionTypes(), writer, false);
            }
            writer.println(" {}");
        }
        for (var method: someClass.getDeclaredMethods()) {
            writer.print(indent + "    ");
            printModifiers(writer, method.getModifiers());
            printTypeParameters(Arrays.stream(method.getTypeParameters())
                    .map(TypeVariable::getName).toArray(String[]::new), writer);
            writer.print(method.getGenericReturnType().getTypeName());
            writer.print(" ");
            writer.print(method.getName());
            writer.print("(");
            printTypes(method.getGenericParameterTypes(), writer, true);
            writer.print(") ");
            if (method.getExceptionTypes().length > 0) {
                writer.print("throws ");
                printTypes(method.getExceptionTypes(), writer, false);
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

    public static void printStructure(Class<?> someClass) throws IOException {
        var file = new File("someClass.java");
        file.createNewFile();
        try (var writer = new PrintWriter(file)) {
            printClass(someClass, writer, "0");
        }
    }

    public static boolean diffClasses(Class<?> a, Class<?> b) {
        return true;
    }
}