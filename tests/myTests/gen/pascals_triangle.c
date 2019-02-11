
int main()
{
	int i;
	int j;
	int rows;

	print_s((char*)"Enter number of rows: ");
	rows = read_i();

	i = 1;
	while (i <= rows) {
		j = 1;
		while (j <= i) {
			print_c('*');
			j = j + 1;
		}
		print_c('\n');
		i = i + 1;
	}

	return 0;
}