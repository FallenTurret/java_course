package hse.hw01.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListTest {

    private List list;

    @BeforeEach
    void setUp() {
        list = new List();
        list.addElement("1", "a");
        list.addElement("2", "b");
        list.addElement("3", "c");
    }

    @Test
    void getValue_returnsCorrectValuesByExistingKeys() {
        assertEquals("a", list.getValue("1"));
        assertEquals("b", list.getValue("2"));
        assertEquals("c", list.getValue("3"));
    }

    @Test
    void removeKey_correctlyRemovesFromHeadAndMiddle() {
        assertEquals("b", list.removeKey("2"));
        assertEquals("a", list.removeKey("1"));
        assertNull(list.removeKey("4"));
        assertEquals("c", list.removeKey("3"));
        var list2 = new List();
        assertNull(list2.removeKey("a"));
    }

    @Test
    void getHead_correctlyRemovesHead() {
        String[] head;
        head = list.getHead();
        assertEquals("3", head[0]);
        assertEquals("c", head[1]);
        head = list.getHead();
        assertEquals("2", head[0]);
        assertEquals("b", head[1]);
        head = list.getHead();
        assertEquals("1", head[0]);
        assertEquals("a", head[1]);
        assertNull(list.getHead());
    }
}