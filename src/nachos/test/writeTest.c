#include "syscall.h"

int main(){
   printf("If you can read this, your write system call works!\n");
   halt();
   return 0; // not reached
}
