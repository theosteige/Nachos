/* search_for_e
 *
 * User gives a string.  Prints first index where 'e' is found in
 * the string, or -1 if 'e' is not present.
 */

#include "stdlib.h"

#define BUFFERSIZE	64
#define LETTERSIZE      1

#define MAXARGSIZE	16
#define MAXARGS		16


int main(int argc, char *argv[]) {
    char target[BUFFERSIZE];
    printf("%s", "word to search: ");
    readline(target, BUFFERSIZE);

    int i=0;
    while (i < strlen(target) && target[i] != 'e') {
       i++;
    }

    if (i < strlen(target)){
       printf("found at index %d\n", i);
       return i;
    }
    else {
       printf("not found: %d\n", -1);
       return -1;
    }
}
