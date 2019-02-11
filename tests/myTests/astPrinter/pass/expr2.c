
int main() {
	
	// Testing how unary expressions and lower precedence exprs is handles
	MulValuAt;
	a**x;
	a***x;

	TypecastMul;
	(void)a*c;
	c*(void)a;

	SizeofMul;
	sizeof(int)*x;
	x * sizeof(int);

	UnaryMinusMul;
	a = -b * c;
	a = c * -b;
}