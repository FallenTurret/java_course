package hse.hw01.hashmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {

    private HashMap hashMap;

    @BeforeEach
    void setUp() {
        hashMap = new HashMap();
    }

    @Test
    void size_changesCorrectlyDuringModifications() {
        assertEquals(0, hashMap.size());
        hashMap.put("a", "1");
        assertEquals(1, hashMap.size());
        hashMap.put("a", "2");
        assertEquals(1, hashMap.size());
        hashMap.remove("a");
        assertEquals(0, hashMap.size());
    }

    @Test
    void contains_correctlyWorksAndThrowsException() {
        assertFalse(hashMap.contains("a"));
        hashMap.put("a", "1");
        assertTrue(hashMap.contains("a"));
        hashMap.put("a", "2");
        assertTrue(hashMap.contains("a"));
        assertFalse(hashMap.contains("b"));
        hashMap.remove("a");
        assertFalse(hashMap.contains("a"));
        assertThrows(IllegalArgumentException.class, () -> {
            hashMap.contains(null);
        });
    }

    @Test
    void get_correctlyWorksAndThrowsException() {
        assertNull(hashMap.get("a"));
        hashMap.put("a", "1");
        assertEquals("1", hashMap.get("a"));
        hashMap.put("a", "2");
        assertEquals("2", hashMap.get("a"));
        hashMap.remove("a");
        assertNull(hashMap.get("a"));
        assertThrows(IllegalArgumentException.class, () -> {
            hashMap.get(null);
        });
    }

    @Test
    void put__correctlyWorksAndThrowsException() {
        assertNull(hashMap.put("a", "1"));
        assertEquals("1", hashMap.put("a", "2"));
        assertThrows(IllegalArgumentException.class, () -> {
            hashMap.put("a", null);
        });
    }

    @Test
    void remove_correctlyWorksAndThrowsException() {
        assertNull(hashMap.remove("a"));
        hashMap.put("a", "1");
        assertEquals("1", hashMap.remove("a"));
        assertThrows(IllegalArgumentException.class, () -> {
            hashMap.remove(null);
        });
    }

    @Test
    void clear_correctlyReturnsToTheStateInBeginning() {
        hashMap.put("a", "1");
        hashMap.clear();
        assertEquals(0, hashMap.size());
        hashMap.put("a", "1");
    }
}