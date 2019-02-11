package ast;

public interface ASTVisitor<T> {

    // The program top AST node (a list of struct type declaration, list of variable declarations, list of FunDecl definition)
    // Program    ::= StructTypeDecl* VarDecl* FunDecl*
    // -----------------------------------------------------------------------------------------------------------------------
    T visitProgram(Program p);


    // Types
    // Type        ::= BaseType | PointerType | StructType | ArrayType
    //
    // BaseType    ::= INT | CHAR | VOID
    // PointerType ::= Type              // use to represent pointers to other types
    // StructType  ::= String            // represent a struct type (the String is the name of the declared struct type)
    // ArrayType   ::= Type int          // Type represent the element type, int represents the number of elements (number of elements)
    // --------------------------------------------------------------------------------------------------------------------------------
    T visitType(Type t);
    T visitBaseType(BaseType bt);
    T visitPointerType(PointerType pt);
    T visitStructType(StructType stype);
    T visitArrayType(ArrayType at);


    // Struct declaration
    // StructTypeDecl ::= StructType VarDecl*
    // --------------------------------------
    T visitStructTypeDecl(StructTypeDecl st);


    // Variable declaration
    // VarDecl    ::= Type String
    // --------------------------
    T visitVarDecl(VarDecl vd);


    // FunDecl definition (the String is the name of the FunDecl)
    // FunDecl  ::= Type String VarDecl* Block
    // ----------------------------------------------------------
    T visitFunDecl(FunDecl p);


    // Expressions
    // Expr       ::= IntLiteral | StrLiteral | ChrLiteral | VarExpr | FunCallExpr | BinOp | ArrayAccessExpr | FieldAccessExpr | ValueAtExpr | SizeOfExpr | TypecastExpr
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------
    T visitExpr (Expr e);


    // Literals
    // IntLiteral ::= int     // int stores the value of the integer
    // StrLiteral ::= String  // String stored the value of the String
    // ChrLiteral ::= char    // char stores the value of the character
    // -----------------------------------------------------------------
    T visitIntLiteral(IntLiteral il);
    T visitStrLiteral(StrLiteral sl);
    T visitChrLiteral(ChrLiteral cl);


    // Variable (the String is the name of the variable)
    // VarExpr     ::= String
    // -------------------------------------------------
    T visitVarExpr(VarExpr v);



    // Function call (the String corresponds to the name of the function to call and the Expr* is the list of arguments)
    // FunCallExpr ::= String Expr*
    // -----------------------------------------------------------------------------------------------------------------
    T visitFunCallExpr(FunCallExpr fce);


    // Binary operations
    // BinOp      ::= Expr Op Expr
    // Op         ::= ADD | SUB | MUL | DIV | MOD | GT | LT | GE | LE | NE | EQ | OR | AND
    // ------------------------------------------------------------------------------------
    T visitBinOp (BinOp bo);
    T visitOp (Op o);


    // Array access expression : Expr[Expr] (e.g. a[10])
    // ArrayAccessExpr ::= Expr Expr // the first Expr is the array, the second one the index
    // --------------------------------------------------------------------------------------
    T visitArrayAccessExpr(ArrayAccessExpr aae);


    // Field access expression : Expr.String (e.g. *a.b)
    // FieldAccessExpr ::= Expr String // the Expr represents the structure, the String represents the name of the field
    // -------------------------------------------------
    T visitFieldAccessExpr(FieldAccessExpr fae);


    // Value at expression : *Expr (e.g. *p)
    // ValueAtExpr ::= Expr
    // -------------------------------------
    T visitValueAtExpr(ValueAtExpr vae);


    // Sizeof expression : sizeof(Type) (e.g. sizeof(int*))
    // SizeOfExpr ::= Type
    // ----------------------------------------------------
    T visitSizeOfExpr(SizeOfExpr soe);


    // Typecast expression : (Type)Expr (e.g. (int*) malloc(4))
    // TypecastExpr ::= Type Expr
    // --------------------------------------------------------
    T visitTypecastExpr(TypecastExpr te);


    // Statements
    // Stmt       ::= Block | While | If | Assign | Return | ExprStmt
    // --------------------------------------------------------------
    T visitStmt(Stmt s);


    // An expression statement (e.g. x+2;)
    // ExprStmt ::= Expr
    // -----------------------------------
    T visitExprStmt(ExprStmt es);


    // While loop statement : while (Expr) Stmt;
    // While      ::= Expr Stmt
    // -----------------------------------------
    T visitWhile (While w);


    // If statement: if (Expr) Stmt1 else Stmt2; (if the second Stmt is null, this means there is no else part)
    // If         ::= Expr Stmt [Stmt]
    // --------------------------------------------------------------------------------------------------------
    T visitIf (If i);


    // Assignment statement: Expr = Expr; (e.g. x[3] = 2;)
    // Assign     ::= Expr Expr
    // ---------------------------------------------------
    T visitAssign (Assign a);


    // Return statement : (the Expr is optional)
    // Return     ::= [Expr]
    // -----------------------------------------
    T visitReturn (Return r);


    // Block statement (starts with { and end with } in the source code)
    // Block      ::= VarDecl* Stmt*
    // -----------------------------------------------------------------
    T visitBlock(Block b);



}
