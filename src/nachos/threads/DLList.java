package nachos.threads;  // don't change this. Gradescope needs it.

public class DLList
{
    private DLLElement first;  // pointer to first node
    private DLLElement last;   // pointer to last node
    private int size;          // number of nodes in list
    private Lock lock;
    private Condition listEmpty;

    /**
     * Creates an empty sorted doubly-linked list.
     */ 
    public DLList() {
        lock = new Lock();
        listEmpty = new Condition(lock);
        this.size = 0;
        this.first = null;  // pointer to first node
        this.last = null;
    }

    /**
     * Add item to the head of the list, setting the key for the new
     * head element to min_key - 1, where min_key is the smallest key
     * in the list (which should be located in the first node).
     * If no nodes exist yet, the key will be 0.
     */
    public void prepend(Object item) {
        lock.acquire();
        int newKey = 0;
        if (this.first != null) {
            newKey = first.key - 1;
        }
        
        DLLElement newElement = new DLLElement(item, newKey);
        
        if (this.first == null) {  // Empty list
            this.first = newElement;
            this.last = newElement;
            listEmpty.wake();
        } else {  // Non-empty list
            newElement.next = this.first;
            this.first.prev = newElement;
            this.first = newElement;
        }
        this.size++;
        lock.release();
    }

    /**
     * Removes the head of the list and returns the data item stored in
     * it.  Returns null if no nodes exist.
     *
     * @return the data stored at the head of the list or null if list empty
     */
    public Object removeHead() {
        lock.acquire();
        while (this.size == 0) {
            listEmpty.sleep();
        } // else {
        Object dataItem = first.data;
        this.first = first.next;
        
        if (first != null) {
            first.prev = null;
        } else {
            this.last = null;
        }
        this.size--;  
        lock.release();
        
        return dataItem;
    }

    /**
     * Tests whether the list is empty.
     *
     * @return true iff the list is empty.
     */
    public boolean isEmpty() {
        lock.acquire();
        boolean isEmpty = this.size == 0;
        lock.release();
        return isEmpty;
    }

    /**
     * returns number of items in list
     * @return
     */
    public int size(){
        lock.acquire();
        int howBig = this.size;
        lock.release();
        return howBig;
    }


    /**
     * Inserts item into the list in sorted order according to sortKey.
     */
    public void insert(Object item, Integer sortKey) {
        lock.acquire();
        DLLElement elementToInsert = new DLLElement(item, sortKey);
    
        if (first == null) { // empty list
            this.first = elementToInsert;
            this.last = elementToInsert;
            this.size++;
            listEmpty.wake();
        } else if (sortKey <= first.key) { // insert at head
            elementToInsert.next = this.first;
            this.first.prev = elementToInsert;
            this.first = elementToInsert;
            this.size++;
        } else if (sortKey >= last.key) { // insert at tail
            elementToInsert.prev = this.last;
            this.last.next = elementToInsert;
            this.last = elementToInsert;
            this.size++;
        } else { // insert somewhere in the middle
            DLLElement current = this.first;
            while (current.next != null && current.next.key < sortKey) {
                current = current.next;
            }
            
            elementToInsert.next = current.next;
            elementToInsert.prev = current;
            if (current.next != null) {
                current.next.prev = elementToInsert;
            }
            current.next = elementToInsert;
            this.size++;
        }
        lock.release();
    }


    /**
     * returns list as a printable string. A single space should separate each list item,
     * and the entire list should be enclosed in parentheses. Empty list should return "()"
     * @return list elements in order
     */
    private String toStringUnsafe() {
        if (this.size == 0) {
            return "()";
        } else {
            String toRet = "(";
            DLLElement current = this.first;
            boolean isFirst = true;
            while (current != null) {
                if (!isFirst) {
                    toRet = toRet + " ";
                }
                toRet = toRet + current.toString();
                isFirst = false;
                current = current.next;
            }
            toRet = toRet + ")";
            return toRet;
        }
    }

    public String toString() {
        lock.acquire();
        String toRet = this.toStringUnsafe();
        lock.release();
        return toRet;
    }

    /**
     * returns list as a printable string, from the last node to the first.
     * String should be formatted just like in toString.
     * @return list elements in backwards order
     */
    private String reverseToStringUnsafe(){
        if (this.size == 0) {
            return "()";
        } else {
            String toRet = "(";
            DLLElement current = this.last;
            boolean isFirst = true;
            while (current != null) {
                if (!isFirst) {
                    toRet = toRet + " ";
                }
                toRet = toRet + current.toString();
                isFirst = false;
                current = current.prev;
            }
            toRet = toRet + ")";
            return toRet;
        }
    }

    public String reverseToString() {
        lock.acquire();
        String toRet = this.reverseToStringUnsafe();
        lock.release();
        return toRet;
    }

    /**
     *  inner class for the node
     */
    private class DLLElement
    {
        private DLLElement next; 
        private DLLElement prev;
        private int key;
        private Object data;

        /**
         * Node constructor
         * @param item data item to store
         * @param sortKey unique integer ID
         */
        public DLLElement(Object item, int sortKey)
        {
        	key = sortKey;
        	data = item;
        	next = null;
        	prev = null;
        }

        /**
         * returns node contents as a printable string
         * @return string of form [<key>,<data>] such as [3,"ham"]
         */
        public String toString(){
            return "[" + key + "," + data + "]";
        }
    }
}
