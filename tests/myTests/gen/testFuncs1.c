// Type your code here, or load an example.
struct st {
	int sta;    // 1*4 = 4 (x2 = 8) 
	int stb;    // 1*4 = 4 (x2 = 8)
	int c[200]; // 200*4 = 800 (x2 = 1600)
};
struct st gs;
int fun0(struct st arg1, int arg2) {
	if (arg1.sta == 1)
		return arg2;
	return 10;
}
int f1() {
	int f1a;
	f1a = 1;
	if (1) {
		return 2 + f1a;
	}
	return 1 + f1a;
}

void fun1() {
	struct st useArg1;
	int a;
	a = 10;
	a = fun0(useArg1, a);
}

int f2() {
	return 2;
}

struct st f3() {
	struct st s; // IMAGINARY RETURN ARG
	struct st s1;
	int a;
	int d;
	a = 0;
	d = 0;
	s.sta = 1;
	s.stb = 2;
	s1.sta = 3;
	s1.stb = 4;
	d = f1() + f2();
	{
		int b;
		a = b;
	}
	{
		int c;
		a = c;
	}

	return s;
}
int main() {
	// gs = f3();
	gs = f3();

	return 0;
}