package sem;

import ast.*;

import java.util.LinkedList;
import java.util.List;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	private Scope scope = new Scope();


	// NAME ANALYSIS FUNCTIONALITY:
	// ----------------------------------------------------------------------
	@Override	// Adds in the minic-lib Symbols
	public Void visitProgram(Program p) {
		addMiniC_STD();
		for (StructTypeDecl std : p.structTypeDecls) {
			std.accept(this);
		}
		for (VarDecl vd : p.varDecls) {
			vd.accept(this);
		}
		for (FunDecl fd : p.funDecls) {
			fd.accept(this);
		}
		//System.out.println("ENDED NAME ANALYSIS");
		return null;
	}

	@Override
	public Void visitStructTypeDecl(StructTypeDecl sts) {
		// Structs Declaration opens a new scope for its VarDecls
		Scope prevScope = scope;
		scope = new Scope(prevScope);
		for ( VarDecl vd : sts.varDecls){
			vd.accept(this);
		}
		// After struct declaration return to previous scope
		scope = prevScope;
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl vd) {
		// Check if VarDecl does not exist in scope => add it to scope
		if (scope.lookupCurrent(vd.varName) == null)
			scope.put(new VarSymbol(vd));
		// If exists it is a secondary declaration => error
		else error("N: Secondary declaration " + vd.varName + " !");

		return null;
	}

	@Override
	public Void visitFunDecl(FunDecl p) {
		// Check if FunDecl already exists in the scope => error
		if (scope.lookupCurrent(p.name) != null){
			error("Redefinition of function " + p.name + " found!");
			return null;
		}
		// Add it to scope
		scope.put(new FunSymbol(p));
		// Open a new scope for its params and ITS BLOCK
		Scope prevScope = scope;
		scope = new Scope(scope);
		for (VarDecl vd : p.params){
			vd.accept(this);
		}
		// Block uses Function's new scope
		p.block.accept(this);

		// return to old scope
		scope = prevScope;
		return null;
	}

	@Override
	public Void visitBlock(Block b) {

		for (VarDecl vd : b.varDecls){
			vd.accept(this);
		}

		for (Stmt stmt : b.stmts){
			if (stmt == null) return null;

			// If it is a BLOCK STATEMENT
			if (stmt.getClass() == Block.class){
				// Open a new scope for the block
				Scope prevScope = scope;
				scope = new Scope(prevScope);
				stmt.accept(this);
				// Return to previous scope
				scope = prevScope;
			} else stmt.accept(this);
		}
		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr fce) {
		Symbol sLook = scope.lookup(fce.name);

		// Check if the symbol was declared
		if (sLook == null){
			error("Undefined function " + fce.name + " !");
			fce.fd = null;
			return null;
		}
		// Check if it is declared as a VarDecl
		if (sLook.getClass() == VarSymbol.class){
			error("Function " + fce.name + " already defined as variable!");
			fce.fd = null;
			return null;
		}

		// Connect FunDecl to FunCall
		fce.fd = ((FunSymbol)sLook).fd;

		// Go to arguments of the FunCall
		for (Expr arg : fce.arguments){
			if (arg != null){
				arg.accept(this);
			}
		}

		return null;
	}

	@Override
	public Void visitVarExpr(VarExpr v) {
		Symbol sLook = scope.lookup(v.name);

		// Check if the symbol was declared
		if (sLook == null){
			error("Undefined variable " + v.name + " !");
			return null;
		}
		// Check if it is declared as a FunDecl
		if (sLook.getClass() == FunSymbol.class){
			error("Variable " + v.name + " already defined as function!");
			return null;
		}

		// Connect VarDecl to VarExpr
		v.vd = ((VarSymbol)sLook).vd;

		return null;
	}

	@Override	// Does nothing
	public Void visitExpr(Expr epxr) {
		epxr.type.accept(this);
		return null;
	}

	// STATEMENTS
	// ----------------------------------------------------------------------
	@Override
	public Void visitReturn(Return r) {
		if (r.expr!=null)
			r.expr.accept(this);
		return null;
	}

	@Override
	public Void visitAssign(Assign a) {
		a.assign_from.accept(this);
		a.assign_to.accept(this);
		return null;
	}

	@Override
	public Void visitWhile(While w) {
		w.expr.accept(this);

		// If it is a BLOCK STATEMENT
		if (w.stmt.getClass() == Block.class){
			// Open a new scope for the block
			Scope prevScope = scope;
			scope = new Scope(prevScope);
			w.stmt.accept(this);
			// Return to previous scope
			scope = prevScope;
		} else w.stmt.accept(this);
		return null;
	}
	@Override
	public Void visitIf(If i) {
		i.expr.accept(this);

		// If it is a BLOCK STATEMENT
		if (i.stmt.getClass() == Block.class){
			// Open a new scope for the block
			Scope prevScope = scope;
			scope = new Scope(prevScope);
			i.stmt.accept(this);
			// Return to previous scope
			scope = prevScope;
		} else i.stmt.accept(this);

		if(i.stmtElse != null) {
			// If it is a BLOCK STATEMENT
			if (i.stmtElse.getClass() == Block.class){
				// Open a new scope for the block
				Scope prevScope = scope;
				scope = new Scope(prevScope);
				i.stmtElse.accept(this);
				// Return to previous scope
				scope = prevScope;
			} else i.stmtElse.accept(this);
		}
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
		es.expr.accept(this);
		return null;
	}

	@Override
	public Void visitStmt(Stmt stmt) { return null; }

	// OPERATIONS
	// ----------------------------------------------------------------------
	@Override
	public Void visitBinOp(BinOp bo) {
		bo.expr_right.accept(this);
		bo.op.accept(this);
		bo.expr_left.accept(this);
		return null;
	}

	@Override
	public Void visitOp(Op o) { return null; }

	// UNARY OPS
	// -------------------------------------------------------
	@Override
	public Void visitTypecastExpr(TypecastExpr tce) {
		tce.expr.accept(this);
		tce.type.accept(this);
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
		aae.array.accept(this);
		aae.index.accept(this);
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fae) {
		fae.structure.accept(this);
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr vae) {
		vae.value.accept(this);
		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr soe) {
		soe.type.accept(this);
		return null;
	}

	// LITERALS
	// ----------------------------------------------------------------------
	@Override
	public Void visitIntLiteral(IntLiteral il) { return null; }
	@Override
	public Void visitChrLiteral(ChrLiteral cl) { return null; }
	@Override
	public Void visitStrLiteral(StrLiteral sl) { return null; }

	// TYPES
	// ----------------------------------------------------------------------
	@Override
	public Void visitType(Type t) { return null; }
	@Override
	public Void visitBaseType(BaseType bt) { return null; }

	@Override
	public Void visitPointerType(PointerType pt) {
		pt.type.accept(this);
		return null;
	}

	@Override
	public Void visitStructType(StructType st) {
		// TODO: fill in?
		return null;
	}

	@Override
	public Void visitArrayType(ArrayType at) {
		at.type.accept(this);
		return null;
	}

	// FUNCTIONALOITY TO ADD MINIC-STD.H
	// ----------------------------------------------------------------------
	// Adds all functions from the minic-std.h
	private void addMiniC_STD(){
		addPrintS();
		addPrintC();
		addPrintI();
		addReadC();
		addReadI();
		addMalloc();
	}

	//	void print_s(const char* s) {
	//		fprintf(stdout,"%s",s);
	//	}
	private void addPrintS(){
		// Get function type
		Type type = BaseType.VOID;
		// Get function name
		String name = "print_s";
		// Get parameters
		Type paramType = new PointerType(BaseType.CHAR);
		List<VarDecl> params = new LinkedList<>();
		params.add(new VarDecl(paramType,"s"));
		// Get block
		Block block = new Block(new LinkedList<>(),new LinkedList<>());
		// Get function
		FunDecl fd = new FunDecl(type,name,params,block);
		// Add Symbol
		scope.put(new FunSymbol(fd));
	}

	//	void print_i(int i) {
	//		fprintf(stdout,"%d",i);
	//	}
	private void addPrintI(){
		// Get function type
		Type type = BaseType.VOID;
		// Get function name
		String name = "print_i";
		// Get parameters
		Type paramType = BaseType.INT;
		List<VarDecl> params = new LinkedList<>();
		params.add(new VarDecl(paramType,"i"));
		// Get block
		Block block = new Block(new LinkedList<>(),new LinkedList<>());
		// Get function
		FunDecl fd = new FunDecl(type,name,params,block);
		// Add Symbol
		scope.put(new FunSymbol(fd));
	}

	//	void print_c(char c) {
	//		fprintf(stdout,"%c",c);
	//	}
	private void addPrintC(){
		// Get function type
		Type type = BaseType.VOID;
		// Get function name
		String name = "print_c";
		// Get parameters
		Type paramType = BaseType.CHAR;
		List<VarDecl> params = new LinkedList<>();
		params.add(new VarDecl(paramType,"c"));
		// Get block
		Block block = new Block(new LinkedList<>(),new LinkedList<>());
		// Get function
		FunDecl fd = new FunDecl(type,name,params,block);
		// Add Symbol
		scope.put(new FunSymbol(fd));
	}

	//	char read_c() {
	//		char c;
	//		fscanf(stdin, " %c", &c);
	//		return c;
	//	}
	private void addReadC(){
		// Get function type
		Type type = BaseType.CHAR;
		// Get function name
		String name = "read_c";
		// Get parameters
		List<VarDecl> params = new LinkedList<>();
		// Get block
		Block block = new Block(new LinkedList<>(),new LinkedList<>());
		// Get function
		FunDecl fd = new FunDecl(type,name,params,block);
		// Add Symbol
		scope.put(new FunSymbol(fd));
	}

	//	int read_i() {
	//		int i;
	//		fscanf(stdin, "%d", &i);
	//		return i;
	//	}
	private void addReadI() {
		// Get function type
		Type type = BaseType.INT;
		// Get function name
		String name = "read_i";
		// Get parameters
		List<VarDecl> params = new LinkedList<>();
		// Get block
		Block block = new Block(new LinkedList<>(),new LinkedList<>());
		// Get function
		FunDecl fd = new FunDecl(type,name,params,block);
		// Add Symbol
		scope.put(new FunSymbol(fd));
	}

	//	void* mcmalloc(int size) {
	//		return malloc(size);
	//	}
	private void addMalloc(){
		// Get function type
		Type type = new PointerType(BaseType.VOID);
		// Get function name
		String name = "mcmalloc";
		// Get parameters
		Type paramType = BaseType.INT;
		List<VarDecl> params = new LinkedList<>();
		params.add(new VarDecl(paramType,"size"));
		// Get block
		Block block = new Block(new LinkedList<>(),new LinkedList<>());
		// Get function
		FunDecl fd = new FunDecl(type,name,params,block);
		// Add Symbol
		scope.put(new FunSymbol(fd));
	}

}
