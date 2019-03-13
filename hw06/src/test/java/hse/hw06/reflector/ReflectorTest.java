package hse.hw06.reflector;

import org.junit.jupiter.api.Test;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class Example<E> {
    interface Interface<T> {
        void a();
        <U> int b(T x, U y);
        <U> Class<? super U> c(T x, U y);
    }
    public class Implementation implements Interface {
        public void a() {
        }
        public Class c(Object x, Object y) {
            return null;
        }
        public int b(Object x, Object y) {
            return 0;
        }
    }
    private class Inner<F> extends Nested {
        Map<E, F> map;
        public class Inner2<G extends E> {
            G value;
        }
        Inner() {
            map.clear();
        }
    }
    private static class Nested<E> {
        E value;
        Nested() {}
        Nested(E value) {
            this.value = value;
        }
    }
    static PrintWriter open(String path) throws FileNotFoundException {
        try (var in = new PrintWriter(path)) {
            return in;
        }
    }
    static int plus(int a, int b) {
        return a + b;
    }
    private E value;
    private List<? super E> s;
    private Example(E value) {
        this.value = value;
    }
    public <F extends E, G extends F> void doNothing(List<G> a, List<F> b) {}
    public Example<String> check() {
        return new Example("qwerty");
    }
}

class ReflectorTest {

    @Test
    void shouldPrintEmptyListOfDifferentMethodsAndFieldsFromExampleAndRenamedAndCompiledVersionOfClass()
            throws IOException, ClassNotFoundException {
        var example = Class.forName("hse.hw06.reflector.Example");
        Reflector.printStructure(example);
        var file = new File[1];
        file[0] = new File("hse/hw06/reflector/Example.java");
        var compiler = ToolProvider.getSystemJavaCompiler();
        var fileManager = compiler.getStandardFileManager(null, null, null);
        var unit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
        compiler.getTask(null, fileManager, null, null, null, unit).call();
        fileManager.close();
        var cls = new File("");
        URL url = cls.toURI().toURL();
        URL[] urls = new URL[]{url};
        var classLoader = new URLClassLoader(urls, null);
        var loadedClass = classLoader.loadClass("hse.hw06.reflector.Example");
        assertNotSame(example, loadedClass);
        assertTrue(Reflector.diffClasses(example, loadedClass));
    }
}