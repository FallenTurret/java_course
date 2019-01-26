import java.util.Arrays;

public class HashMap {
    private final int mod = (int)1e6;
    private List[] buckets;
    private int size;

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

    public boolean contains(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return (get(key) != null);
    }

    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        var hash = getIndex(key);
        return buckets[hash].getValue(key);
    }

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

    public void clear() {
        Arrays.fill(buckets, null);
        size = 0;
    }
}