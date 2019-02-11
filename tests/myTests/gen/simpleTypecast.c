#include "simple typecasting"

struct st {
	int a;
	char b;
};
int a;
int arr[10];
char *bp;

struct st s;
struct st *stp;
int main() {
	int* ip;
	char b;
	char brr[10];
	b = 'b';
	print_c(b); // b;
	print_c('\n'); 


	// TYPECAST : (int)char
	a = (int)b;
	print_i(a); // 98 (ASCII of b)
	print_c('\n');
	
	a = (int)"arr"[0];
	print_i(a); // 97 (ASCII of a)
	print_c('\n');

	print_i((int)"functionCall"[9]); // 97 (ASCII of a)
	print_c('\n');

	print_i((int)"functionCall"[12]); // 0 (ASCII of '\0')
	print_c('\n');

	print_i((int)"Call"[4]); // 0 (ASCII OF '\0')
	print_c('\n');

	print_c('\n');
	print_c('\n');

	// TYPECAST: (ptr) array
	arr[0] = 10;
	arr[1] = 11;

	ip = (int*)arr;
	print_i(*ip); // 10
	print_c('\n');

	*ip = *ip + 4;
	print_i(*ip); // 14
	print_c('\n');

	brr[0] = 'a';
	brr[1] = 'b';

	bp = (char*)brr;
	print_c(*bp); // a
	print_c('\n');

	bp = (char*)"STRING";
	print_c(*bp);	// S
	print_c('\n');

	// POINTER TO POINTER
	// (int*) char*
	print_i(*(int*)bp); // 83 (ASCII OF S)
	print_c('\n');

	// (char*) int*
	arr[0] = 84; /* ASCII code for T*/
	ip = (int*)arr;
	print_c(*(char*)ip);
	print_c('\n');

	(char*)stp;
	(int*)stp;
	(struct st*)ip;
	(struct st*)bp;

	return 0;
}