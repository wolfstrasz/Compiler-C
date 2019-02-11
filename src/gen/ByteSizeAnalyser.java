package gen;
import ast.*;

import java.util.List;
import java.util.Map;

public class ByteSizeAnalyser extends BaseASTVisitor <Integer>{

    private Map<String, StructTypeDecl> structs;
    private CopyArgTable CAT;
    private FCExprTable FCT;
    private RetExprTable RET;
    private int curFunFrameSize;
    public void analyse(Program p, Map<String, StructTypeDecl> structs,
                        CopyArgTable CAT, FCExprTable FCT, RetExprTable RET){
        this.structs = structs;
        this.CAT = CAT;
        this.FCT = FCT;
        this.RET = RET;
        p.accept(this);
    }

    // PROGRAM
    // --------------------------------------------------------------------------
    @Override
    public Integer visitProgram(Program p) {
        //System.out.println("############################################################");
        //System.out.println("#                   BYTESIZE ANALYSER                      #");
        //System.out.println();
        //System.out.println("_________________________GLOBALS____________________________");
        for (VarDecl vd : p.varDecls){
            vd.byteSize = vd.accept(this);                  // Set size of variable
        }
        //System.out.println("_________________________OTHERS_____________________________");
        //System.out.println();
        for (FunDecl fd : p.funDecls){
            fd.accept(this);
        }
        ////System.out.println("############################################################");
        return null;
    }

    // Fun declaration
    @Override
    public Integer visitFunDecl(FunDecl fd) {

        fd.returnVar.accept(this);

        //System.out.println("\t\t\t\t\t\t " + fd.name + "\r\t\t\t\t   " +   "#FUNCT_NAME:");

        curFunFrameSize = 0;

        curFunFrameSize += 4; // add return argument offset space;
        //System.out.println("\t\t\t\t\t\t " + 4 + "\r" +   "$OFFSET");

        curFunFrameSize += 4; // add RA space
        //System.out.println("\t\t\t\t\t\t " + 4 + "\r" +   "$RA");

        curFunFrameSize += 4; // add OLD_FP space
        //System.out.println("\t\t\t\t\t\t " + 4 + "\r" +   "$OLD_FP");

        //System.out.println("VarDecl in block: " + fd.block.varDecls.size());
        for (VarDecl vd: fd.params){
            vd.byteSize = vd.accept(this);
            // FrameSize -> add parameters sizes
            curFunFrameSize+=vd.byteSize;
        }

        fd.block.accept(this);

        //System.out.println("\t\t\t\t\t\t " + curFunFrameSize + "\r\t\t\t\t   " +   "#OVERAL_SIZE:");
        //System.out.println("____________________________________________________________");
        //System.out.println();
        fd.frameSize = curFunFrameSize;
        return null;
    }

    // Var declaration
    @Override
    public Integer visitVarDecl(VarDecl vd) {
        Integer size = vd.type.accept(this);
        size = (size % 4 == 0) ? size : size + (4 - size % 4);
        vd.byteSize = size;
        //System.out.println("\t\t\t\t\t\t\t" + Integer.toString(vd.byteSize) + "\r" + vd.varName);
        return size;
    }


    // TYPES
    // --------------------------------------------------------------------------
    @Override
    public Integer visitBaseType(BaseType bt) {
        if (bt == BaseType.INT ) {
            return 4;
        }
        if (bt == BaseType.CHAR) {
            return 1;
        }
        return 0;
    }
    @Override
    public Integer visitPointerType(PointerType pt) {
        Integer typeSize = pt.type.accept(this);
        pt.offsetPerItem = typeSize;
        return 4;
    }
    @Override
    public Integer visitStructType(StructType stype) {
        StructTypeDecl std = structs.get(stype.string);
        return std.byteSize;
    }
    @Override
    public Integer visitArrayType(ArrayType at) {
        Integer typeSize = at.type.accept(this);
        at.offsetPerItem = typeSize;
        return typeSize * at.length;
    }

    // STATEMENTS
    // --------------------------------------------------------------------------
    @Override
    public Integer visitBlock(Block b) {
        for (VarDecl vd : b.varDecls){
            vd.byteSize = vd.accept(this);
            curFunFrameSize+=vd.byteSize; // FrameSize -> add variables sizes;
        }
        for (Stmt stmt : b.stmts){
            stmt.accept(this);
        }
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
        if(i.stmtElse != null)
            i.stmtElse.accept(this);
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
    public Integer visitExprStmt(ExprStmt es) {
        es.expr.accept(this);
        return null;
    }


    private boolean notABuiltin(FunCallExpr fce){
        if(fce.name.equals("print_i"))return false;
        if(fce.name.equals("print_c"))return false;
        if(fce.name.equals("print_s"))return false;
        if(fce.name.equals("read_i"))return false;
        if(fce.name.equals("read_c"))return false;
        if(fce.name.equals("mcmalloc"))return false;
        return true;
    }
    // FORWARDING
    // --------------------------------------------------------------------------
    // Exprs
    @Override
    public Integer visitBinOp(BinOp bo) {
        bo.expr_left.accept(this);
        bo.expr_right.accept(this);
        return null;
    }
    @Override
    public Integer visitVarExpr(VarExpr ve) {
        if(ve.vd.byteSize == -1)
            ve.vd.accept(this);
        return null;
    }
    @Override
    public Integer visitFunCallExpr(FunCallExpr fce) {

        if(notABuiltin(fce)) {
            List<Assign> assignStmts = CAT.get(fce);
            for (Assign a : assignStmts) {
                a.assign_to.accept(this);
                a.assign_from.accept(this); // this is the same as fce.args(EXPR);
            }
        }else {
            for (Expr arg : fce.arguments)
                arg.accept(this);
        }
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
        return vae.value.accept(this);
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

    // Literals
    @Override
    public Integer visitIntLiteral(IntLiteral il) { return null; }
    @Override
    public Integer visitChrLiteral(ChrLiteral cl) { return null; }
    @Override
    public Integer visitStrLiteral(StrLiteral sl) { return null; }
}
