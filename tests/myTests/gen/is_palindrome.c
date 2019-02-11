int main()
{
	int n;
	int reversedInteger;
	int originalInteger;
	int remainder;

	reversedInteger = 0;

	print_s((char*)"Enter an integer: ");
	n = read_i();

	originalInteger = n;

	// reversed integer is stored in variable 
	while (n != 0)
	{
		remainder = n % 10;
		reversedInteger = reversedInteger * 10 + remainder;
		n = n / 10;
	}

	// palindrome if orignalInteger and reversedInteger are equal
	if (originalInteger == reversedInteger)
		print_s((char*)"%d is a palindrome.\n");
	else
		print_s((char*)"%d is not a palindrome.\n");

	return 0;
}