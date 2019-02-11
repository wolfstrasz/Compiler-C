#include "simple structs"

struct st1 {
	int a;
	char b;
};

struct st2 {
	char b;
	int a;
};

struct st3 {
	char b;
	struct st1 s1;
	int a;
	struct st2 s2;
};

struct st1 s1;
struct st3 s3;
int main() {
	struct st2 s2;

	s1.a = 4;
	print_i(s1.a);	//4
	print_c('\n');

	s1.b = 'a';
	print_c(s1.b); // a
	print_c('\n');

	s3.s2.b = 'p';
	print_c(s3.s2.b); // p
	print_c('\n');

	s3.b = s3.s2.b;
	print_c(s3.b); // p
	print_c('\n');



	return 0;
}