#include "test assign"

struct st {
	int a;
	char b;
	char str[6];
	int arr[3];

};
struct st s1;

int main() {
struct st s2;
	
	// Assign to s1
	s1.a = 2;
	s1.b = 'B';
	s1.str[0] = 'S';
	s1.str[1] = 'T';
	s1.str[2] = "STRING"[2];
	s1.str[3] = 'I';
	s1.str[4] = 'N';
	s1.str[5] = 'G';
	s1.arr[0] = 10;
	s1.arr[1] = 11;
	s1.arr[2] = 12;

	// Print s1;
	print_i(s1.a);
	print_c(s1.b);
	print_c('\n');
	print_c(s1.str[0]);
	print_c(s1.str[1]);
	print_c(s1.str[2]);
	print_c(s1.str[3]);
	print_c(s1.str[4]);
	print_c(s1.str[5]);
	print_i(s1.arr[0]);
	print_i(s1.arr[1]);
	print_i(s1.arr[2]);
	print_c('\n');
	print_c('\n');

	// Assign to s2;
	s2 = s1;

	// Print s2;

	print_i(s2.a);
	print_c(s2.b);
	print_c('\n');
	print_c(s2.str[0]);
	print_c(s2.str[1]);
	print_c(s2.str[2]);
	print_c(s2.str[3]);
	print_c(s2.str[4]);
	print_c(s2.str[5]);
	print_i(s2.arr[0]);
	print_i(s2.arr[1]);
	print_i(s2.arr[2]);
	return 0;
}