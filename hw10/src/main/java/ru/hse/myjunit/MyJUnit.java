package ru.hse.myjunit;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.jar.JarFile;

public class MyJUnit {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Specify test file");
            return;
        }
        runAllTests(args[0]);
    }

    public static void runAllTests(String path) {
        if (path.endsWith(".jar")) {
            try {
                var jar = new JarFile(path);
                var entries = jar.entries();
                URL[] urls = {new URL("jar:file:" + path + "!/")};
                var cl = URLClassLoader.newInstance(urls);
                while (entries.hasMoreElements()) {
                    var entry = entries.nextElement();
                    if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                        continue;
                    }
                    var className = entry.getName().substring(0, entry.getName().length() - 6)
                            .replace('/', '.');
                    try {
                        System.out.println(className + ":");
                        runClassTests(cl.loadClass(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (path.endsWith(".class")) {
            var file  = new File(path);
            try {
                URL[] urls = {file.toURI().toURL()};
                var cl = URLClassLoader.newInstance(urls);
                var className = file.getName().substring(0, file.getName().length() - 6)
                        .replace('/', '.');
                System.out.println(className + ":");
                runClassTests(cl.loadClass(className));
            } catch (MalformedURLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Wrong file format");
        }
    }

    private static void runClassTests(Class<?> c) {
        Object instance = null;
        for (var constructor: c.getDeclaredConstructors()) {
            try {
                instance = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
            }
        }
        if (instance == null) {
            printLine("There is no callable constructor");
            System.out.println();
            return;
        }
        var threadPool = Executors.newCachedThreadPool();
        var tasks = new LinkedList<Callable<String>>();
        runBeforeOrAfter(c, instance, "BeforeClass");
        for (var method: c.getDeclaredMethods()) {
            var annotations = method.getDeclaredAnnotations();
            boolean run = false;
            Object expected = null;
            String reason = null;
            for (var a: annotations) {
                if (a.annotationType().getName().equals("Test")) {
                    for (var annotationMethod: a.annotationType().getDeclaredMethods()) {
                        if (annotationMethod.getName().equals("ignore")) {
                            try {
                                reason = (String) annotationMethod.invoke(instance, (Object[]) null);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                printLine(annotationMethod.getName() + ": error");
                                System.out.println();
                            }
                        } else {
                            run = true;
                            if (annotationMethod.getName().equals("expected")) {
                                try {
                                    expected = annotationMethod.invoke(instance, (Object[]) null);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    printLine(annotationMethod.getName() + ": error");
                                    System.out.println();
                                }
                            }
                        }
                    }
                }
            }
            if (reason != null) {
                printLine("Test + " + method.getName() + " disabled: " + reason);
                System.out.println();
            }
            if (run) {
                Object finalInstance = instance;
                Object finalExpected = expected;
                var task = new Callable<String>() {
                    @Override
                    public String call() {
                        runBeforeOrAfter(c, finalInstance, "Before");
                        String result = "Test " + method.getName() + " ";
                        var time = System.currentTimeMillis();
                        try {
                            method.invoke(finalInstance, (Object[]) null);
                            time = System.currentTimeMillis() - time;
                            result += "passed\n";
                        } catch (Exception e) {
                            time = System.currentTimeMillis() - time;
                            if (e.getClass().equals(finalExpected)) {
                                result += "passed\n";
                            } else {
                                result += "failed:\n";
                                var pw = new PrintWriter(new StringWriter());
                                e.printStackTrace(pw);
                                result += pw.toString();
                                result += "\n";
                            }
                        }
                        runBeforeOrAfter(c, finalInstance, "After");
                        result += "Time: ";
                        result += String.valueOf(time);
                        result += "ms";
                        return result;
                    }
                };
                threadPool.submit(task);
                tasks.add(task);
            }
        }
        for (var task: tasks) {
            try {
                printLine(task.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println();
        }
        runBeforeOrAfter(c, instance, "AfterClass");
    }

    private static void runBeforeOrAfter(Class<?> c, Object instance, String expected) {
        for (var method: c.getDeclaredMethods()) {
            var annotations = method.getDeclaredAnnotations();
            boolean run = false;
            for (var a: annotations) {
                if (a.annotationType().getName().equals(expected)) {
                    run = true;
                }
            }
            if (run) {
                try {
                    method.invoke(instance, (Object[]) null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void printLine(String s) {
        System.out.println("    " + s);
    }
}