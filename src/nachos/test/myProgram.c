/* find Theo
 *
 * User gives an input.  returns t/f if the word Theo is present 
 * (contiguous) in the string given by the user
 */

#include "stdlib.h"

#define BUFFERSIZE	128
#define LETTERSIZE      1

#define MAXARGSIZE	32
#define MAXARGS		32

int main(int argc, char *argv[]) {
    char target[BUFFERSIZE];
    printf("%s", "string to search: ");
    readline(target, BUFFERSIZE);

    int i=0;
    while (i < strlen(target) && target[i] != 'T') {
       i++;
    }

    if (i < strlen(target) && i + 3 < strlen(target)) {
       if (target[i+1] == 'h'
        && target[i+2] == 'e'
        && target[i+3] == 'o') {
       printf("%s", "Found Theo!\n");
       return true;
      }
        else {
           printf("%s", "Theo not found.\n");
           return false;
        }
    }
}

