


int *a[10];
int *b[10];
int *p;
int *p2;
int size;

int main(){
	int i;
	int j;


	p = (int*)mcmalloc(4);
	*p = 200;
	print_i(*p);
	p2 = p;
	print_i(*p2);

	a[0] = p;
	print_i(*a[0]);
	b[0] = a[0];
	print_i(*b[0]);
	// STAGE 2
	a[2] = (int*)mcmalloc(200);
	a[2][3] = 13;		// third and fourth
	print_i(a[2][3]);


	print_c('\n');
	print_s((char*)"2D GENERATION\n");

	//a[3] = (int*)mcmalloc(200);
	//a[3] = (int*)mcmalloc(size*sizeof(int));
	//a[3][7] = 200;
		
	size = 10;
	// generate 2d array
	i = 0;
	while (i<size){
		a[i] = (int*)mcmalloc(size*sizeof(int));
		j = 0;
		while (j < size){
			// int k;
			// k = i;
			// k = k*size;
			// k = k + j;
			//a[i][j] = k;
			a[i][j] = i*size + j;
			// print_s((char*)"k == ");
			// print_i(k);
			// print_c('\n');
			// print_s((char*)"a[i][j] == ");
			// print_i(a[i][j]);
			// print_c('\n');
			// print_c('\n');
			j = j + 1;
		}
		// print_s((char*) "Next line\n");
		i = i + 1;
	}


    i = 0;
	while (i<size){
		j = 0;
		while (j < size){
			print_i(a[i][j]);
			print_c(',');
			j = j + 1;
		}
		print_c('\n');
		i = i + 1;
	}

	// i = size - 1;
	// while (i >= 0){
	// 	j = size - 1;
	// 	while (j >= 0){

	// 		print_i(a[i][j]);
	// 		print_c(',');

	// 		j = j - 1;
	// 	}
		
	// 	print_c('\n');
	// 	i = i - 1;
	// }


	return 0;
}