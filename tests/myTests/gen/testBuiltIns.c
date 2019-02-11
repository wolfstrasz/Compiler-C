#include "simple arrays"

int * ip;
char *cp;

int main() {

	int a;
	char b;

	a = 1;
	b = 'S';

	print_i(a);
	print_c(b);
	print_c('\n');

	print_s((char *)"HELLO");
	print_c('\n');

	a = read_i();
	print_i(a);
	print_c('\n');

	b = read_c();
	print_c(b);
	print_c('\n');

	ip = (int*)mcmalloc(sizeof(int));
	print_i(*ip);		// 0
	print_c('\n');
	*ip = 4;
	print_i(*ip);		// 4
	print_c('\n');

	cp = (char*)mcmalloc(sizeof(char));
	print_c(*cp);		
	print_c('\n');	// null
	*cp = 'c';	
	print_c(*cp);		// c
	print_c('\n');


	return 0;
}