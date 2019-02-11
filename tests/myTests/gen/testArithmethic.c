
int main() {

	int a;
	int b;
	int res;

	a = 212;
	b = 32;
	////////////////////////////////
	res = a + b;
	print_i(res);
	print_c(',');

	res = a - b;
	print_i(res);
	print_c(',');

	res = b - a;
	print_i(res);
	print_c(',');

	res = a * b;
	print_i(res);
	print_c(',');

	res = a / b;
	print_i(res);
	print_c(',');

	res = a % b;
	print_i(res);
	print_c('\n');

	/////////////////////////////////////////
	res = a == b;
	print_i(res);
	print_c(',');

	res = a != b;
	print_i(res);
	print_c(',');

	res = a == a;
	print_i(res);
	print_c(',');

	res = a != a;
	print_i(res);
	print_c('\n');
	/////////////////////////////////////////
	res = a || b;
	print_i(res);
	print_c(',');

	res = a && b;
	print_i(res);
	print_c(',');

	res = 0 && 0;
	print_i(res);
	print_c(',');

	res = 0 || 0;
	print_i(res);
	print_c('\n');

	////////////////////////////////////////
	res = a || 0;
	print_i(res);
	print_c(',');

	res = 0 || a;
	print_i(res);
	print_c(',');

	res = a && 0;
	print_i(res);
	print_c(',');

	res = 0 && a;
	print_i(res);
	print_c('\n');
	///////////////////////////////////////////
	b = -4;
	a = 20;

	res = a * b;
	print_i(res);
	print_c(',');

	res = b * b;
	print_i(res);
	print_c(',');

	res = a / b;
	print_i(res);
	print_c(',');

	res = b / b;
	print_i(res);
	print_c('\n');
	//////////////////////////////////////////
	a = 20;
	b = -3;

	res = a % b;
	print_i(res);
	print_c(',');

	res = b % a;
	print_i(res);
	print_c('\n');

	//////////////////////////////////////////
	a = 20;
	b = 20;

	res = a < b;
	print_i(res);
	print_c(',');

	res = a <= b;
	print_i(res);
	print_c(',');

	res = a >= b; //( b <= a)
	print_i(res);
	print_c(',');

	res = a > b;  //( b < a)
	print_i(res);
	print_c('\n');

	////////////////////////////////////////////
	a = 15;
	b = 20;

	res = a < b;
	print_i(res);
	print_c(',');

	res = a <= b;
	print_i(res);
	print_c(',');

	res = a >= b;
	print_i(res);
	print_c(',');

	res = a > b;
	print_i(res);
	print_c('\n');



	// 244,180,-180,6784,6,20,
	// 0,1,1,0
	// 1,1,0,0
	// 1,1,0,0
	// -80,16,-5, 1
	// -1,-3 ?? 2, -3
	// 0,1,1,0
	// 1,1,0,0
}