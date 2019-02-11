#include "somelib.h"
// EXAMPLES for AST PRINTER

struct node_t { int field1; char field2; };	// StructTypeDecl(StructType(node_t),VarDecl(INT,field1),VarDecl(CHAR,field2))



void foo() { return; } 		// FunDecl(VOID, foo, Block(Return()))



int main() {
	struct node_t n;	// VarDecl(StructType(node_t), n)
	y = 3*x;		// Assign(VarExpr(y),BinOp(IntLiteral(3), MUL, VarExpr(x)))
	a = -x;			// Assign(VarExpr(a),BinOp(IntLiteral(0), SUB, VarExpr(x)))
	a = -1;			// Assign(VarExpr(a),BinOp(IntLiteral(0),SUB,IntLiteral(1)))
	a = 2+3+4;		// Assign(VarExpr(a),BinOp(BinOp(IntLiteral(2), ADD, IntLiteral(3)), ADD, IntLiteral(4)))
	a = 2+3*4;		// Assign(VarExpr(a),BinOp(IntLiteral(2), ADD, BinOp(IntLiteral(3), MUL, IntLiteral(4)))
	
}

