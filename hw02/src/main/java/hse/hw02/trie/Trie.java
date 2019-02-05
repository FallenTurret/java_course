package hse.hw02.trie;

import hse.hw02.serializable.Serializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Trie implementation for strings consisting of small latin letters
 */
public class Trie implements Serializable {

    private class TrieNode implements Serializable {

        private TrieNode[] next;
        private int strings = 0;

        private TrieNode(int alphabet) {
            next = new TrieNode[alphabet];
        }

        public void serialize(OutputStream out) throws IOException {
            byte[] bytes = ByteBuffer.allocate(4).putInt(strings).array();
            out.write(bytes);
            for (var curSon : next) {
                if (curSon == null) {
                    out.write(0);
                } else {
                    out.write(1);
                    curSon.serialize(out);
                }
            }
        }

        public void deserialize(InputStream in) throws IOException {
            byte[] bytes = in.readNBytes(4);
            strings = ByteBuffer.wrap(bytes).getInt();
            for (int i = 0; i < next.length; i++) {
                if (in.read() == 1) {
                    next[i] = new TrieNode(next.length);
                    next[i].deserialize(in);
                }
            }
        }
    }

    private final char START = 'a';
    private final char ALPHABET = 26;
    private TrieNode root;

    /**
     * Constructs empty trie with one root vertex
     */
    public Trie() {
        root = new TrieNode(ALPHABET);
    }

    /**
     * adds new string to trie, increases number of occurrences if it was there
     * @param element any string with latin small letters
     * @return true if unique string was added, otherwise false
     */
    public boolean add(String element) {
        root.strings++;
        var curNode = root;
        var newString = false;
        for (var symbol : element.toCharArray()) {
            var index = symbol - START;
            if (curNode.next[index] == null) {
                curNode.next[index] = new TrieNode(ALPHABET);
                newString = true;
            }
            curNode = curNode.next[index];
            curNode.strings++;
        }
        return newString;
    }

    /**
     * checks for given string in trie
     * @param element any string with latin small letters
     * @return true if trie has given string, otherwise false
     */
    public boolean contains(String element) {
        var curNode = root;
        for (var symbol : element.toCharArray()) {
            var index = symbol - START;
            if (curNode.next[index] == null) {
                return false;
            }
            curNode = curNode.next[index];
        }
        return true;
    }

    /**
     * decreases number of occurrences of given string if trie has it, does nothing otherwise
     * @param element any string with latin small letters
     * @return true if string was removed, otherwise false
     */
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }
        root.strings--;
        var curNode = root;
        for (var symbol : element.toCharArray()) {
            var index = symbol - START;
            if (curNode.next[index].strings == 1) {
                curNode.next[index] = null;
                break;
            }
            curNode = curNode.next[index];
            curNode.strings--;
        }
        return true;
    }

    /**
     * tells total number of words
     * @return number of words with repetitions
     */
    public int size() {
        return root.strings;
    }

    /**
     * tells total number of words with given prefix
     * @param prefix any string with latin small letters
     * @return number of words with given prefix with repetitions
     */
    public int howManyStartsWithPrefix(String prefix) {
        var curNode = root;
        for (var symbol : prefix.toCharArray()) {
            var index = symbol - START;
            if (curNode.next[index] == null) {
                return 0;
            }
            curNode = curNode.next[index];
        }
        return curNode.strings;
    }

    /**
     * writes byte representation of trie to output stream
     * @param out opened output stream
     * @throws IOException
     */
    public void serialize(OutputStream out) throws IOException {
        root.serialize(out);
    }

    /**
     * constructs trie from given byte representation in input stream
     * @param in opened input stream with byte representation in format of serialize method
     * @throws IOException
     */
    public void deserialize(InputStream in) throws IOException {
        root.deserialize(in);
    }
}