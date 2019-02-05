package hse.hw02.trie;

import hse.hw02.serializable.Serializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

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

    public Trie() {
        root = new TrieNode(ALPHABET);
    }

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

    public int size() {
        return root.strings;
    }

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

    public void serialize(OutputStream out) throws IOException {
        root.serialize(out);
    }

    public void deserialize(InputStream in) throws IOException {
        root.deserialize(in);
    }
}