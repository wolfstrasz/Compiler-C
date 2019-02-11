

int main()
{
	int n;
	int i;
	int flag;

	print_s((char*)"Enter a positive integer: ");
	n = read_i();

	if (n == 1)
	{
		print_s((char*)"1 is neither a prime nor a composite number.\n");
		return 0;
	}

	i = 2;
	flag = 0;

	while (i <= n / 2) {
		if (n % i == 0) flag = 1;
		i = i + 1;
	}


	if (flag == 0)
		print_s((char*)" is a prime number.\n");
	else
		print_s((char*)" is not a prime number.\n");

	return 0;
}