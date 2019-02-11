/*
* C Program to Display the ATM Transaction
*/

int amount;
int deposit;
int withdraw;
int choice;
int pin;
int k;
char transaction;

void main()
{
	amount = 1000;
	transaction = 'y';
	k = 0;

	while (pin != 1520)
	{
		print_s((char*)"ENTER YOUR SECRET PIN NUMBER:");
		pin = read_i();
		if (pin != 1520)
			print_s((char*)"PLEASE ENTER VALID PASSWORD\n");
	}

	while(k == 0)
	{
		print_c('\n');
		print_s((char*)"********Welcome to ATM Service**************\n");
		print_s((char*)"1. Check Balance\n");
		print_s((char*)"2. Withdraw Cash\n");
		print_s((char*)"3. Deposit Cash\n");
		print_s((char*)"4. Quit\n");
		print_s((char*)"******************?**************************?*\n\n");
		print_s((char*)"Enter your choice: ");
		choice = read_i();


		if (choice == 1) {
			print_s((char*)"\n YOUR BALANCE IN Rs : ");
			print_i(amount);

		} else if (choice == 2) {
			print_s((char*)"\n ENTER THE AMOUNT TO WITHDRAW: ");
			withdraw = read_i();

			if (withdraw % 100 != 0)
			{
				print_s((char*)"\n PLEASE ENTER THE AMOUNT IN MULTIPLES OF 100!");
			}
			else if (withdraw > (amount - 500))
			{
				print_s((char*)"\n INSUFFICENT BALANCE");
			} else
			{
				amount = amount - withdraw;
				print_s((char*)"\n\n PLEASE COLLECT CASH");
				print_s((char*)"\n YOUR CURRENT BALANCE IS: ");
				print_i(amount);
			}


			
		} else if (choice == 3) {
			print_s((char*)"\n ENTER THE AMOUNT TO DEPOSIT: ");
			deposit = read_i();
			amount = amount + deposit;
			print_s((char*)"YOUR BALANCE IS: ");
			print_i(amount);



		} else if (choice == 4) {
			print_s((char*)"\n THANK YOU FOR USING ATM!");


		} else {
			print_s((char*)"\n INVALID CHOICE!");
		}



		print_s((char*)"\n\n\n DO U WISH TO HAVE ANOTHER TRANSCATION?(y/n): \n");
		//fflush(stdin);
		transaction = read_c();
		if (transaction == 'n' || transaction == 'N')
			k = 1;
	}
	print_s((char*)"\n\n THANKS FOR USING OUR ATM SERVICE!");
}