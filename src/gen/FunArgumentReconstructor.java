package gen;

import ast.*;

import java.util.LinkedList;
import java.util.List;

public class FunArgumentReconstructor extends BaseASTVisitor<Void> {

    private CopyArgTable CAT;
    private String curFDname;
    public void analyse(Program p, CopyArgTable CAT){
        this.CAT = CAT;
        p.accept(this);
    }

    @Override
    public Void visitProgram(Program p) {
        for (FunDecl fd : p.funDecls)
            fd.accept(this);
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {
        curFDname = fd.name;
        fd.block.accept(this);
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
    @Override
    public Void visitFunCallExpr(FunCallExpr fce){
        if (notABuiltin(fce)) {
            int i = 0;
            List<Assign> assignArgs = new LinkedList<>();
            for (Expr e : fce.arguments) {
                e.accept(this); // there might be fces inside this fce
                // Create a new VarDecl for the argument[i]
                String varArgName = "$" + fce.fd.name + "$Argument" + i + "$from$" + curFDname;
                VarDecl vdArg = new VarDecl(fce.fd.params.get(i).type, varArgName);
                VarExpr veArg = new VarExpr(varArgName);
                veArg.vd = vdArg;

                // Create an Assign stmt
                Assign a = new Assign(veArg, e);

                // Push assign
                assignArgs.add(a);
                i++;
            }
            CAT.put(fce, assignArgs);
        }
        return null;
    }
    // FORWARDING
    // ----------------------------------------------------------------------
    @Override
    public Void visitReturn(Return r){
        if (r.expr != null)
            r.expr.accept(this);
        return null;
    }
    @Override
    public Void visitBlock(Block b) {
        for(Stmt stmt : b.stmts){
            stmt.accept(this);
        }
        return null;
    }
    @Override
    public Void visitIf(If i) {
        i.expr.accept(this);
        i.stmt.accept(this);
        if(i.stmtElse != null){
            i.stmtElse.accept(this);
        }
        return null;
    }
    @Override
    public Void visitWhile(While w) {
        w.expr.accept(this);
        w.stmt.accept(this);
        return null;
    }
    @Override
    public Void visitAssign(Assign a){
        a.assign_to.accept(this);
        a.assign_from.accept(this);
        return null;
    }
    @Override
    public Void visitExprStmt(ExprStmt es){
        es.expr.accept(this);
        return null;
    }
    @Override
    public Void visitBinOp(BinOp bo){
        bo.expr_left.accept(this);
        bo.expr_right.accept(this);
        return null;
    }
    @Override
    public Void visitVarExpr(VarExpr ve) {
        return null;
    }
    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr fae) {
        fae.structure.accept(this);
        return null;
    }
    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
        aae.array.accept(this);
        aae.index.accept(this);
        return null;
    }
    @Override
    public Void visitTypecastExpr(TypecastExpr te)  {
        te.expr.accept(this);
        return null;
    }
    @Override
    public Void visitValueAtExpr(ValueAtExpr vae)  {
        vae.value.accept(this);
        return null;
    }
    @Override
    public Void visitSizeOfExpr(SizeOfExpr soe)  {
        return null;
    }
    @Override
    public Void visitStrLiteral(StrLiteral sl){ return null; }
    @Override
    public Void visitChrLiteral(ChrLiteral cl){ return null; }
    @Override
    public Void visitIntLiteral(IntLiteral il){ return null; }

}
