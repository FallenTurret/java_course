package hse.hw02.trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
    }

    @Test
    void add_worksCorrectlyInBothCases() {
        assertTrue(trie.add("abc"));
        assertTrue(trie.add("abd"));
        assertFalse(trie.add("abc"));
        assertFalse(trie.add("abd"));
    }

    @Test
    void contains_correctlyWorksForDifferentStatesOfTrie() {
        assertFalse(trie.contains("abc"));
        trie.add("abc");
        assertTrue(trie.contains("abc"));
        trie.add("abc");
        assertTrue(trie.contains("abc"));
        trie.add("abcd");
        trie.remove("abc");
        trie.remove("abc");
        assertFalse(trie.contains("abc"));
    }

    @Test
    void remove_worksCorrectlyInBothCases() {
        assertFalse(trie.remove("abc"));
        trie.add("abc");
        trie.add("abc");
        assertTrue(trie.contains("abc"));
        assertTrue(trie.contains("abc"));
        assertFalse(trie.contains("abc"));
    }

    @Test
    void size_givesRealNumberOfStringsWithRepetitions() {
        assertEquals(0, trie.size());
        trie.add("abc");
        assertEquals(1, trie.size());
        trie.add("abc");
        assertEquals(2, trie.size());
        trie.remove("abcd");
        assertEquals(2, trie.size());
        trie.add("abcd");
        assertEquals(3, trie.size());
    }

    @Test
    void howManyStartsWithPrefix_givesRealNumberOfStringsWithRepetitions() {
        assertEquals(0, trie.howManyStartsWithPrefix("ab"));
        trie.add("abc");
        assertEquals(1, trie.howManyStartsWithPrefix("ab"));
        trie.add("abcd");
        assertEquals(2, trie.howManyStartsWithPrefix("ab"));
        trie.add("abg");
        assertEquals(3, trie.howManyStartsWithPrefix("ab"));
        trie.add("a");
        trie.add("ac");
        assertEquals(3, trie.howManyStartsWithPrefix("ab"));
        trie.remove("abc");
        assertEquals(2, trie.howManyStartsWithPrefix("abc"));
    }

    @Test
    void serializeAndDeserialize_workAndSynchronizedBetweenEachOther() throws IOException {
        trie.add("abc");
        trie.add("abcd");
        trie.add("abg");
        trie.add("a");
        trie.add("ac");
        trie.add("a");
        var out = new ByteArrayOutputStream();
        trie.serialize(out);
        var in = new ByteArrayInputStream(out.toByteArray());
        var trie2 = new Trie();
        trie2.deserialize(in);
        assertEquals(trie, trie2);
    }
}