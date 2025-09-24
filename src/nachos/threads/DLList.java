package nachos.threads;  // don't change this. Gradescope needs it.

public class DLList
{
    private DLLElement first;  // pointer to first node
    private DLLElement last;   // pointer to last node
    private int size;          // number of nodes in list

    /**
     * Creates an empty sorted doubly-linked list.
     */ 
    public DLList() {
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
        int newKey = 0;
        KThread.yieldIfShould(0);  
        if (this.first != null) {
            newKey = first.key - 1;
        }
        KThread.yieldIfShould(1);  
        
        DLLElement newElement = new DLLElement(item, newKey);
        
        KThread.yieldIfShould(2);  
        if (this.first == null) {  // Empty list
            this.first = newElement;
            KThread.yieldIfShould(3);  
            this.last = newElement;
        } else {  // Non-empty list
            newElement.next = this.first;
            KThread.yieldIfShould(4);  
            this.first.prev = newElement;
            KThread.yieldIfShould(5); 
            this.first = newElement;
        }
        this.size++;
    }

    /**
     * Removes the head of the list and returns the data item stored in
     * it.  Returns null if no nodes exist.
     *
     * @return the data stored at the head of the list or null if list empty
     */
    public Object removeHead() {
        KThread.yieldIfShould(6);  
        if (this.size() == 0) {
            return null;
        } else {
            Object dataItem = first.data;
            KThread.yieldIfShould(7);  
            this.first = first.next;
            
            KThread.yieldIfShould(8);  
            if (first != null) {
                first.prev = null;
            } else {
                last = null;  
            }
            this.size--;  
            
            return dataItem;
        }
    }

    /**
     * Tests whether the list is empty.
     *
     * @return true iff the list is empty.
     */
    public boolean isEmpty() {
        boolean isEmpty = this.size() == 0;
        return isEmpty;
    }

    /**
     * returns number of items in list
     * @return
     */
    public int size(){
        return this.size;
    }


    /**
     * Inserts item into the list in sorted order according to sortKey.
     */
    public void insert(Object item, Integer sortKey) {
        DLLElement elementToInsert = new DLLElement(item, sortKey);
    
        if (first == null) {
            this.first = elementToInsert;
            this.last = elementToInsert;
            this.size++;
            return;
        }
        
        if (sortKey <= first.key) {
            elementToInsert.next = this.first;
            this.first.prev = elementToInsert;
            this.first = elementToInsert;
            this.size++;
            return;
        }
        
        if (sortKey >= last.key) {
            elementToInsert.prev = this.last;
            this.last.next = elementToInsert;
            this.last = elementToInsert;
            this.size++;
            return;
        }
        
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


    /**
     * returns list as a printable string. A single space should separate each list item,
     * and the entire list should be enclosed in parentheses. Empty list should return "()"
     * @return list elements in order
     */
    public String toString() {
        if (this.size() == 0) {
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

    /**
     * returns list as a printable string, from the last node to the first.
     * String should be formatted just like in toString.
     * @return list elements in backwards order
     */
    public String reverseToString(){
        if (this.size() == 0) {
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
