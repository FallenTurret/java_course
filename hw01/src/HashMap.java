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
        return (get(key) != null);
    }

    public String get(String key) {
        var hash = getIndex(key);
        return buckets[hash].getValue(key);
    }

    public String put(String key, String value) {
        String oldValue = remove(key);
        var hash = getIndex(key);
        buckets[hash].addElement(key, value);
        return oldValue;
    }

    public String remove(String key) {
        var hash = getIndex(key);
        return buckets[hash].removeKey(key);
    }
}