class List {
    class ListElement {
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
    List() {
        head = null;
    }
    void addElement(String key, String value) {
        var newHead = new ListElement(key, value);
        newHead.next = head;
        head = newHead;
    }
    String getValue(String key) {
        var curElement = head;
        while (curElement != null) {
            if (key.equals(curElement.key)) {
                return curElement.value;
            }
            curElement = curElement.next;
        }
        return null;
    }
    void removeKey(String key) {
        if (key.equals(head.key)) {
            head = head.next;
            return;
        }
        var curElement = head;
        while (curElement.next != null) {
            if (key.equals(curElement.next.key)) {
                curElement.next = curElement.next.next;
                return;
            }
            curElement = curElement.next;
        }
    }
}