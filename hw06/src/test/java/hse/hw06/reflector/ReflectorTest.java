package hse.hw06.reflector;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExampleParent {
    interface Interface<T> {
        void a();
        <U> int b(T x, U y);
        <U> String c(T x, U y);
    }
}
class Example<E> extends ExampleParent {
    private class Inner<F> extends Nested {
        Map<E, F> map;
        public class Inner2<G> {
            G value;
        }
        Inner() {
            map.clear();
        }
    }
    private class Nested {
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
    public Example(E value) {
        this.value = value;
    }
    public <E, F> void doNothing(List<E> a, List<F> b) {}
}

class ReflectorTest {
    @Test
    void print() throws IOException {
        var instance = new Example<>("abc");
        Reflector.printStructure(instance.getClass());
    }
}