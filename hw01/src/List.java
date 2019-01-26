public class List {

    private class ListElement {
        String key;
        String value;
        ListElement next;
        ListElement(String k, String v) {
            key = k;
            value = v;
            next = null;
        }
    }

    private ListElement head;

    public List() {
        head = null;
    }

    public void addElement(String key, String value) {
        var newHead = new ListElement(key, value);
        newHead.next = head;
        head = newHead;
    }

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

    public String removeKey(String key) {
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
}