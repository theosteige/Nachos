/*
 *  Tests the process stack via recursion.  
 *  User enters base b and exponent e, and this prints b^e
 */

#include "stdlib.h"

#define BUFFERSIZE	16

#define MAXARGSIZE	16
#define MAXARGS		16


int power(int base, int exp) {
    if (exp == 0){
        return 1;
    }
    else {
        return base * power(base, exp-1);
    }
}

int main(int argc, char *argv[]) {
    char base[BUFFERSIZE];
    printf("%s", "base: ");
    readline(base, BUFFERSIZE);

    char exponent[BUFFERSIZE];
    printf("%s", "exponent: ");
    readline(exponent, BUFFERSIZE);

    int b = atoi(base);
    int e = atoi(exponent);
    int answer = power(b,e);
    
    printf("%d^%d = %d\n", b, e, answer);
    return answer;
}
