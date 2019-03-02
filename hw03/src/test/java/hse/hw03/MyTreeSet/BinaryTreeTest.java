package hse.hw03.MyTreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class BinaryTreeTest {

    private BinaryTree<String> treeSet, treeSet2;

    @BeforeEach
    void setUp() {
        treeSet = new BinaryTree<>(Comparator.comparing(String::length));
        treeSet2 = new BinaryTree<>();
    }

    @Test
    void size_shouldGiveCorrectNumberOfElements() {
        assertEquals(0, treeSet.size());
        treeSet.add("a");
        assertEquals(1, treeSet.size());
        treeSet.remove("a");
        assertEquals(0, treeSet.size());
    }

    @Test
    void isEmpty_shouldCorrectlyCheckForEmptiness() {
        assertTrue(treeSet.isEmpty());
        treeSet.add("a");
        assertFalse(treeSet.isEmpty());
        treeSet.remove("a");
        assertTrue(treeSet.isEmpty());
    }

    @Test
    void contains_shouldCorrectlyCheckIfElementStored() {
        assertFalse(treeSet.contains("a"));
        treeSet.add("a");
        assertTrue(treeSet.contains("a"));
        treeSet.remove("a");
        assertFalse(treeSet.contains("a"));
    }

    @Test
    void iterator_shouldBeGotCorrectlyAndIteratorMethodsShouldWorkCorrectly() {
        treeSet.add("ab");
        treeSet.add("abc");
        treeSet.add("a");
        var iterator = treeSet.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("ab", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("abc", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void toArray_shouldReturnSortedArrayOfStoredElements() {
        treeSet.add("ab");
        treeSet.add("abc");
        treeSet.add("a");
        var expected = new String[]{"a", "ab", "abc"};
        assertArrayEquals(expected, treeSet.toArray());
    }

    @Test
    void toArray_shouldWriteElementsStoredInSetToArrayInSortedOrder() {
        treeSet.add("ab");
        treeSet.add("abc");
        treeSet.add("a");
        var array = new String[3];
        var expected = new String[]{"a", "ab", "abc"};
        assertArrayEquals(expected, treeSet.toArray(array));
    }

    @Test
    void add_shouldAddElementsCorrectly() {
        assertTrue(treeSet.add("a"));
        assertFalse(treeSet.add("a"));
    }

    @Test
    void remove_shouldRemoveElementsCorrectly() {
        assertFalse(treeSet.remove("a"));
        treeSet.add("a");
        assertTrue(treeSet.remove("a"));
    }

    @Test
    void containsAll_shouldCorrectlyCheckIfContainsAllElementsFromCollection() {
        treeSet2.add("a");
        assertFalse(treeSet.containsAll(treeSet2));
        treeSet.add("a");
        assertTrue(treeSet.containsAll(treeSet2));
    }

    @Test
    void addAll_shouldCorrectlyAddElementsFromCollection() {
        treeSet.add("a");
        treeSet2.add("a");
        assertFalse(treeSet.addAll(treeSet2));
        treeSet2.add("ab");
        assertTrue(treeSet.addAll(treeSet2));
    }

    @Test
    void retainAll_shouldCorrectlyLeaveElementsFromCollection() {
        treeSet.add("a");
        treeSet2.add("a");
        assertFalse(treeSet.retainAll(treeSet2));
        treeSet2.remove("a");
        assertTrue(treeSet.retainAll(treeSet2));
    }

    @Test
    void removeAll_shouldCorrectlyRemoveElementsFromCollection() {
        treeSet.add("a");
        assertFalse(treeSet.removeAll(treeSet2));
        treeSet2.add("a");
        assertTrue(treeSet.removeAll(treeSet2));
    }

    @Test
    void clear_shouldCorrectlyReturnToTheStateAfterConstruction() {
        treeSet.add("a");
        treeSet.clear();
        assertTrue(treeSet.isEmpty());
    }

    @Test
    void descendingIterator_shouldIterateInDescendingOrder() {
        treeSet.add("ab");
        treeSet.add("abc");
        treeSet.add("a");
        var iterator = treeSet.descendingIterator();
        assertTrue(iterator.hasNext());
        assertEquals("abc", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("ab", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void descendingSet_shouldReturnSetWIthDescendingOrder() {
        treeSet.add("ab");
        treeSet.add("abc");
        treeSet.add("a");
        var iterator = treeSet.descendingSet().iterator();
        assertTrue(iterator.hasNext());
        assertEquals("abc", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("ab", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void first_shouldReturnLowestElement() {
        treeSet.add("a");
        assertEquals("a", treeSet.first());
    }

    @Test
    void last_shouldReturnHighestElement() {
        treeSet.add("a");
        treeSet.add("ab");
        assertEquals("ab", treeSet.last());
    }

    @Test
    void lower_shouldCorrectlyReturnLowerElement() {
        treeSet.add("a");
        treeSet.add("ab");
        treeSet.add("abc");
        assertNull(treeSet.lower("a"));
        assertEquals("a", treeSet.lower("ab"));
        assertEquals("ab", treeSet.lower("abc"));
    }

    @Test
    void floor_shouldCorrectlyReturnFloorElement() {
        treeSet.add("a");
        treeSet.add("ab");
        treeSet.add("abcd");
        assertEquals("a", treeSet.floor("a"));
        assertEquals("ab", treeSet.floor("ab"));
        assertEquals("ab", treeSet.floor("abc"));
    }

    @Test
    void ceiling_shouldCorrectlyReturnCeilingElement() {
        treeSet.add("a");
        treeSet.add("ab");
        treeSet.add("abcd");
        assertEquals("a", treeSet.ceiling("a"));
        assertEquals("ab", treeSet.ceiling("ab"));
        assertEquals("abcd", treeSet.ceiling("abc"));
    }

    @Test
    void higher_shouldCorrectlyReturnHigherElement() {
        treeSet.add("a");
        treeSet.add("ab");
        treeSet.add("abc");
        assertEquals("ab", treeSet.higher("a"));
        assertEquals("abc", treeSet.higher("ab"));
        assertNull(treeSet.higher("abc"));
    }
}