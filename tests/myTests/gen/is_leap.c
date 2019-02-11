int main()
{
	int year;

	print_s((char*)"Enter a year: ");
	year = read_i();

	if (year % 4 == 0)
	{
		if (year % 100 == 0)
		{
			// year is divisible by 400, hence the year is a leap year
			if (year % 400 == 0)
				print_s((char*)"%d is a leap year.");
			else
				print_s((char*)"%d is not a leap year.");
		}
		else
			print_s((char*)"%d is a leap year.");
	}
	else
		print_s((char*)"%d is not a leap year.");

	return 0;
}