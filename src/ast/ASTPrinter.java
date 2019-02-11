package ast;

import java.io.PrintWriter;

public class ASTPrinter implements ASTVisitor<Void> {

    private PrintWriter writer;
    public ASTPrinter(PrintWriter writer) {
            this.writer = writer;
    }

    // Utilities for printing (done from reverse engineering the fibonacci-ast-dump)
    private boolean isDepth = false;
    private int depth = 0;
    private void printDepth(){
        if(isDepth) {
            writer.print("\n");
            for (int i = 0; i < depth; i++) {
                writer.print("\t");
            }
        }
    }
    // The program top AST node (a list of struct type declaration, list of variable declarations, list of FunDecl definition)
    // Program    ::= StructTypeDecl* VarDecl* FunDecl*
    // ------------------------------------------------
    @Override
    public Void visitProgram(Program p) {
        printDepth();
        depth++;
        writer.print("Program(");
        String delimiter = "";
        for (StructTypeDecl std : p.structTypeDecls) {
            writer.print(delimiter);
            delimiter = ",";
            std.accept(this);
        }
        for (VarDecl vd : p.varDecls) {
            writer.print(delimiter);
            delimiter = ",";
            vd.accept(this);
        }
        for (FunDecl fd : p.funDecls) {
            writer.print(delimiter);
            delimiter = ",";
            fd.accept(this);
        }
        depth --;
        //printDepth();
        writer.print(")");
        writer.flush();
        return null;
    }


    // Types
    // --------------------------------------------------------------------------------------------------------------------------------
    // Type        ::= BaseType | PointerType | StructType | ArrayType
    @Override
    public Void visitType(Type t){
        // to be complete ...
        return null;
    }

    // BaseType    ::= INT | CHAR | VOID
    @Override
    public Void visitBaseType(BaseType bt) {
        //printDepth();
        //depth++;
        writer.print(bt.toString());
        //depth --;
        //printDepth();
        return null;
    }

    // PointerType ::= Type                             // use to represent pointers to other types
    @Override
    public Void visitPointerType(PointerType pt){
        //printDepth();
        //depth++;
        writer.print ("PointerType(");
        pt.type.accept(this);
        writer.print(")");
        //depth --;
        //printDepth();
        return null;
    }

    // StructType  ::= String                           // represent a struct type (the String is the name of the declared struct type)
     @Override
     public Void visitStructType(StructType stype){
         //printDepth();
         //depth++;
         writer.print("StructType(");
         writer.print(stype.string);
         writer.print(")");
         //depth --;
         //printDepth();
         return null;
     }

    // ArrayType   ::= Type int                         // Type represent the element type, int represents the number of elements (number of elements)
    @Override
    public Void visitArrayType(ArrayType at){
        //printDepth();
        //depth++;
        writer.write("ArrayType(");
        at.type.accept(this);
        writer.print (",");
        writer.print (at.length.toString());
        writer.print (")");
        //depth --;
        //printDepth();
        return null;
    }


    // Struct declaration
    // StructTypeDecl ::= StructType VarDecl*
    // --------------------------------------
    @Override
    public Void visitStructTypeDecl(StructTypeDecl std) {
        printDepth();
        depth++;
        writer.print("StructTypeDecl(");
        String delimiter = "";

        // Print StructType
        writer.print(delimiter);
        std.structType.accept(this);

        // Print VarDecls
        delimiter = ",";
        for (VarDecl vd : std.varDecls){
            writer.print(delimiter);
            delimiter = ",";
            vd.accept(this);
        }

        depth--;
        //printDepth();
        writer.print(")");
        return null;
    }

    // Variable declaration
    // VarDecl    ::= Type String
    // --------------------------
    @Override
    public Void visitVarDecl(VarDecl vd){
        printDepth();
        depth++;
        writer.print("VarDecl(");
        vd.type.accept(this);
        writer.print(","+vd.varName);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }

    // FunDecl definition (the String is the name of the FunDecl)
    // FunDecl  ::= Type String VarDecl* Block
    // ----------------------------------------------------------
    @Override
    public Void visitFunDecl(FunDecl fd) {
        printDepth();
        depth++;
        writer.print("FunDecl(");
        fd.type.accept(this);
        writer.print(","+fd.name+",");
        for (VarDecl vd : fd.params) {
            vd.accept(this);
            writer.print(",");
        }
        fd.block.accept(this);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Expressions
    // Expr       ::= IntLiteral | StrLiteral | ChrLiteral | VarExpr | FunCallExpr | BinOp | ArrayAccessExpr | FieldAccessExpr | ValueAtExpr | SizeOfExpr | TypecastExpr
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Void visitExpr (Expr e){
        // to complete ...
        return null;
    }


    // Literals
    // --------
    // IntLiteral ::= int                               // int stores the value of the integer
    @Override
    public Void visitIntLiteral(IntLiteral il){
        printDepth();
        depth++;
        writer.print("IntLiteral(");
        writer.print(il.value.toString());
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }

    // StrLiteral ::= String                            // String stored the value of the String
    @Override
    public Void visitStrLiteral(StrLiteral sl){
        printDepth();
        depth++;
        writer.print("StrLiteral(");
        writer.print(sl.value);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }

    // ChrLiteral ::= char                              // char stores the value of the character
    @Override
    public Void visitChrLiteral(ChrLiteral cl){
        printDepth();
        depth++;
        writer.print("ChrLiteral(");
        writer.print(cl.value);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Variable (the String is the name of the variable)
    // VarExpr     ::= String
    // -------------------------------------------------
    @Override
    public Void visitVarExpr(VarExpr v) {
        printDepth();
        depth++;
        writer.print("VarExpr(");
        writer.print(v.name);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Function call (the String corresponds to the name of the function to call and the Expr* is the list of arguments)
    // FunCallExpr ::= String Expr*
    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Void visitFunCallExpr(FunCallExpr fce){
        printDepth();
        depth++;
        writer.print ("FunCallExpr(");
        writer.print(fce.name);
        for (Expr arg : fce.arguments){
            writer.print(",");
            if (arg == null) System.out.println("SHIET END");
            else arg.accept(this);

        }
        writer.print(")");
        depth --;

        //printDepth();
        return null;
    }


    // Binary operations
    // BinOp      ::= Expr Op Expr
    // Op         ::= ADD | SUB | MUL | DIV | MOD | GT | LT | GE | LE | NE | EQ | OR | AND
    // ------------------------------------------------------------------------------------
    @Override
    public Void visitBinOp (BinOp bo){
        printDepth();
        depth++;
        //isDepth = false;
        writer.print("BinOp(");
        bo.expr_left.accept(this);
        writer.print(",");
        bo.op.accept(this);
        writer.print(",");
        bo.expr_right.accept(this);
        writer.print(")");
        //isDepth = true;
        depth --;
        //printDepth();
        return null;
    }

    @Override
    public Void visitOp (Op o){
        printDepth();
        depth++;
        //writer.print("Op(");
        writer.print(o.toString());
        //writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Array access expression : Expr[Expr] (e.g. a[10])
    // ArrayAccessExpr ::= Expr Expr // the first Expr is the array, the second one the index
    // --------------------------------------------------------------------------------------
    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr aae){
        printDepth();
        depth++;
        writer.print("ArrayAccessExpr(");
        aae.array.accept(this);
        writer.print(",");
        aae.index.accept(this);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Field access expression : Expr.String (e.g. *a.b)
    // FieldAccessExpr ::= Expr String // the Expr represents the structure, the String represents the name of the field
    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr fae){
        printDepth();
        depth++;
        writer.print("FieldAccessExpr(");
        fae.structure.accept(this);
        writer.print(",");
        writer.print(fae.fieldname);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Value at expression : *Expr (e.g. *p)
    // ValueAtExpr ::= Expr
    // -------------------------------------
    @Override
    public Void visitValueAtExpr(ValueAtExpr vae){
        printDepth();
        depth++;
        writer.print("ValueAtExpr(");
        vae.value.accept(this);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Sizeof expression : sizeof(Type) (e.g. sizeof(int*))
    // SizeOfExpr ::= Type
    // ----------------------------------------------------
    @Override
    public Void visitSizeOfExpr(SizeOfExpr soe){
        printDepth();
        depth++;
        writer.print("SizeOfExpr(");
        soe.type.accept(this);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Typecast expression : (Type)Expr (e.g. (int*) malloc(4))
    // TypecastExpr ::= Type Expr
    // --------------------------------------------------------
    @Override
    public Void visitTypecastExpr(TypecastExpr te){
        printDepth();
        depth++;
        writer.print("TypecastExpr(");
        te.type.accept(this);
        writer.print(",");
        te.expr.accept(this);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Statements
    // Stmt       ::= Block | While | If | Assign | Return | ExprStmt
    // --------------------------------------------------------------
    @Override
    public Void visitStmt(Stmt s){
        // to complete ...
        return null;
    }


    // An expression statement (e.g. x+2;)
    // ExprStmt ::= Expr
    // -----------------------------------
    @Override
    public Void visitExprStmt(ExprStmt es){
        printDepth();
        depth++;
        //isDepth = false;
        writer.print("ExprStmt(");
        es.expr.accept(this);
        writer.print(")");
        depth --;
        //isDepth = true;
        //printDepth();
        return null;
    }


    // While loop statement : while (Expr) Stmt;
    // While      ::= Expr Stmt
    // -----------------------------------------
    @Override
    public Void visitWhile (While w){
        printDepth();
        depth++;
        //isDepth = false;
        writer.print("While(");
        w.expr.accept(this);
        writer.print(",");
        w.stmt.accept(this);
        writer.print(")");
        depth --;
        //isDepth = true;
        //printDepth();
        return null;
    }


    // If statement: if (Expr) Stmt1 else Stmt2; (if the second Stmt is null, this means there is no else part)
    // If         ::= Expr Stmt [Stmt]
    // --------------------------------------------------------------------------------------------------------
    @Override
    public Void visitIf (If i){
        printDepth();
        depth++;
        writer.print("If(");
        i.expr.accept(this);
        writer.print(",");
        i.stmt.accept(this);
        if (i.stmtElse != null){
            writer.print(",");
            i.stmtElse.accept(this);
        }
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Assignment statement: Expr = Expr; (e.g. x[3] = 2;)
    // Assign     ::= Expr Expr
    // ---------------------------------------------------
    @Override
    public Void visitAssign (Assign a){
        printDepth();
        depth++;
        //isDepth = false;
        writer.print("Assign(");
        a.assign_to.accept(this);
        writer.print(",");
        a.assign_from.accept(this);
        writer.print(")");
        depth --;
        //isDepth = true;
        //printDepth();
        return null;
    }


    // Return statement : (the Expr is optional)
    // Return     ::= [Expr]
    // -----------------------------------------
    @Override
    public Void visitReturn (Return r){
        printDepth();
        depth++;
        writer.print("Return(");
        if (r.expr != null)
            r.expr.accept(this);
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }


    // Block statement (starts with { and end with } in the source code)
    // Block      ::= VarDecl* Stmt*
    // -----------------------------------------------------------------
    @Override
    public Void visitBlock(Block b) {
        printDepth();
        depth++;
        writer.print("Block(");
        String delimiter = "";
        for(VarDecl vd : b.varDecls){
            writer.print(delimiter);
            delimiter = ",";
            vd.accept(this);
        }
        for(Stmt stmt : b.stmts){
            writer.print(delimiter);
            delimiter = ",";
            stmt.accept(this);
        }
        writer.print(")");
        depth --;
        //printDepth();
        return null;
    }

}
