package hse.hw01.list;

/**
 * Linked list, which contains pair of strings - key and value
 * Should contain no more than one value for the same key
 */
public class List {

    private class ListElement {
        private String key;
        private String value;
        private ListElement next;
        ListElement(String keyOfElement, String valueOfElement) {
            key = keyOfElement;
            value = valueOfElement;
            next = null;
        }
    }

    private ListElement head;

    /**
     * Constructs empty list
     */
    public List() {
        head = null;
    }

    /**
     * adds new pair of strings to the beginning
     * @param key any string
     * @param value any string
     */
    public void addElement(String key, String value) {
        var newHead = new ListElement(key, value);
        newHead.next = head;
        head = newHead;
    }

    /**
     * searches for value by given key
     * @param key any string
     * @return value if key was found, null otherwise
     */
    public String getValue(String key) {
        ListElement curElement = head;
        while (curElement != null) {
            if (key.equals(curElement.key)) {
                return curElement.value;
            }
            curElement = curElement.next;
        }
        return null;
    }

    /**
     * removes value, paired with given key
     * @param key any string
     * @return removed value if key was found, null otherwise
     */
    public String removeKey(String key) {
        if (head == null) {
            return null;
        }
        if (key.equals(head.key)) {
            String tmp = head.value;
            head = head.next;
            return tmp;
        }
        ListElement curElement = head;
        while (curElement.next != null) {
            if (key.equals(curElement.next.key)) {
                String tmp = curElement.next.value;
                curElement.next = curElement.next.next;
                return tmp;
            }
            curElement = curElement.next;
        }
        return null;
    }

    /**
     * pops head of the list
     * @return array with key and value of first element in list
     */
    public String[] getHead() {
        if (head == null) {
            return null;
        }
        var newHead = head.next;
        var headPair = new String[] {head.key, head.value};
        head = newHead;
        return headPair;
    }
}