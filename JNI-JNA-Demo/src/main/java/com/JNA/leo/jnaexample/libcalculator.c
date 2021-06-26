#include <stdio.h>
#include <string.h>

int sum(int int1, int int2) {
    int x = int1;
    int y = int2;
    int sum = x + y;

    return sum;
}

int multiply(int int1, int int2) {
	int x = int1;
	int y = int2;
    int mult = x * y;

    return mult;
}

const char* sayHello() {
	return "Hello from C!";
}
