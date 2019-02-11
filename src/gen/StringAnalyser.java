package gen;
import ast.*;
import java.io.PrintWriter;

public class StringAnalyser extends BaseASTVisitor<Integer> {

    private PrintWriter writer;
    private int StrLiteralCounter = 0;
    // Entry Point
    public void analyse(Program p, PrintWriter writer){
        this.writer = writer;
        //this.DST = DST;
        p.accept(this);
    }

    // PROGRAM
    // --------------------------------------------------------------------------
    @Override
    public Integer visitProgram(Program p) {
        //System.out.println("_________________________STRINGS____________________________");
        writer.println("############################################################");
        writer.println("#                      STRING ANALYSER                     #");

        for (FunDecl fd : p.funDecls)
                fd.accept(this);

        writer.println("############################################################");
        //System.out.println("############################################################");
        return null;
    }

    // LITERALS
    // --------------------------------------------------------------------------
    @Override
    public Integer visitStrLiteral(StrLiteral sl) {
        // Generate global var declaration that is
        // corresponding to sl : ArrayType(CharType, sl.size+1)
        // ----------------------------------------------------
        int size = sl.value.length() + 1;
        int wordAligner = (size%4==0)? size/4 : size/4 + 1;         // Size in words
        wordAligner = wordAligner * 4;                              // Size in bytes

        // Print the label in global
        // -------------------------
        String address = "$STR$" + Integer.toString(StrLiteralCounter++);
        String str = sl.value;

        // Write to global
        writer.print(address + ": " + ".asciiz \"");

        for (int i = 0; i < size - 1; i++){
            printout(str.charAt(i));
        }
        writer.print("\0");
        for (int i = size; i<wordAligner; i++)
            writer.print("\0");

        writer.println("\"");

        // Create coressponding global variable
        // ------------------------------------
        // create the array type
        ArrayType at = new ArrayType(BaseType.CHAR,size);
        at.offsetPerItem = 1;
        at.isGlobal = true;
        // create the vardecl
        VarDecl vd = new VarDecl(at, address);
        vd.offset = 0;
        vd.isGlobal = true;
        vd.byteSize = wordAligner;
        vd.address = address;
        // Create corresponding VarExpr
        // ----------------------------
        VarExpr ve = new VarExpr(address);
        ve.type = at;
        // Link global variable to varExpr
        ve.vd = vd;
        // Link var expr to string literal;
        sl.varExpr = ve;

        // Put in table
        //DST.addSymbol(vd,address,0);

        return null;
    }

    // FORWARDING
    // --------------------------------------------------------------------------
    @Override
    public Integer visitBlock(Block b) {
        for (Stmt stmt : b.stmts)
            stmt.accept(this);
        return null;
    }
    @Override
    public Integer visitWhile(While w) {
        w.expr.accept(this);
        w.stmt.accept(this);
        return null;
    }
    @Override
    public Integer visitIf(If i) {
        i.expr.accept(this);
        i.stmt.accept(this);
        if(i.stmtElse != null) {
            i.stmtElse.accept(this);
        }
        return null;
    }
    @Override
    public Integer visitFunDecl(FunDecl fd) {
        fd.block.accept(this);
        return null;
    }
    @Override
    public Integer visitBinOp(BinOp bo) {
        bo.expr_left.accept(this);
        bo.expr_right.accept(this);
        return null;
    }
    @Override
    public Integer visitVarExpr(VarExpr v) {
        return null;
    }
    @Override
    public Integer visitFunCallExpr(FunCallExpr fce) {
        for (Expr arg : fce.arguments)
            arg.accept(this);
        return null;
    }
    @Override
    public Integer visitArrayAccessExpr(ArrayAccessExpr aae) {
        aae.array.accept(this);
        aae.index.accept(this);
        return null;
    }
    @Override
    public Integer visitFieldAccessExpr(FieldAccessExpr fae) {
        fae.structure.accept(this);
        return null;
    }
    @Override
    public Integer visitValueAtExpr(ValueAtExpr vae) {
        vae.value.accept(this);
        return null;
    }
    @Override
    public Integer visitSizeOfExpr(SizeOfExpr soe) {
        return null;
    }
    @Override
    public Integer visitTypecastExpr(TypecastExpr te) {
        te.expr.accept(this);
        return null;
    }
    @Override
    public Integer visitExprStmt(ExprStmt es) {
        es.expr.accept(this);
        return null;
    }
    @Override
    public Integer visitAssign(Assign a) {
        a.assign_to.accept(this);
        a.assign_from.accept(this);
        return null;
    }
    @Override
    public Integer visitReturn(Return r) {
        if (r.expr!= null)
            r.expr.accept(this);
        return null;
    }
    @Override
    public Integer visitIntLiteral(IntLiteral il) { return null; }
    @Override
    public Integer visitChrLiteral(ChrLiteral cl) { return null; }


    private void printout(char c){
        if (c == '\t') writer.print("\\t");
        else if (c == '\b') writer.print("\\b");
        else if (c == '\n') writer.print("\\n");
        else if (c == '\r') writer.print("\\r");
        else if (c == '\f') writer.print("\\f");
        else if (c == '\\') writer.print("\\\\");
        else if (c == '\'') writer.print("\\\'");
        else if (c == '\"') writer.print("\\\"");
        else writer.print(c);
    }
}
