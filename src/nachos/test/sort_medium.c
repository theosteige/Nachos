/* sort_medium.c
*    Test program to sort a medium-sized number of integers.
*
*/

#include "syscall.h"

#define MAX  1000

int A[MAX];

int
main()
{
   int i, j, tmp;

   /* first initialize the array, in reverse sorted order: MAX-1 to 0 */
   for (i = 0; i < MAX; i++)
       A[i] = MAX -1 - i;

   /* then sort! */
   for (i = 0; i < MAX; i++)
       for (j = 0; j < MAX-1; j++)
          if (A[j] > A[j + 1]) {       /* out of order -> need to swap ! */
             tmp = A[j];
             A[j] = A[j + 1];
             A[j + 1] = tmp;
          }

   /* and last, verify. Return 1 if sort failed */
   printf("... ");
   for (i=0; i<MAX; i++) {
      if (i>MAX-5){
         printf("%d ", A[i]);
      }
      if (A[i] != i) {
         printf("\n***Sort Failed***\n");
         return 1;
      }
   }

   /* if successful, return 0 */
   printf("\nSort Succeeded!\n");
   return 0;

}

