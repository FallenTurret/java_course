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
import java.util.concurrent.Future;
import java.util.jar.JarFile;

public final class MyJUnit {

    protected static LinkedList<Object> instancesForTest = new LinkedList<>();

    /**
     * Loads test classes from file, path of which is given in first argument
     * @param args first argument must be path to .class test file or to .jar, whih contains some .class test files
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Specify test file");
            return;
        }
        runAllTests(args[0]);
    }

    /**
     * Loads classes from given path and runs methods from those classes with annotation Test
     * @param path path to .class test file or to .jar, whih contains some .class test files
     */
    public static void runAllTests(String path) {
        instancesForTest.clear();

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
        if (c.isAnnotation()) {
            return;
        }

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
        instancesForTest.add(instance);

        System.out.println(c.getName() + ":");
        System.out.println();

        var threadPool = Executors.newCachedThreadPool();
        var tasks = new LinkedList<Future<String>>();
        runBeforeOrAfter(c, instance, BeforeClass.class);
        for (var method: c.getDeclaredMethods()) {
            var annotations = method.getDeclaredAnnotations();

            boolean run = false;
            Object expected = void.class;
            String reason = "";
            for (var a: annotations) {
                if (a instanceof Test) {
                    run = true;
                    reason = ((Test) a).ignore();
                    expected = ((Test) a).expected();
                }
            }
            if (!reason.equals("")) {
                printLine("Test '" + method.getName() + "' disabled: " + reason);
                System.out.println();
                continue;
            }
            if (run) {
                Object finalInstance = instance;
                Object finalExpected = expected;
                var task = new Callable<String>() {
                    @Override
                    public String call() {
                        runBeforeOrAfter(c, finalInstance, Before.class);
                        String result = "Test '" + method.getName() + "' ";
                        var time = System.currentTimeMillis();
                        try {
                            method.setAccessible(true);
                            method.invoke(finalInstance, (Object[]) null);
                            time = System.currentTimeMillis() - time;
                            result += "passed\n\t";
                        } catch (InvocationTargetException e) {
                            var testException = e.getTargetException();
                            time = System.currentTimeMillis() - time;
                            if (testException.getClass().equals(finalExpected)) {
                                result += "passed\n\t";
                            } else {
                                result += "failed:\n\t";
                                var sw = new StringWriter();
                                var pw = new PrintWriter(sw);
                                testException.printStackTrace(pw);
                                result += sw.toString().replace("\n", "\n\t");
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        runBeforeOrAfter(c, finalInstance, After.class);
                        result += "Time: ";
                        result += String.valueOf(time);
                        result += "ms";
                        return result;
                    }
                };
                tasks.add(threadPool.submit(task));
            }
        }
        threadPool.shutdown();
        for (var task: tasks) {
            try {
                printLine(task.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println();
        }
        runBeforeOrAfter(c, instance, AfterClass.class);
    }

    private static void runBeforeOrAfter(Class<?> c, Object instance, Class<?> expected) {
        for (var method: c.getDeclaredMethods()) {
            var annotations = method.getDeclaredAnnotations();
            boolean run = false;
            for (var a: annotations) {
                if (expected.isAssignableFrom(a.getClass())) {
                    run = true;
                }
            }
            if (run) {
                try {
                    method.setAccessible(true);
                    method.invoke(instance, (Object[]) null);
                } catch (InvocationTargetException e) {
                    e.getTargetException().printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void printLine(String s) {
        System.out.println("\t" + s);
    }
}