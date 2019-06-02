package ru.hse.myjunit;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import ru.hse.myjunit.example.*;

import static org.junit.jupiter.api.Assertions.*;

class MyJUnitTest {

    @Test
    void testMyJUnitUsingJarFile() throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        var path = "src/test/java/ru/hse/myjunit/example/*.java";
        var pb = new ProcessBuilder("bash", "-c", "javac -d . " + path);
        pb.start().waitFor();
        pb = new ProcessBuilder("bash", "-c", "jar cvf Classes.jar ru");
        pb.start().waitFor();
        MyJUnit.runAllTests("Classes.jar");
        for (var o: MyJUnit.instancesForTest) {
            if (o.getClass().getName().endsWith("Example")) {
                var c = o.getClass();
                assertEquals(c.getField("acExpected").getInt(o), c.getField("ac").getInt(o));
                assertEquals(c.getField("bcExpected").getInt(o), c.getField("bc").getInt(o));
                assertEquals(c.getField("aExpected").getInt(o), c.getField("a").getInt(o));
                assertEquals(c.getField("bExpected").getInt(o), c.getField("b").getInt(o));
                assertEquals(c.getField("tExpected").getInt(o), c.getField("t").getInt(o));
            }
        }
    }

}