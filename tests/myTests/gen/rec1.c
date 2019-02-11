

int f(int a) {

	if (a > 0) {
		print_i(a);
		print_c('\n');
		return f(a - 1);
	}
	return -1;
}


int factorial(int i) {

	if (i <= 1) {
		return 1;
	}
	return i * factorial(i - 1);
}


void main() {

	int b;
	b = 10;
	b = f(b);
	print_i(b);
	print_c('\n');
	b = read_i();
	b = factorial(b);
	print_i(b);
}