package nachos.userprog;

import java.io.EOFException;
import nachos.machine.*;
import nachos.threads.*;


/**
 * Encapsulates the state of a user process that is not contained in its
 * user thread (or threads). This includes its address translation state, a
 * file table, and information about the program being executed.
 *
 * <p>
 * This class is extended by other classes to support additional functionality
 * (such as additional syscalls).
 *
 * @see	nachos.vm.VMProcess
 * @see	nachos.network.NetProcess
 */
public class UserProcess {
    /**
     * Allocate a new process.
     */
    public UserProcess() {
	int numPhysPages = Machine.processor().getNumPhysPages();
	pageTable = new TranslationEntry[numPhysPages];
	for (int i=0; i<numPhysPages; i++)
	    pageTable[i] = new TranslationEntry(i,i, true,false,false,false);
    }
    
    /**
     * Allocate and return a new process of the correct class. The class name
     * is specified by the <tt>nachos.conf</tt> key
     * <tt>Kernel.processClassName</tt>.
     *
     * @return	a new process of the correct class.
     */
    public static UserProcess newUserProcess() {
	return (UserProcess)Lib.constructObject(Machine.getProcessClassName());
    }

    /**
     * Execute the specified program with the specified arguments. Attempts to
     * load the program, and then forks a thread to run it.
     *
     * @param	name	the name of the file containing the executable.
     * @param	args	the arguments to pass to the executable.
     * @return	<tt>true</tt> if the program was successfully executed.
     */
    public boolean execute(String name, String[] args) {
	if (!load(name, args))
	    return false;
	
	new UThread(this).setName(name).fork();

	return true;
    }

    /**
     * Save the state of this process in preparation for a context switch.
     * Called by <tt>UThread.saveState()</tt>.
     */
    public void saveState() {
    }

    /**
     * Restore the state of this process after a context switch. Called by
     * <tt>UThread.restoreState()</tt>.
     */
    public void restoreState() {
	Machine.processor().setPageTable(pageTable);
    }

    /**
     * Read a null-terminated string from this process's virtual memory. Read
     * at most <tt>maxLength + 1</tt> bytes from the specified address, search
     * for the null terminator, and convert it to a <tt>java.lang.String</tt>,
     * without including the null terminator. If no null terminator is found,
     * returns <tt>null</tt>.
     *
     * @param	vaddr	the starting virtual address of the null-terminated
     *			string.
     * @param	maxLength	the maximum number of characters in the string,
     *				not including the null terminator.
     * @return	the string read, or <tt>null</tt> if no null terminator was
     *		found.
     */
    public String readVirtualMemoryString(int vaddr, int maxLength) {
	Lib.assertTrue(maxLength >= 0);

	byte[] bytes = new byte[maxLength+1];

	int bytesRead = readVirtualMemory(vaddr, bytes);

	for (int length=0; length<bytesRead; length++) {
	    if (bytes[length] == 0)
		return new String(bytes, 0, length);
	}

	return null;
    }

    /**
     * Transfer data from this process's virtual memory to all of the specified
     * array. Same as <tt>readVirtualMemory(vaddr, data, 0, data.length)</tt>.
     *
     * @param	vaddr	the first byte of virtual memory to read.
     * @param	data	the array where the data will be stored.
     * @return	the number of bytes successfully transferred.
     */
    public int readVirtualMemory(int vaddr, byte[] data) {
	return readVirtualMemory(vaddr, data, 0, data.length);
    }

    /**
     * Transfer data from this process's virtual memory to the specified array.
     * This method handles address translation details. This method must
     * <i>not</i> destroy the current process if an error occurs, but instead
     * should return the number of bytes successfully copied (or zero if no
     * data could be copied).
     *
     * @param	vaddr	the first byte of virtual memory to read.
     * @param	data	the array where the data will be stored.
     * @param	offset	the first byte to write in the array.
     * @param	length	the number of bytes to transfer from virtual memory to
     *			the array.
     * @return	the number of bytes successfully transferred.
     */
    public int readVirtualMemory(int vaddr, byte[] data, int offset,
				 int length) {
	Lib.assertTrue(offset >= 0 && length >= 0 && offset+length <= data.length);

	byte[] memory = Machine.processor().getMemory();

	// invalid address
	if (vaddr < 0)
	    return 0;

	int totalRead = 0;

	// read data page by pgae
	while (length > 0 && vaddr < numPages * pageSize) {
	    int vpn = Processor.pageFromAddress(vaddr);
	    int pageOffset = Processor.offsetFromAddress(vaddr);

	    // invalid address
	    if (vpn < 0 || vpn >= pageTable.length || pageTable[vpn] == null || !pageTable[vpn].valid) {
		return totalRead;
	    }

	    int ppn = pageTable[vpn].ppn;

	    // physical address
	    int paddr = ppn * pageSize + pageOffset;

	    // Calculate how much to read from this page
	    int bytesInPage = pageSize - pageOffset;
	    int amount = Math.min(length, bytesInPage);

	    // Make sure we don't read past physical memory
	    if (paddr < 0 || paddr >= memory.length || paddr + amount > memory.length) {
		return totalRead;
	    }

	    // Get actual data from RAM
	    System.arraycopy(memory, paddr, data, offset, amount);

	    // Update counters
	    totalRead += amount;
	    vaddr += amount;
	    offset += amount;
	    length -= amount;
	}

	return totalRead;
    }

    /**
     * Transfer all data from the specified array to this process's virtual
     * memory.
     * Same as <tt>writeVirtualMemory(vaddr, data, 0, data.length)</tt>.
     *
     * @param	vaddr	the first byte of virtual memory to write.
     * @param	data	the array containing the data to transfer.
     * @return	the number of bytes successfully transferred.
     */
    public int writeVirtualMemory(int vaddr, byte[] data) {
	return writeVirtualMemory(vaddr, data, 0, data.length);
    }

    /**
     * Transfer data from the specified array to this process's virtual memory.
     * This method handles address translation details. This method must
     * <i>not</i> destroy the current process if an error occurs, but instead
     * should return the number of bytes successfully copied (or zero if no
     * data could be copied).
     *
     * @param	vaddr	the first byte of virtual memory to write.
     * @param	data	the array containing the data to transfer.
     * @param	offset	the first byte to transfer from the array.
     * @param	length	the number of bytes to transfer from the array to
     *			virtual memory.
     * @return	the number of bytes successfully transferred.
     */
    public int writeVirtualMemory(int vaddr, byte[] data, int offset,
				  int length) {
	Lib.assertTrue(offset >= 0 && length >= 0 && offset+length <= data.length);

	byte[] memory = Machine.processor().getMemory();

	if (vaddr < 0)
	    return 0; // invalid vaddr

	int totalWritten = 0;

	// write data page by pgae
	while (length > 0 && vaddr < numPages * pageSize) {
	    int vpn = Processor.pageFromAddress(vaddr);
	    int pageOffset = Processor.offsetFromAddress(vaddr);

	    if (vpn < 0 || vpn >= pageTable.length || pageTable[vpn] == null || !pageTable[vpn].valid) {
		return totalWritten; // Invalid page - stop writing
	    }

	    // Check if page is read-only
	    if (pageTable[vpn].readOnly) {
		return totalWritten;
	    }

	    int ppn = pageTable[vpn].ppn;
	    int paddr = ppn * pageSize + pageOffset;

	    // Calculate how much to write to this page (don't cross page boundary)
	    int bytesInPage = pageSize - pageOffset;
	    int amount = Math.min(length, bytesInPage);

	    // Make sure we don't write past physical memory
	    if (paddr < 0 || paddr >= memory.length || paddr + amount > memory.length) {
		return totalWritten;
	    }

	    // Copy data to RAM
	    System.arraycopy(data, offset, memory, paddr, amount);

	    // Mark page as used and dirty
	    pageTable[vpn].used = true;
	    pageTable[vpn].dirty = true;

	    // Update counters
	    totalWritten += amount;
	    vaddr += amount;
	    offset += amount;
	    length -= amount;
	}

	return totalWritten;
    }

    /**
     * Load the executable with the specified name into this process, and
     * prepare to pass it the specified arguments. Opens the executable, reads
     * its header information, and copies sections and arguments into this
     * process's virtual memory.
     *
     * @param	name	the name of the file containing the executable.
     * @param	args	the arguments to pass to the executable.
     * @return	<tt>true</tt> if the executable was successfully loaded.
     */
    private boolean load(String name, String[] args) {
	Lib.debug(dbgProcess, "UserProcess.load(\"" + name + "\")");
	
	OpenFile executable = ThreadedKernel.fileSystem.open(name, false);
	if (executable == null) {
	    Lib.debug(dbgProcess, "\topen failed");
	    return false;
	}

	try {
	    coff = new Coff(executable);
	}
	catch (EOFException e) {
	    executable.close();
	    Lib.debug(dbgProcess, "\tcoff load failed");
	    return false;
	}

	// make sure the sections are contiguous and start at page 0
	numPages = 0;
	for (int s=0; s<coff.getNumSections(); s++) {
	    CoffSection section = coff.getSection(s);
	    if (section.getFirstVPN() != numPages) {
		coff.close();
		Lib.debug(dbgProcess, "\tfragmented executable");
		return false;
	    }
	    numPages += section.getLength();
	}

	// make sure the argv array will fit in one page
	byte[][] argv = new byte[args.length][];
	int argsSize = 0;
	for (int i=0; i<args.length; i++) {
	    argv[i] = args[i].getBytes();
	    // 4 bytes for argv[] pointer; then string plus one for null byte
	    argsSize += 4 + argv[i].length + 1;
	}
	if (argsSize > pageSize) {
	    coff.close();
	    Lib.debug(dbgProcess, "\targuments too long");
	    return false;
	}

	// program counter initially points at the program entry point
	initialPC = coff.getEntryPoint();	

	// next comes the stack; stack pointer initially points to top of it
	numPages += stackPages;
	initialSP = numPages*pageSize;

	// and finally reserve 1 page for arguments
	numPages++;

	if (!loadSections())
	    return false;

	// store arguments in last page
	int entryOffset = (numPages-1)*pageSize;
	int stringOffset = entryOffset + args.length*4;

	this.argc = args.length;
	this.argv = entryOffset;
	
	for (int i=0; i<argv.length; i++) {
	    byte[] stringOffsetBytes = Lib.bytesFromInt(stringOffset);
	    Lib.assertTrue(writeVirtualMemory(entryOffset,stringOffsetBytes) == 4);
	    entryOffset += 4;
	    Lib.assertTrue(writeVirtualMemory(stringOffset, argv[i]) ==
		       argv[i].length);
	    stringOffset += argv[i].length;
	    Lib.assertTrue(writeVirtualMemory(stringOffset,new byte[] { 0 }) == 1);
	    stringOffset += 1;
	}

	return true;
    }

    /**
     * Allocates memory for this process, and loads the COFF sections into
     * memory. If this returns successfully, the process will definitely be
     * run (this is the last step in process initialization that can fail).
     *
     * @return	<tt>true</tt> if the sections were successfully loaded.
     */
    protected boolean loadSections() {
	if (numPages > Machine.processor().getNumPhysPages()) {
	    coff.close();
	    Lib.debug(dbgProcess, "\tinsufficient physical memory");
	    return false;
	}

	// Note: numPages already includes stack pages and argument page from load()
	// Don't add them again!

	// Allocate frames from the kernel's free frames list
	int[] allocatedFrames = UserKernel.allocateFrames(numPages);
	if (allocatedFrames == null) {
	    coff.close();
	    Lib.debug(dbgProcess, "\tunable to allocate " + numPages + " frames");
	    return false;
	}

	// Create the page table
	pageTable = new TranslationEntry[numPages];

	// print allocated frames
	System.out.println("Process allocated " + numPages + " pages:");
	for (int i = 0; i < numPages; i++) {
	    System.out.println("  Page " + i + " -> Frame " + allocatedFrames[i]);
	}

	// Load sections and create page table entries
	int pageIndex = 0;

	// First, load all the code/data sections
	int sectionPages = 0;
	for (int s = 0; s < coff.getNumSections(); s++) {
	    CoffSection section = coff.getSection(s);
	    sectionPages += section.getLength();

	    Lib.debug(dbgProcess, "\tinitializing " + section.getName()
		      + " section (" + section.getLength() + " pages)");

	    for (int i = 0; i < section.getLength(); i++) {
		int vpn = section.getFirstVPN() + i;
		int ppn = allocatedFrames[pageIndex];

		// Create page table entry
		boolean readOnly = section.isReadOnly();
		pageTable[vpn] = new TranslationEntry(vpn, ppn, true, readOnly, false, false);

		// Load the page into the allocated frame
		section.loadPage(i, ppn);

		pageIndex++;
	    }
	}

	// Create page table entries for stack pages
	// Stack pages come after the code/data sections
	for (int i = 0; i < stackPages; i++) {
	    int vpn = sectionPages + i;
	    int ppn = allocatedFrames[pageIndex];

	    // Stack pages are not read-only
	    pageTable[vpn] = new TranslationEntry(vpn, ppn, true, false, false, false);
	    pageIndex++;
	}

	// Create page table entry for arguments page (last page)
	int argVPN = numPages - 1;
	int argPPN = allocatedFrames[pageIndex];
	pageTable[argVPN] = new TranslationEntry(argVPN, argPPN, true, false, false, false);

	return true;
    }

    /**
     * Release any resources allocated by <tt>loadSections()</tt>.
     */
    protected void unloadSections() {
	// Close the coff file if it's still open
	if (coff != null) {
	    coff.close();
	    coff = null;
	}

	// Release all frames back to the free list
	if (pageTable != null) {
	    for (int i = 0; i < pageTable.length; i++) {
		if (pageTable[i] != null && pageTable[i].valid) {
		    // Release the physical frame back to the kernel
		    UserKernel.releaseFrame(pageTable[i].ppn);
		}
	    }
	    // Destroy the page table
	    pageTable = null;
	}
    }    

    /**
     * Initialize the processor's registers in preparation for running the
     * program loaded into this process. Set the PC register to point at the
     * start function, set the stack pointer register to point at the top of
     * the stack, set the A0 and A1 registers to argc and argv, respectively,
     * and initialize all other registers to 0.
     */
    public void initRegisters() {
	Processor processor = Machine.processor();

	// by default, everything's 0
	for (int i=0; i<processor.numUserRegisters; i++)
	    processor.writeRegister(i, 0);

	// initialize PC and SP according
	processor.writeRegister(Processor.regPC, initialPC);
	processor.writeRegister(Processor.regSP, initialSP);

	// initialize the first two argument registers to argc and argv
	processor.writeRegister(Processor.regA0, argc);
	processor.writeRegister(Processor.regA1, argv);
    }

    /**
     * Handle the halt() system call.
     */
    private int handleHalt() {

	Machine.halt();

	Lib.assertNotReached("Machine.halt() did not halt machine!");
	return 0;
    }

    /**
     * Handle the exit() system call.
     */
    private int handleExit(int status) {
	// Unload sections to free all allocated frames
	unloadSections();

	// If this is the last process, halt the machine
	// For now, we'll just halt the machine after any exit
	Machine.halt();

	// This should never be reached
	return 0;
    }

    /**
     * See the full Javadocs in syscall.h.
     * This version of handleWrite only handles printf!
     */
    private int handleWrite(int fileDescriptor, int bufferAddr, int count){
	OpenFile file = UserKernel.console.openForWriting();
	if (!(bufferAddr >= 0 && count >= 0)) {
	    Lib.debug(dbgProcess, "bufferAddr and count should bigger then zero");
	    return -1;
	}
	byte[] buf = new byte[count];
	int length = readVirtualMemory(bufferAddr, buf, 0, count);
	length = file.write(buf, 0, length);
	return length;
    }

    /**
     * See the full Javadocs in syscall.h.
     * This version only handles reading from the console, not from any files!
     */
    private int handleRead(int fileDescriptor, int buffer, int count) {
	OpenFile file = UserKernel.console.openForReading();
	if (!(buffer >= 0 && count >= 0)) {
	    Lib.debug(dbgProcess, "buffer and count should bigger then zero");
	    return -1;
	}
	byte[] buf = new byte[count];
	int length = file.read(buf, 0, count);
	if (length == -1) {
	    Lib.debug(dbgProcess, "Fail to read from file");
	    return -1;
	}
	length = writeVirtualMemory(buffer, buf, 0, length);
	return length;
    }


    private static final int
        syscallHalt = 0,
	syscallExit = 1,
	syscallExec = 2,
	syscallJoin = 3,
	syscallCreate = 4,
	syscallOpen = 5,
	syscallRead = 6,
	syscallWrite = 7,
	syscallClose = 8,
	syscallUnlink = 9;

    /**
     * Handle a syscall exception. Called by <tt>handleException()</tt>. The
     * <i>syscall</i> argument identifies which syscall the user executed:
     *
     * <table>
     * <tr><td>syscall#</td><td>syscall prototype</td></tr>
     * <tr><td>0</td><td><tt>void halt();</tt></td></tr>
     * <tr><td>1</td><td><tt>void exit(int status);</tt></td></tr>
     * <tr><td>2</td><td><tt>int  exec(char *name, int argc, char **argv);
     * 								</tt></td></tr>
     * <tr><td>3</td><td><tt>int  join(int pid, int *status);</tt></td></tr>
     * <tr><td>4</td><td><tt>int  creat(char *name);</tt></td></tr>
     * <tr><td>5</td><td><tt>int  open(char *name);</tt></td></tr>
     * <tr><td>6</td><td><tt>int  read(int fd, char *buffer, int size);
     *								</tt></td></tr>
     * <tr><td>7</td><td><tt>int  write(int fd, char *buffer, int size);
     *								</tt></td></tr>
     * <tr><td>8</td><td><tt>int  close(int fd);</tt></td></tr>
     * <tr><td>9</td><td><tt>int  unlink(char *name);</tt></td></tr>
     * </table>
     * 
     * @param	syscall	the syscall number.
     * @param	a0	the first syscall argument.
     * @param	a1	the second syscall argument.
     * @param	a2	the third syscall argument.
     * @param	a3	the fourth syscall argument.
     * @return	the value to be returned to the user.
     */
    public int handleSyscall(int syscall, int a0, int a1, int a2, int a3) {
	switch (syscall) {
	case syscallHalt:
	    return handleHalt();

	case syscallExit:
	    return handleExit(a0);

	case syscallRead:
	    return handleRead(a0, a1, a2);

	case syscallWrite:
	    return handleWrite(a0, a1, a2);

	default:
	    Lib.debug(dbgProcess, "Unknown syscall " + syscall);
	    Lib.assertNotReached("Unknown system call!");
	}
	return 0;
    }

    /**
     * Handle a user exception. Called by
     * <tt>UserKernel.exceptionHandler()</tt>. The
     * <i>cause</i> argument identifies which exception occurred; see the
     * <tt>Processor.exceptionZZZ</tt> constants.
     *
     * @param	cause	the user exception that occurred.
     */
    public void handleException(int cause) {
	Processor processor = Machine.processor();

	switch (cause) {
	case Processor.exceptionSyscall:
	    int result = handleSyscall(processor.readRegister(Processor.regV0),
				       processor.readRegister(Processor.regA0),
				       processor.readRegister(Processor.regA1),
				       processor.readRegister(Processor.regA2),
				       processor.readRegister(Processor.regA3)
				       );
	    processor.writeRegister(Processor.regV0, result);
	    processor.advancePC();
	    break;				       
				       
	default:
	    Lib.debug(dbgProcess, "Unexpected exception: " +
		      Processor.exceptionNames[cause]);
	    Lib.assertNotReached("Unexpected exception");
	}
    }

    /** The program being run by this process. */
    protected Coff coff;

    /** This process's page table. */
    protected TranslationEntry[] pageTable;
    /** The number of contiguous pages occupied by the program. */
    protected int numPages;

    /** The number of pages in the program's stack. */
    protected final int stackPages = 8;
    
    private int initialPC, initialSP;
    private int argc, argv;
	
    private static final int pageSize = Processor.pageSize;
    private static final char dbgProcess = 'a';
}
