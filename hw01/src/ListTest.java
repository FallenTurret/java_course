import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ListTest {

    private List l;

    @BeforeEach
    void setUp() {
        l = new List();
        l.addElement("1", "a");
        l.addElement("2", "b");
        l.addElement("3", "c");
    }

    @org.junit.jupiter.api.Test
    void getValue() {
        Assert.assertEquals("a", l.getValue("1"));
        Assert.assertEquals("b", l.getValue("2"));
        Assert.assertEquals("c", l.getValue("3"));
    }

    @org.junit.jupiter.api.Test
    void removeKey() {
        Assert.assertEquals("b", l.removeKey("2"));
        Assert.assertEquals("a", l.removeKey("1"));
        Assert.assertEquals(null, l.removeKey("4"));
        Assert.assertEquals("c", l.removeKey("3"));
        var m = new List();
        Assert.assertEquals(null, m.removeKey("a"));
    }
}