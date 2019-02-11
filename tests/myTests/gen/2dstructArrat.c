
struct s {
	int a[10];
};

struct s* sparr[10]; 
struct s randoms;
int main (){
	int i;
	int j;
	int size;

	randoms.a[0] = 10;
	randoms.a[1] = 20;
	print_i(randoms.a[0]);
	size = 10;


	sparr[0] = (struct s *)mcmalloc(sizeof(struct s));
	*sparr[0] = randoms;
	print_i((*sparr[0]).a[0]);
	print_c('\n');
	(*sparr[0]).a[0] = 100;
	print_i((*sparr[0]).a[0]);
	print_c('\n');

	print_s((char*)"2D ARRAY WITH STRUCT\n");
	i = 0;
	while (i < size){
		sparr[i] = (struct s *)mcmalloc(sizeof(struct s));
		j= 0;
		while (j < size){
			(*sparr[i]).a[j] = i*size + j;
			j = j + 1;
		}
		i = i + 1;
	}


    i = 0;
	while (i<size){
		j = 0;
		while (j < size){
			print_i((*sparr[i]).a[j]);
			print_c(',');
			j = j + 1;
		}
		print_c('\n');
		i = i + 1;
	}

}