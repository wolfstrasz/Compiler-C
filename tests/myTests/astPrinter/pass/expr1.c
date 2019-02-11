#include "Test precedence exprs"

void main() {
	
	//a || b && c == d != e < f <= g > h >= i + j - k * l / m % (void)-*sizeof(int)func().n[0];
	//a || b && c == d != e < f <= g > h >= i + j - k * l / m % n;

	// Different lvls
	DifferentLevels;
	a || b && c == d < e + f * -g[1];

	// Level 6
	Level6;
	a == b != c;
	a != b == c;

	// Level 5
	Level5;
	a < b <= c;
	a <= b < c;

	// Level 4
	Level4;
	a + b - c;
	a - b + c;

	// Level 3
	Level3;
	a * b / c;
	a / b * c;

	// Level 2
	Level2;
	{
		UnMinus;
		--a;
		-*a;
		*-a;
		-(int)a;
		(int)-a;
		-sizeof(int);

		ValueAt;
		**a;
		*(int)a;
		(int)*a;
		*sizeof(int);

		Typecast;
		(int)(void)a;
		(void)sizeof(int);
	}

	Level1;
	a[0][1];
	a.b.c;
	f();
	
	a[4].c;
	a.c[5];

	f().c;
	f()[0];
	f().a[0];
	f()[0].a;

	//a[0].c.f();
	//a[0].f().c;

}
