package nachos.threads;

import nachos.machine.*;
import java.util.LinkedList;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
	this.conditionLock = conditionLock;
	this.waitQueue = new LinkedList<KThread>();
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     *
     * Implementation using interrupt disabling:
     * 1. Release the lock
     * 2. Disable interrupts for atomicity
     * 3. Add current thread to wait queue
     * 4. Sleep (block) the current thread
     * 5. When woken up, interrupts are re-enabled
     * 6. Reacquire the lock before returning
     */
    public void sleep() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());

	// Release the lock first
	conditionLock.release();

	// Disable interrupts for atomicity
	boolean intStatus = Machine.interrupt().disable();

	// Add the current thread to the wait queue
	waitQueue.add(KThread.currentThread());

	// Block the current thread (this will context switch)
	KThread.sleep();

	// When we wake up, restore interrupt status
	Machine.interrupt().restore(intStatus);

	// Reacquire the lock before returning
	conditionLock.acquire();
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     *
     * Only wakes a thread if there is one waiting in the queue.
     */
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());

	// Disable interrupts for atomicity
	boolean intStatus = Machine.interrupt().disable();

	// Wake up one thread if there is one waiting
	if (!waitQueue.isEmpty()) {
	    KThread thread = waitQueue.removeFirst();
	    thread.ready();  // Put the thread back on the ready queue
	}

	// Restore interrupt status
	Machine.interrupt().restore(intStatus);
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     *
     * Only wakes threads if there are any waiting in the queue.
     */
    public void wakeAll() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());

	// Disable interrupts for atomicity
	boolean intStatus = Machine.interrupt().disable();

	// Wake up all waiting threads
	while (!waitQueue.isEmpty()) {
	    KThread thread = waitQueue.removeFirst();
	    thread.ready();  // Put the thread back on the ready queue
	}

	// Restore interrupt status
	Machine.interrupt().restore(intStatus);
    }

    private Lock conditionLock;
    private LinkedList<KThread> waitQueue;  // Queue of threads waiting on this condition
}
