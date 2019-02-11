#include "test if"
int a[50];

int f(){
	return 1;
}
int main() {

	int i;
	int c;
	int b;
	b = 0;
	while (b < 50) {
		a[b] = b;
		b = b + 1;
	}
	b = b - 1;
	while (b >= 0) {
		if (b > 24) {
			print_i(a[b]);
			print_c('\n');
		}
		else {
			print_i(a[b + 25]);
			print_c('\n');
		}
		b = b - 1;
	}
	
	if ((0 == 1) && (read_i() == 1)){
		print_c('n');
	}

	return 0;
}