/*
 *  Tests the read system call.  
 *  User enters a string and it echoes it.
 */

#include "stdlib.h"

#define BUFFERSIZE	16

#define MAXARGSIZE	16
#define MAXARGS		16


int main(int argc, char *argv[]) {
    char something[BUFFERSIZE];
    printf("%s", "Type something: ");
    readline(something, BUFFERSIZE);

    printf("You typed %s\n", something);
    printf("Congrats.  The read system call works.\n");
    halt();
    return 0;  // not reached
}
