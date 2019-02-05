package hse.hw02.trie;

public class Trie {

    public class TrieNode {

        private TrieNode next[];
        private int strings = 0;

        public TrieNode(int alphabet) {
            next = new TrieNode[alphabet];
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
            var index = symbol - ALPHABET;
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
            var index = symbol - ALPHABET;
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
            var index = symbol - ALPHABET;
            if (curNode.next[index].strings == 1) {
                curNode.next[index] = null;
                break;
            }
            curNode = curNode.next[index];
            curNode.strings--;
        }
    }

    public int size() {
        return root.strings;
    }

    public int howManyStartsWithPrefix(String prefix) {
        var curNode = root;
        for (var symbol : prefix.toCharArray()) {
            var index = symbol - ALPHABET;
            if (curNode.next[index] == null) {
                return 0;
            }
            curNode = curNode.next[index];
        }
        return curNode.strings;
    }
}