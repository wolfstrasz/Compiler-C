#include "Test simple exprs"

void main() {
	// BLOCK 1
	
	// Simple exprs;
	a || b;

	a && b;
	
	a == b;
	a != b;
	
	a < b;
	a <= b;
	a >= b;
	a > b;
	
	a + b;
	a - b;

	a * b;
	a % b;
	a / b;

	-a;
	*a;
	(void)a;
	(void*)a;
	(char)a;
	(char*)a;
	(int)a;
	(int*)a;
	(struct k)a;
	(struct k*) a;

	a[0];
	a.i;

	((a));
	(a);
	a;
	
}