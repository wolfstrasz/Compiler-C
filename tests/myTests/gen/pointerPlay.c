
struct st {
	int a;
	char c;
};

int arr[100];
int *p;


struct st *sp;
struct st *sp2;

int* retIntP() {

	int* pp;
	pp = (int*)mcmalloc(sizeof(int));
	*pp = 2000;
	return pp;
}

int main() {

	// POINTER FROM FUNCTION 
	// ---------------------
	p = retIntP();
	print_i(*p); // 2000
	print_c('\n');

	// POINTER to struct in heap memory
	// --------------------------------
	print_i(sizeof(struct st));
	print_c('\n');
	sp = (struct st *)mcmalloc(sizeof(struct st));

	(*sp).a = 200;
	print_i((*sp).a); // 200
	print_c('\n');

	sp2 = sp;
	print_i((*sp2).a); // 200
	print_c('\n');

	// POINTER to ARRAYS
	// ------------------

	// assign pointer to array
	arr[0] = 10;
	p = (int*)arr;

	// print first array element using array
	print_i(arr[0]);	// 10
	print_c('\n');
	// print first array element using pointer
	print_i(*p);		// 10
	print_c('\n');

	// re-assign value
	arr[0] = 11;
	// print first array element using array
	print_i(arr[0]);	// 11
	print_c('\n');

	// print first array element using pointer
	print_i(*p);		// 11
	print_c('\n');

	// re-assign value of second array element using array
	arr[1] = 20;
	// print second array element using pointer
	print_i(p[1]);		// 20
	print_c('\n');

	// assign to third array element using pointer
	p[2] = 30;
	// print third array element using array
	print_i(arr[2]);	// 30
	print_c('\n');
	// print third array element using pointer
	print_i(p[2]);		// 30
	print_c('\n');
}