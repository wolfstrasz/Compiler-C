#include "test while"

int a[50];
int main() {
	int b;
	b = 0;
	while (b < 50) {
		a[b] = b;
		b = b + 1;
	}
	b = b - 1;
	while (b >= 0) {
		print_i(a[b]);
		print_c('\n');
		b = b - 1;
	}
	return 0;
}