/* find Theo
 *
 * Read a line from the user and print whether the substring "Theo"
 * appears contiguously anywhere in the input.
 */

#include <stdio.h>
#include <string.h>
#include <stdbool.h>

#define BUFFERSIZE 128

/* Returns true if the substring "Theo" appears in s. Case-sensitive. */
static bool containsTheo(const char *s) {
  if (s == NULL) return false;
  size_t len = strlen(s);
  if (len < 4) return false;

  for (size_t i = 0; i + 3 < len; i++) {
    if (s[i] == 'T' && s[i+1] == 'h' && s[i+2] == 'e' && s[i+3] == 'o')
      return true;
  }
  return false;
}

int main(int argc, char *argv[]) {
  char target[BUFFERSIZE];

  printf("string to search: ");
  if (readline(target, BUFFERSIZE) < 0) {
    return 1;
  }

  size_t len = strlen(target);
  if (len > 0 && target[len-1] == '\n')
    target[len-1] = '\0';

  if (containsTheo(target)) {
    printf("Found Theo!\n");
  } else {
    printf("Theo not found.\n");
  }

  return 0;
}

