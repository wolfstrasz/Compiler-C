package gen;
import ast.*;
public class BlockVarReconstructor extends BaseASTVisitor<Void>{

    FunDecl curFD;

    public void reconstruct(Program p){

        p.accept(this);
    }

    @Override
    public Void visitProgram(Program p){
        //System.out.println("############################################################");
        //System.out.println("#               BLOCK VARIABLE RECONSTRUCTION              #");
        for ( FunDecl fd : p.funDecls){
            curFD = fd;
            fd.accept(this);
        }
        //System.out.println("############################################################");
        return null;
    }
    @Override
    public Void visitFunDecl(FunDecl fd){
        // SKIP FUNCTION DECL MAIN BLOCK
        for(Stmt s : fd.block.stmts)
            s.accept(this);
        return null;
    }

    public Void visitBlock(Block b){

        for(VarDecl vd : b.varDecls){
            //System.out.println("# Pushing in " + curFD.name + " : " + vd.varName);
            curFD.block.varDecls.add(vd);
        }
        b.varDecls.clear();
        for(Stmt s : b.stmts){
            s.accept(this);
        }

        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt es) {
        return null;
    }

    @Override
    public Void visitIf(If i) {
        i.stmt.accept(this);
        if(i.stmtElse!=null)
            i.stmtElse.accept(this);
        return null;
    }

    @Override
    public Void visitWhile(While w) {
        w.stmt.accept(this);
        return null;
    }

    @Override
    public Void visitAssign(Assign a) {
        return null;
    }

    @Override
    public Void visitReturn(Return r) {
        return null;
    }
}
