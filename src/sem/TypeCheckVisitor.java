package sem;

import ast.*;

import java.util.HashMap;
import java.util.Map;


public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

	// ENVIRONMENT
	// --------------------------------------------------------------------
	// Handle Structures
	private Map<String,StructTypeDecl> structs = new HashMap<>();

	// Handle Functions
	private Map <String, FunDecl> funcs = new HashMap<>();
	private String curFunDeclName;

	// PROGRAM
	// --------------------------------------------------------------------
	@Override
	public Type visitProgram(Program p) {
		//System.out.println("Program: ");
		for (StructTypeDecl std : p.structTypeDecls){
			std.accept(this);
		}

		for (VarDecl vd : p.varDecls){
			vd.accept(this);
		}

		for (FunDecl fd : p.funDecls){
			fd.accept(this);
		}
		return null;
	}


	// DECLARATIONS
	// -------------------------------------------------------------------------
	@Override
	public Type visitStructTypeDecl(StructTypeDecl sts) {

		// Put struct decl data into map if it does not exists
		String structName = sts.structType.string;
		if (structs.get(structName) != null){
			error("T: Redefinition of structure " + structName + " found!");
			return null;
		}
		structs.put(sts.structType.string, sts);

		// Define variables in struct
		for (VarDecl vd : sts.varDecls){
			vd.accept(this);
		}
		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		Type type = vd.type;
		// skip null-pointer
		if (type == null) {
			return null;
		}
		if (type == BaseType.VOID){
			error("T: Declaration of VOID variable " + vd.varName + " not allowed!");
		}
		if (type.getClass() == StructType.class){
				((StructType)type).accept(this);
		}
		return vd.type;
	}

	@Override
	public Type visitFunDecl(FunDecl p) {
		p.type.accept(this);
		curFunDeclName = p.name;
		if (funcs.get(curFunDeclName) != null || isInMiniCstd(curFunDeclName)){
			error ("Redefinition of function " + curFunDeclName + " found!");
			return null;
		}
		for (VarDecl vd : p.params){
			vd.accept(this);
		}
		// Put the function in;
		funcs.put(curFunDeclName,p);

		// Go inside the block
		p.block.accept(this);

		return null;
	}


	// LITERALS
	// ------------------------------------------------------------------------
	@Override
	public Type visitIntLiteral(IntLiteral il) {
		il.type = BaseType.INT;
		//System.out.println( il.type.toString() + " | " + il.value);
		//if (il.type == null) System.out.println("IL is null");
		return il.type;
	}
	@Override
	public Type visitChrLiteral(ChrLiteral cl) {
		cl.type = BaseType.CHAR;
		//System.out.println(cl.type.toString() + " | " + cl.value);
		return cl.type;
	}
	@Override
	public Type visitStrLiteral(StrLiteral sl) {
		sl.type = new ArrayType(BaseType.CHAR, sl.value.length() + 1);
		//System.out.println(sl.type.toString() + " | " + sl.value + " | length = " + ((ArrayType)sl.type).length);
		return sl.type;
	}

	// TYPES
	// -------------------------------------------------------------------------
	@Override
	public Type visitType(Type t) { return null; }

	@Override	// RETURNS POINTERTYPE ( TYPE )
	public Type visitPointerType(PointerType pt) {
		pt.type.accept(this);
		return pt;
	}

	@Override
	public Type visitStructType(StructType st) {

		// Check if it exists for use
		if (structs.get(st.string) == null){
			error("T: Undefined struct type " + st.string + "!");
		}

		return st;
	}

	@Override
	public Type visitArrayType(ArrayType at) {
		at.type.accept(this);
		return at;
	}

	@Override	// RETURNS INT,CHAR,VOID
	public Type visitBaseType(BaseType bt) { return bt; }


	// EXPRESSIONS
	// -------------------------------------------------------------------------
	@Override
	public Type visitExpr(Expr epxr) { return null;	}

	@Override
	public Type visitVarExpr(VarExpr v) {
		if (v.vd != null)
			return (v.type = v.vd.type);
		return null;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr fce) {

		String funName = fce.name;

		FunDecl fd = fce.fd;
		//System.out.println(fd.name);
		// Skip null pointer exception
		if (fd == null)
		{
			//error("FD null");
			return null;
		}
//		System.out.println("HERE" + fce.name);

		// Check if size of arguments match
		if (fce.arguments.size() != fd.params.size()){
			error("T: Function call " + funName + "() number of arguments does not match!");
			return null;
		}

		// Check if Types of arguments match;
		int count = 0;
		for (Expr arg : fce.arguments){
			Type exprType = arg.accept(this);
			//if (exprType == null) return null;
			if (!areCompatibleTypes(exprType, fd.params.get(count).type)) {
				error("T: Argument " + Integer.toString(count + 1) + " of function call "
						 + funName + " does not match the function definition");
				return null;
			}
			count ++;
		}
		fce.type = fd.type;
		return fd.type;
	}

	@Override
	public Type visitTypecastExpr(TypecastExpr tce) {
		Type castee = tce.expr.accept(this);	// Expr type
		Type casting = tce.type.accept(this);	// Casting type
		// Skip null pointer exceptions;
		if (casting == null){
			error("T: Casting must be of type INT or POINTER");
			return null;
		}
		// Check typecasting to int
		if (casting == BaseType.INT){
			if (castee == BaseType.CHAR) return casting;
			error("T: Castring to INT type requires arguments of type CHAR");
			return null;
		}
		// Check typecasting to Pointer
		if (casting.getClass() == PointerType.class){
			// Check Pointer to Pointer
			if (castee.getClass() == PointerType.class) return casting;
			// Check Pointer to Array
			if (castee.getClass() == ArrayType.class){
				if (areCompatibleTypes((((ArrayType)castee).type), ((PointerType)casting).type))
					return casting;
				// ELSE ERROR
				error ("T: Casting from ARRAY type to POINTER requires same subtypes");
				return null;
			}
			// ELSE ERROR
			error("T: Casting to POINTER type requires arguments of type POINTER or ARRAY");
			return null;
		}
		// ELSE ERROR
		error ("T: Casting must be of type INT or POINTER");
		return null;
	}

	@Override
	public Type visitArrayAccessExpr(ArrayAccessExpr aae) {
		Type arrayType = aae.array.accept(this);
		Type indexType = aae.index.accept(this);

		// Skip null pointer exception
		if (arrayType == null || indexType == null){
			return null;
		}

		// Check if array access is possible
		if (indexType != BaseType.INT){
			error("T: Index of array must be of type INT!" );
			return null;
		}

		if (arrayType.getClass() == ArrayType.class){
			return ((ArrayType)arrayType).type;
		}

		if (arrayType.getClass() == PointerType.class){
			return ((PointerType)arrayType).type;
		}

		// ERRORS
		error ("Arrayaccess requests an Array type or a Pointer type to access the index!");
		return null;
	}

	@Override
	public Type visitFieldAccessExpr(FieldAccessExpr fae) {
		Type type = fae.structure.accept(this);

		// Skipp null-pointer exception
		if (type == null)
			return null;

		if ( type.getClass() == StructType.class){
			String structName = ((StructType)type).string;

			// Check if such struct exists
			StructTypeDecl std = structs.get(structName);
			if (std == null){
				error ("No struct definition of " + structName + " found.");
				return null;
			}

			//System.out.println(fae.fieldname);
			//System.out.println(fae.structure);
			// Check if such fieldname exists
			for ( VarDecl vd : std.varDecls){
				if ( vd.varName.equals(fae.fieldname)){
					return vd.type;
				}
			}
			// If reached here we did not find the field
			error("T: Fieldname " + fae.fieldname + " not found in struct definition.");
			return null;
		}
		// ERRORS
		// If reachead here that means we are not accessing a struct
		error ("Left-hand side of DOT operator requires struct variable");
		return null;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr vae) {
		Type type = vae.value.accept(this);

		// skip null-pointer exception
		if (type == null)
				return null;

		if ( type.getClass() == PointerType.class){
			return ((PointerType)type).type;
		}
		// if reached here we don't have a pointer
		error ("Dereferencing non-pointer type forbidden");
		return vae.type;
	}

	@Override
	public Type visitSizeOfExpr(SizeOfExpr soe) {
		Type type = soe.type.accept(this);
		// Skip null-pointer exception;
		if (type == null)
			return null;
		return BaseType.INT;
	}

	// EXPRESSIONS : OPERATIONS
	// -------------------------------------------------------------------------
	@Override
	public Type visitOp(Op o) {	return null; }

	@Override
	public Type visitBinOp(BinOp bo) {
		Type ltype = bo.expr_left.accept(this);
		Type rtype = bo.expr_right.accept(this);

		// skip null-pointer exception
		if (ltype == null || rtype == null)
			return null;

		// Type analysis for BinOp that is not EQ or NE
		if (bo.op != Op.EQ && bo.op != Op.NE){
			if (ltype != BaseType.INT || rtype != BaseType.INT){
				error( "Operation " + bo.op.toString() + " requires both operands of type INT!");
				return null;
			}
			return BaseType.INT;
		} else {
			// Ltype != StructType | ArrayType | VOID | null
			if (isNotBinOpType(ltype)){
				error( "Left operand of in operation " + bo.op.toString() + " must not be of types: {Array, Struct, Void}!");
				return null;
			}
			// Ltype must be equal to Rtype
			if (areCompatibleTypes(ltype, rtype))
				return BaseType.INT;
			error("T: Incompatible types of operands in operation " + bo.op + " found!");
			return null;
		}
	}


	// STATEMENTS
	// -------------------------------------------------------------------------

	@Override
	public Type visitStmt(Stmt stmt) { return null; }

	@Override
	public Type visitBlock(Block b) {

		for (VarDecl vd : b.varDecls){
			vd.accept(this);
		}

		for (Stmt stmt : b.stmts){
			if (stmt != null)
				stmt.accept(this);
		}

		return null;
	}

	@Override
	public Type visitExprStmt(ExprStmt es) {
		Type type = es.expr.accept(this);
		return type;
	}

	@Override
	public Type visitReturn(Return r) {
		Type expectedType = funcs.get(curFunDeclName).type;

		Type type;
		if (r.expr == null){
			if ( expectedType != BaseType.VOID)
				error ("T: Function " + curFunDeclName + " expects a return type of "
						+ expectedType.getClass().toString() + " !");
			return null;
		} else type = r.expr.accept(this);

		// Check if there is a "return;" but expected type is other
		if (type == null){
			return null;
		}

		// Check if "return Type;" equals the function's return type
		if (!areCompatibleTypes(expectedType,type)){
			error("T: Function " + curFunDeclName  + " expects a return type of "
					+ expectedType.getClass().toString() + " !");
		}
		return expectedType;
	}

	@Override
	public Type visitAssign(Assign a) {
		if (!isValidAssignExpr (a.assign_to)){
			error("T: Operator = expects left-hand side to be: {variable, field access, array access, dereferencing of pointer}!");
			return null;
		}
		Type type_to = a.assign_to.accept(this);
		Type type_from = a.assign_from.accept(this);
		// skip null-pointer exception
		if (type_to == null || type_from == null) return null;

		if (type_to == BaseType.VOID || type_to.getClass() == ArrayType.class){
			error("T: Operator = expects left-hand side expression to not be of types: {Array, VOID}!");
			return null;
		}
		//System.out.println(type_to.toString() + " = " + type_from.toString());
		if (!areCompatibleTypes(type_to, type_from)){
			error("T: Operator = expects matching types from both sides!");
		}
		// TODO: DO WE HAVE TO RETRIEVE ALL ERRORS POSSIBLE?
		return type_to;
	}

	@Override
	public Type visitWhile(While w) {
		Type type = w.expr.accept(this);
		if (type == BaseType.INT){
			w.stmt.accept(this);
			return null;
		}
		error ("Operation \"WHILE\" expects expression of type INT!");
		return null;
	}

	@Override
	public Type visitIf(If i) {
		Type type = i.expr.accept(this);
		if (type == BaseType.INT){
			i.stmt.accept(this);
			if (i.stmtElse != null)
				i.stmtElse.accept(this);
			return null;
		}
		error ("Operation \"IF\" expects expression of type INT!");
		return null;
	}

	// UTILITIES
	// ----------------------------------------------

	private boolean isNotBinOpType(Type type){
		if (type == null) return true;
		if (type.getClass() == ArrayType.class) return true;
		if (type.getClass() == StructType.class) return true;
		return type == BaseType.VOID;
	}
	private boolean areCompatibleTypes(Type t1, Type t2){
		// Check if either of them is null
		if (t1 == null || t2 == null) return false;

		// Check if types are of BaseType => used to stop recursion
		if ( t1.getClass() == BaseType.class || t2.getClass() == BaseType.class){
			return t1 == t2;
		}
		// Check if types are the same struct type
		if ( t1.getClass() == StructType.class || t2.getClass() == StructType.class){
			return ((StructType)t1).string.equals(((StructType)t2).string);
		}
		// Check if types have same higher Type class;
		if ( t1.getClass() == t2.getClass()){
			// Check for arrays
			if (t1.getClass() == ArrayType.class){
				if (((ArrayType)t1).length.equals(((ArrayType)t2).length)) return true;

				error("T: Arrays are of different size!");
				return false;
			}
			// Check for pointers
			else {
				return areCompatibleTypes(((PointerType)t1).type, ((PointerType)t2).type);
			}
		}

		return false;
	}
	private boolean isValidAssignExpr(Expr e){
		return (e.getClass() == VarExpr.class ||
				e.getClass() == FieldAccessExpr.class ||
				e.getClass() == ArrayAccessExpr.class ||
				e.getClass() == ValueAtExpr.class );
	}
	private boolean isInMiniCstd(String name){
		return name.equals("print_s") ||
				name.equals("print_i") ||
				name.equals("print_c") ||
				name.equals("read_c") ||
				name.equals("read_i") ||
				name.equals("mcmalloc");
	}
	// Not needed anymore
	private boolean typeIsVoid(Type type){
		if (type == BaseType.VOID) return true;
		if (type == BaseType.INT) return false;
		if (type == BaseType.CHAR) return false;
		if (type.getClass() == StructType.class) return false;
		if (type.getClass() == PointerType.class) return typeIsVoid(((PointerType)type).type);
		if (type.getClass() == ArrayType.class) return typeIsVoid(((ArrayType)type).type);
		return false;
	}
}
