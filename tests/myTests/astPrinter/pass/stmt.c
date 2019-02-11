#include "Test statements"

void main() {
	// BLOCK 1
	
	// Test Expr statement
	{
		x + 2;
	}
	
	// Test Assign statement
	{
		a = x;
	}

	// Test return stmt
	{
		return;
	}
	{
		return ;
	}
	{
		return a;
	}

	// Test while stmt without block
	{
		while (a)a = x;
	}
	// Test while stmt with a block
	{
		while (a == b) {
			int x;
			x = 1;
			a = b - x;
		}
	}

	// Test if stmt without else and without block
	{
		if (a) a = x;
	}

	// Test if stmt without else with block
	{
		if (a == b) {
			int x;
			x = 1;
			a = b - x;
		}
	}

	// Test if stmt with else without block
	{
		if (a) a = x;
		else a = x - 1;
	}

	// Test if stmt with else with a block
	{
		if (a == b) {
			int x;
		} else a = x;
	}
	
	{
		if (a == b) {
			int x;
		}
		else {
			a = x;
		}
	}

	//// Test faulty if 
	//{
	//	if (a)
	//		a = x;
	//	a = x;
	//	else a = 1;
	//}
}