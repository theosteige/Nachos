/* sort_large.c 
 *    Test program to sort a large number of integers.
 *
 *    With 64 frames, this should just fit.
 */

#include "syscall.h"

#define SORTSIZE	256
#define SORTSHIFT	5

int array[SORTSIZE<<SORTSHIFT];

#define	A(i)	(array[(i)<<SORTSHIFT])

void swap(int* x, int* y)
{
  int temp = *x;
  *x = *y;
  *y = temp;
}

int
main()
{
  int i, j;
  
  /* first initialize the array, in reverse sorted order */
  for (i=0; i<SORTSIZE; i++)
    A(i) = (SORTSIZE-1)-i;

  /* then sort! */
  for (i=0; i<SORTSIZE-1; i++) {
    for (j=i; j<SORTSIZE; j++) {
      if (A(i) > A(j))
	swap(&A(i), &A(j));
    }
  }

  /* and last, verify */
  for (i=0; i<SORTSIZE; i++) {
    if (A(i) != i) {
      printf("FAILURE\n");
      return 1;
    }
  }

  /* if successful, return 0 */
  printf("SUCCESS\n");
  return 0;
}
