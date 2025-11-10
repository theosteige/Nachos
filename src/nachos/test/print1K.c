/*
 * Prints the integers from 1-1000 in one string.
 * Stress-tests the paging system by forcing printf
 * to read from 4 different pages in memory.
 */

#include "stdio.h"
#include "stdlib.h"

#define MAX_INTEGERS 1000
#define BUFFER_SIZE (MAX_INTEGERS * 5) // Adjusted to handle large string size

// Function to convert an integer to a string
void intToStr(int num, char *str) {
    int i = 0;
    int isNegative = 0;

    // Handle 0 explicitly, because it's a special case
    if (num == 0) {
        str[i++] = '0';
        str[i] = '\0';
        return;
    }

    // Handle negative numbers
    if (num < 0) {
        isNegative = 1;
        num = -num;
    }

    // Process each digit
    while (num != 0) {
        str[i++] = (num % 10) + '0';
        num = num / 10;
    }

    // If the number was negative, append '-'
    if (isNegative) {
        str[i++] = '-';
    }

    str[i] = '\0'; // Append null character at the end

    // Reverse the string since the digits are stored in reverse order
    int start = 0;
    int end = i - 1;
    char temp;
    while (start < end) {
        temp = str[start];
        str[start] = str[end];
        str[end] = temp;
        start++;
        end--;
    }
}

int main() {
    int i;
    char result[BUFFER_SIZE] = "";
    char temp[10]; // Temporary buffer for each integer

    for (i = 1; i <= MAX_INTEGERS; ++i) {
        intToStr(i, temp); // Convert integer to string
        strcat(result, temp); // Concatenate to the result string
        strcat(result, " "); // Add a space
    }

    // Remove the trailing space by setting the last character to null terminator
    result[strlen(result) - 1] = '\0';

    // Print the result
    printf("%s\n", result);

    return 0;
}

