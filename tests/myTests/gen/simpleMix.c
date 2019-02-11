#include "simple mix"

struct st {
	char b[10];
	int a;
};

struct st s[30];
int main() {
	
	s[10].a = 10;
	print_i(s[10].a);	// 10
	print_c('\n');

	s[10].b[3] = "string"[0];
	print_c(s[10].b[3]); // s
	print_c('\n');

	return 0;
}