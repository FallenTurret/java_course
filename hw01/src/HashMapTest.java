import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {

    private HashMap h;

    @BeforeEach
    void setUp() {
        h = new HashMap();
    }

    @Test
    void size() {
        assertEquals(0, h.size());
        h.put("a", "1");
        assertEquals(1, h.size());
        h.put("a", "2");
        assertEquals(1, h.size());
        h.remove("a");
        assertEquals(0, h.size());
    }

    @Test
    void contains() {
        assertEquals(false, h.contains("a"));
        h.put("a", "1");
        assertEquals(true, h.contains("a"));
        h.put("a", "2");
        assertEquals(true, h.contains("a"));
        assertEquals(false, h.contains("b"));
        h.remove("a");
        assertEquals(false, h.contains("a"));
        assertThrows(IllegalArgumentException.class, () -> {
            h.contains(null);
        });
    }

    @Test
    void get() {
        assertEquals(null, h.get("a"));
        h.put("a", "1");
        assertEquals("1", h.get("a"));
        h.put("a", "2");
        assertEquals("2", h.get("a"));
        h.remove("a");
        assertEquals(null, h.get("a"));
        assertThrows(IllegalArgumentException.class, () -> {
            h.get(null);
        });
    }

    @Test
    void put() {
        assertEquals(null, h.put("a", "1"));
        assertEquals("1", h.put("a", "2"));
        assertThrows(IllegalArgumentException.class, () -> {
            h.put("a", null);
        });
    }

    @Test
    void remove() {
        assertEquals(null, h.remove("a"));
        h.put("a", "1");
        assertEquals("1", h.remove("a"));
        assertThrows(IllegalArgumentException.class, () -> {
            h.remove(null);
        });
    }

    @Test
    void clear() {
        h.put("a", "1");
        h.clear();
        assertEquals(0, h.size());
    }
}