#include "simple pointers"


int *igp; // nullpointer

int main() {
	
	int *ilp;	//nullpointer
	ilp = (int*)mcmalloc(sizeof(int)); // assign memory
	igp = (int*)mcmalloc(sizeof(int)); // assign memory

	*igp = 10;
	print_i(*igp); // 10
	print_c('\n');

	*ilp = 11;
	print_i(*ilp); // 11
	print_c('\n');

	*ilp = *igp;
	print_i(*ilp); // 10
	print_c('\n');

	*ilp = 12;
	print_i(*ilp); // 12
	print_c('\n');
	
	ilp = igp;
	print_i(*ilp); // 10
	print_c('\n');

	//print_i(igp); // should print address of igp
	//print_c('\n');
	//print_i(ilp); // should print address of ilp
	//print_c('\n');
	//ilp = igp;
	//print_i(ilp); // should print address of ilp( which is = to address og igp);
	return 0;
}