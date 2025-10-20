package nachos.threads;

public class BoundedBuffer {

    private char[] buffer;
    private int maxSize;
    private int size;
    private int first;
    private int last;
    private Lock lock;
    private Condition bufferEmpty;
    private Condition bufferFull;

    public BoundedBuffer(int maxsize) {
        this.size = 0;
        this.first = 0;
        this.last = 0;
        this.maxSize = maxsize;
        this.buffer = new char[maxSize];
        lock = new Lock();
        bufferEmpty = new Condition(lock);
        bufferFull = new Condition(lock);
    }
    // Read a character from the buffer, blocking until there is a char
    // in the buffer to satisfy the request. Return the char read.
    public char read() {
        lock.acquire();
        // wait while buffer empty
        while (this.size == 0) {
            bufferEmpty.sleep();
        }

        // remove from head
        char c = this.buffer[first];
        first = (first + 1) % maxSize;
        this.size--;

        // wake a writer waiting for space
        bufferFull.wake();
        lock.release();

        return c;
    }
    // Write the given character c into the buffer, blocking until
    // enough space is available to satisfy the request.
    public void write(char c) {
        lock.acquire();
        // wait while buffer full
        while (this.size == this.maxSize) {
            bufferFull.sleep();
        }

        // insert at tail
        buffer[last] = c;
        last = (last + 1) % maxSize;
        this.size++;

        // wake a reader waiting for data
        bufferEmpty.wake();
        lock.release();
    }
    // Prints the contents of the buffer; for debugging only
    public void print() {
        lock.acquire();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < this.size; i++) {
            if (i > 0) sb.append(", ");
            int idx = (first + i) % maxSize;
            sb.append(buffer[idx]);
        }
        sb.append("]");
        System.out.println(sb.toString());
        lock.release();
    }
}
