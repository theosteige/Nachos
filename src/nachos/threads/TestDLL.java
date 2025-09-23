
package nachos.threads;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// # Compile the classes
//   javac -cp "lib/junit-platform-console-standalone-1.10.2.jar:src"
//   src/nachos/threads/DLList.java src/nachos/threads/TestDLL.java

//   # Run the tests
//   java -jar lib/junit-platform-console-standalone-1.10.2.jar --class-path
//   src --select-class nachos.threads.TestDLL

public class TestDLL {
    @Test
    void testIsEmpty() {
        DLList myList = new DLList();
        assertTrue(myList.isEmpty(), "Empty list should be empty");
        myList.insert("A", 1);
        assertFalse(myList.isEmpty(), "List with one element should not be empty");
    }

    @Test
    void testInsert() {
        DLList myList = new DLList();
        myList.insert("A", 2);
        myList.insert("B", 1);
        myList.insert("C", 3);
        assertEquals(3, myList.size(), "List should have 3 elements after 3 inserts");
        assertEquals("([1,B] [2,A] [3,C])", myList.toString(), "Insert should keep list sorted by key");
    }

    @Test
    void testToString() {
        DLList myList = new DLList();
        assertEquals("()", myList.toString(), "Empty list string should be ()");
        myList.insert("A", 1);
        myList.insert("B", 2);
        assertEquals("([1,A] [2,B])", myList.toString(), "List string should match expected format");
    }

    @Test
    void testReverseToString() {
        DLList myList = new DLList();
        assertEquals("()", myList.reverseToString(), "Empty list reverse string should be ()");
        myList.insert("A", 1);
        myList.insert("B", 2);
        assertEquals("([2,B] [1,A])", myList.reverseToString(), "Reverse string should match expected format");
    }

    @Test
    void testSize() {
        DLList myList = new DLList();
        assertEquals(0, myList.size(), "Empty list size should be 0");
        myList.insert("A", 1);
        assertEquals(1, myList.size(), "List size should be 1 after one insert");
        myList.insert("B", 2);
        assertEquals(2, myList.size(), "List size should be 2 after two inserts");
        myList.removeHead();
        assertEquals(1, myList.size(), "List size should decrease after removeHead");
    }

    @Test
    void testPrepend() {
        DLList myList = new DLList();
        myList.prepend("A");
        assertEquals(1, myList.size(), "List size should be 1 after prepend");
        myList.prepend("B");
        assertEquals(2, myList.size(), "List size should be 2 after two prepends");
        assertEquals("([ -1,B] [0,A])", myList.toString().replace("[0,A]", "[0,A]").replace("[ -1,B]", "[-1,B]"), "Prepend should add to head with decreasing key");
    }

    @Test
    void testRemoveHead() {
        DLList myList = new DLList();
        assertNull(myList.removeHead(), "removeHead on empty list should return null");
        myList.insert("A", 1);
        myList.insert("B", 2);
        Object removed = myList.removeHead();
        assertEquals("A", removed, "removeHead should return the first element");
        assertEquals(1, myList.size(), "List size should decrease after removeHead");
    }
}
