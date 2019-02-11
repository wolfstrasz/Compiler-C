package gen;
import ast.*;

import java.util.*;

public class FunReturnReconstructor extends BaseASTVisitor<Void>{

    // Tables to fill
    private RetExprTable RET;
    private FCExprTable FCT;

    // Flags
    private List<VarDecl> emptyVarDeclList = new LinkedList<>();
    private FunDecl curFD;
    private Stack<ASTNode> lastNodes = new Stack<>();
    private int fceCounter = 0;
    // Entry Point
    public void analyse(Program p, RetExprTable RET, FCExprTable FCT){
        this.RET = RET;
        this.FCT = FCT;
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
        fceCounter = 0;
        lastNodes.push(fd);
        curFD = fd;
        fd.returnVar = new VarDecl(fd.type, "$" + fd.name + "$ReturnVarDecl");
        fd.block.accept(this);
        lastNodes.pop();
        return null;
    }

    @Override
    public Void visitReturn(Return r) {
        if(curFD.type != BaseType.VOID /*&& !curFD.name.equals("main")*/){
            // We have "return Expr;"
            // -----------------------

            // Forwarding
            lastNodes.push(r);
            r.expr.accept(this);
            lastNodes.pop();

            // Construct new VarExpr'
            String returnVarName = "$" + curFD.name + "$ReturnVarExpr";
            if (!RET.contains(returnVarName)) {
                VarExpr ve = new VarExpr(returnVarName);
                ve.vd = curFD.returnVar;
                RET.put(returnVarName, ve);
            }

            // get VarExpr'
            VarExpr retVE = (VarExpr)RET.get(returnVarName);

            // create " VarExpr' = Expr; "
            Assign newAssign = new Assign(retVE, r.expr);

            // update " return Expr;" to "return; "
            r.expr = null;

            // Create a list of statements (assign,return)
            List<Stmt> newStmtList = new LinkedList<>();
            newStmtList.add(newAssign);
            newStmtList.add(r);

            // Create a block with no VarDecls and the new stmt list
            Block newBlock = new Block(emptyVarDeclList, newStmtList);

            // Put the new block in the previous node and remove the return
            ASTNode lastNode = lastNodes.pop();
            if (lastNode.getClass() == If.class) {
                replaceInIf((If) lastNode, newBlock, r);
            }
            if (lastNode.getClass() == While.class) {
                replaceInWhile((While) lastNode, newBlock, r);
            }
            if (lastNode.getClass() == Block.class) {
                replaceInBlock((Block) lastNode, newBlock, r);
            }

            // Return the node to the stack
            lastNodes.push(lastNode);
        }

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
    public Void visitFunCallExpr(FunCallExpr fce)  {
        // Forwarding
        if(notABuiltin(fce)) {
            for (Expr e : fce.arguments)
                e.accept(this);

            // We have a return type
            // -----------------------

            // Construct new VarExpr'
            String fceName = "$" + curFD.name + "$FCEReturnArg$" + fceCounter++ + "$from$" + fce.fd.name;
            if (!FCT.contains(fce)) {
                VarDecl vd = new VarDecl(fce.fd.type, fceName);
                curFD.block.varDecls.add(vd);
                fce.writeToVD = vd;
                FCT.put(fce, vd);
            } else {
                System.out.println("ERROR: FCExpr already exists in FCT (" + fce.toString() + ")");
            }
        }
        return null;
    }


    // FORWARDING
    // ----------------------------------------------------------------------
    @Override
    public Void visitBlock(Block b) {
        lastNodes.push(b);
        for(Stmt stmt : b.stmts){
            stmt.accept(this);
        }
        lastNodes.pop();
        return null;
    }
    @Override
    public Void visitIf(If i) {
        lastNodes.push(i);
        i.expr.accept(this);
        i.stmt.accept(this);
        if(i.stmtElse != null){
            i.stmtElse.accept(this);
        }
        lastNodes.pop();
        return null;
    }
    @Override
    public Void visitWhile(While w) {
        lastNodes.push(w);
        w.expr.accept(this);
        w.stmt.accept(this);
        lastNodes.pop();
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

    // UTILITIES
    // ----------------------------------------------------------------------
    // Replaces the return statement with the new node in the IF stmt
    private void replaceInIf(If ifNode, Block newBlock, Return r){
        if (ifNode.stmt == r){
            ifNode.stmt = newBlock;
            return;
        }else if (ifNode.stmtElse != null){
            if(ifNode.stmtElse == r) {
                ifNode.stmtElse = newBlock;
                return;
            }
        }
        // If we reach here there is an Error
        System.out.println("ERROR: replace in \"If\" failed!");
    }
    // Replaces the return statement with the new node in the WHILE stmt
    private void replaceInWhile(While whileNode, Block newBlock, Return r){
        if(whileNode.stmt == r){
            whileNode.stmt = newBlock;
            return;
        }
        // If we reach here there is an Error
        System.out.println("ERROR: replace in \"While\" failed!");
    }
    // Replaces the return statement with the new node in the Block stmt
    private void replaceInBlock(Block blockNode, Block newBlock, Return r){
        Iterator it = blockNode.stmts.iterator();
        int i = 0;
        int flag = -1;
        while (it.hasNext()){
            Stmt stmt = (Stmt)it.next();
            if(stmt.toString().equals(r.toString()) ){
                flag = i;
            }
            i++;
        }

        if (flag != -1){
            blockNode.stmts.remove(flag);
            blockNode.stmts.add(flag,newBlock);
           // System.out.println(blockNode.stmts.get(flag).toString());
            return;
        }
        // If we reach here there is an Error
        System.out.println("ERROR: replace in \"Block\" failed!");
    }
}
