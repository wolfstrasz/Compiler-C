#include "Testing generation"

int a;
char c;

void f() {
	int g;
}
int main (){
	int b;
	char d;
	a = 10;
	b = 100;
	c = 'c';
	d = 'd';
	print_i(a); // 10
	print_i(b); // 100
	print_c(c); // c
	print_c(d); // d
	f();
	print_c('\n');
	b = a;
	c = d;
	print_i(b);	// 10
	print_c(c); // d

	print_c('\n');
	f();
	print_i(a + b);	// 20

	print_i(sizeof(int)); // 4

    return 1;
}