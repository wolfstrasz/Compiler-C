#include "simple arrays"

int a[5];
char b[5];

int main() {
	int la[5];
	char lb[5];
	
	a[0] = 5;
	print_i(a[0]);  // 5
	print_c('\n');

	b[1] = 'c';
	print_c(b[1]);  // c
	print_c('\n');

	la[3] = 100;
	a[1] = la[3]; 
	print_i(la[3]);
	print_i(a[1]); // 100100
	print_c('\n');

	lb[1] = 'L';
	b[4] = lb[1];
	print_c(lb[1]);
	print_c(b[4]); // LL
	print_c('\n');

	b[3] = "string"[0];
	print_c(b[3]); // s
	print_c('\n');

	"low"[0] = "high"[0];
	b[2] = "low"[0];
	print_c(b[2]); // l (not h)
	print_c('\n');
	return 0;
}