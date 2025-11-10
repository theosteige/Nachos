/* sort_small.c
*    Test program to sort a small number of integers.
*
*/

#include "syscall.h"

int A[10];   

int
main()
{
   int i, j, tmp;
   int toReturn = 0;

   /* first initialize the array, in reverse sorted order: 9 to 0 */
   for (i = 0; i < 10; i++)
       A[i] = 10 - 1 - i;

   /* then sort! */
   for (i = 0; i < 10; i++)
       for (j = 0; j < 9; j++)
          if (A[j] > A[j + 1]) {       /* out of order -> need to swap ! */
             tmp = A[j];
             A[j] = A[j + 1];
             A[j + 1] = tmp;
          }

   /* and last, verify */
   for (i=0; i<10; i++) {
      printf("%d ", A[i]);
      if (A[i] != i)
        toReturn = 1;
   }
  
   if (toReturn==1){
      printf("\n***Sort Failed***\n");
   }
   else if (toReturn==0){
      printf("\nSort Succeeded!\n");
   }

   /* if successful, return 0; else return 1 */
   return toReturn;
}

