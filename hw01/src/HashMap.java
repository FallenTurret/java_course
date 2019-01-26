import java.util.Arrays;

/**
 * hash table for string keys and values, uses separate chaining method
 */
public class HashMap {
    private final int mod = (int)1e6;
    private List[] buckets;
    private int size;

    /**
     * constructs empty hash table and reserves space for lists
     */
    public HashMap() {
        buckets = new List[mod];
        size = 0;
    }

    private int getIndex(String key) {
        return ((key.hashCode() % mod) + mod) % mod;
    }

    public int size() {
        return size;
    }

    /**
     * checks if given key exists in hash table
     * @param key must not be null
     * @return true if key was found, false otherwise
     */
    public boolean contains(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return (get(key) != null);
    }

    /**
     * searches for given key
     * @param key must not be null
     * @return value of key if key was found, null otherwise
     */
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        var hash = getIndex(key);
        return buckets[hash].getValue(key);
    }

    /**
     * puts new value for new key, if given key already was in the table, replaces old value with given
     * @param key must not be null
     * @param value must not be null
     * @return old value of given key, null if key was not in the table
     */
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = remove(key);
        var hash = getIndex(key);
        buckets[hash].addElement(key, value);
        size++;
        return oldValue;
    }

    /**
     * removes key and its value if they were in the table, otherwise nothing changes
     * @param key must not be null
     * @return removed value if key was in the table, otherwise null
     */
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        var hash = getIndex(key);
        String tmp = buckets[hash].removeKey(key);
        if (tmp != null) {
            size--;
        }
        return tmp;
    }

    /**
     * removes all key and values, goes back to the state after construction
     */
    public void clear() {
        Arrays.fill(buckets, null);
        size = 0;
    }
}