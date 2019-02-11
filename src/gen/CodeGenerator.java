package gen;

import ast.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class CodeGenerator implements ASTVisitor<Register> {
    /*
     * Simple register allocator.
     */
    private PrintWriter writer; // use this writer to output the assembly instructions

    // contains all the free temporary registers
    private Stack<Register> freeRegs = new Stack<Register>();
    public CodeGenerator() { freeRegs.addAll(Register.tmpRegs); }

    // Handle Registers
    // ----------------
    private class RegisterAllocationError extends Error {}
    private Register getRegister() {
        try {
            Register popReg = freeRegs.pop();
            usedRegisters.addLast(popReg);     // the register to be used is put in the DEQUE
            allTempRegs.add(popReg);
            if(popReg == null)
                System.out.println("GET REGISTER RETURNED: NULL REG");
            return popReg;
        } catch (EmptyStackException ese) {
            throw new RegisterAllocationError(); // no more free registers, bad luck!
        }
    }
    private void freeRegister(Register reg) {
        freeRegs.push(reg);
        usedRegisters.remove(reg);          // the register to be free is removed from the DEQUE
        allTempRegs.remove(reg);
    }

    // Handle Labels
    // -------------
    private int labelCount = 0;
    private int whileLabelCount = 0;
    private int ifLabelCount = 0;

    // Handle templates
    // ----------------
    private FunDecl curFD;
    private Deque<ASTNode> previousNode = new LinkedList<>();
    private BinOp emptyNode = new BinOp(new IntLiteral(0),Op.ADD, new IntLiteral(0));

    // Handle Register Spilling
    // ------------------------
    private Deque<Register> usedRegisters = new LinkedList<>();
    private Stack<Integer> lastSpillAmount = new Stack<>();
    private void spillRegisters (int count){
        int k = 0;
        // check if there are enough registers
        // if not spills the ammount needed (ammount = k)  // K and count are not equal always
        while (count>freeRegs.size()){
        //      pops top reg of the deque -> regI
            Register reg = usedRegisters.removeFirst();
        //      write code to push to stack
            writer.println("\t# Reg spill ");
            writer.println("\taddi\t$sp,$sp, -4");
            writer.println("\tsw\t" + reg.toString() + ", 0($sp)");

        //      free reg
            freeRegister(reg);
            k++;
        }
        // pushes (K to stack LastSpillAmount)
        lastSpillAmount.push(k);
    }       // Should be added to getRegister if null
    private void collectRegisters (){
        // retrieves K from stack
        int k = lastSpillAmount.pop();
        // collects K registers
        while (k > 0) {
        //      writes code to pull from stack
            Register reg = getRegister();
            writer.println("\t# Reg collect");
            writer.println("\tlw\t" + reg.toString() + ", 0($sp)");
            writer.println("\taddi\t$sp, $sp, 4");
            k--;
        }
    }              // Should be added to freeRegister (if stack is not empty)

    // Handle Registers Store
    // ----------------------
    private Set<Register> allTempRegs = new HashSet<>();
    private void storeAllRegs (){
        writer.println("\t# Store all temp registers");
        int stackVal = 0;
        Iterator it = allTempRegs.iterator();
        while (it.hasNext()){
            Register reg = (Register)it.next();
            stackVal--;
            writer.println("\tsw\t" + reg.toString() + ", " + stackVal * 4 + "($sp)");
        }
        writer.println("\taddi\t$sp,$sp," + stackVal*4);
    }
    private void loadAllRegs (){
        writer.println("\t# Load all temp registers");
        int stackVal = allTempRegs.size() - 1;
        Iterator it = allTempRegs.iterator();
        while (it.hasNext()) {
            Register reg = (Register)it.next();
            writer.println("\tlw\t" + reg.toString() + ", " + stackVal * 4 + "($sp)");
            stackVal--;
        }
        writer.println("\taddi\t$sp,$sp," + allTempRegs.size()*4);
    }

    // Handle Register Clean
    // ---------------------
    private void cleanRegisterData(){
        freeRegs.clear();
        freeRegs.addAll(Register.tmpRegs);
        allTempRegs.clear();
        while(!usedRegisters.isEmpty()){
            usedRegisters.removeLast();
        }
        while(!lastSpillAmount.empty()){
            lastSpillAmount.pop();
        }
    }

    // AST PASSES
    // ----------
    private Map<String, StructTypeDecl> structs = new HashMap<>();
    private RetExprTable RET = new RetExprTable();
    private FCExprTable FCT = new FCExprTable();
    private CopyArgTable CAT = new CopyArgTable();
    private AccessTable AT = new AccessTable();

    private StructAnalyser structAnalyser = new StructAnalyser();
    private FunReturnReconstructor funReturnReconstructor = new FunReturnReconstructor();
    private FunArgumentReconstructor funArgumentReconstructor = new FunArgumentReconstructor();
    private ByteSizeAnalyser byteSizeAnalyser = new ByteSizeAnalyser();
    private StringAnalyser stringAnalyser = new StringAnalyser();
    private BlockVarReconstructor blockVarReconstructor = new BlockVarReconstructor();
    private AddressAnalyser addressAnalyser = new AddressAnalyser();
    private AccessAnalyser accessAnalyser = new AccessAnalyser();
    private AddressabilityFinder AF;


    // FOR LOGGING
    private boolean isLogged = false;

    // Entry Point
    public void emitProgram(Program program, File outputFile) throws FileNotFoundException {
        writer = new PrintWriter(outputFile);
        writer.println(".data");

        // put all vardecl in main func block
        blockVarReconstructor.reconstruct(program);
        // get data for structures
        structAnalyser.analyse(program, writer, structs);
        // expand return stmts to block ( assign, return);
        // and create var declaration for function call returns
        funReturnReconstructor.analyse(program, RET, FCT);
        // create assign stmts for copying expr to arguments
        funArgumentReconstructor.analyse(program, CAT);
        // analyse the sizes of variables
        byteSizeAnalyser.analyse(program, structs, CAT, FCT, RET);
        // analyse the address of each variable declaration
        addressAnalyser.analyse(program, writer);
        // find string literals and make them global
        stringAnalyser.analyse(program, writer);

        // analyse access offsets
        accessAnalyser.analyse(program, AT, structs, CAT);

        AF = new AddressabilityFinder(structs);

        emitStartJump();

        printTables();
        visitProgram(program);
        writer.close();
    }

    // PROGRAM
    // --------------------------------------------------------------------------
    @Override
    public Register visitProgram(Program p) {
        for (FunDecl fd : p.funDecls){
            fd.accept(this);
        }
        return null;
    }


    // GLOBAL DECLARATIONS
    // --------------------------------------------------------------------------
    @Override
    public Register visitFunDecl(FunDecl fd) {
        curFD = fd;

        if(fd.name.equals("main")){
            emitMainEntry();
        } else emitFunctionEntry(fd);

        fd.block.accept(this);

        if (fd.name.equals("main")) {
            emitMainEpilogue();
        }else emitFunctionEpilogue(fd);

        cleanRegisterData();
        return null;
    }


    // EXPRESSIONS
    // --------------------------------------------------------------------------
    @Override
    public Register visitVarExpr(VarExpr v) {
        emitStart(v);

        Register addr = this.getRegister();
        // Check if it is the function return var expr
        if (RET.get(v.name) != null) {
            // load addres from return value address address
            writer.println("\tlw\t" + addr.toString() + ", " + (curFD.frameSize - (curFD.argumentsByteSize + 4)) + "($fp)");
        } else {
            if (v.vd.isGlobal)
                writer.println("\tla\t" + addr.toString() + ", " + v.vd.address);
            else
                writer.println("\taddiu\t" + addr.toString() + ", " + v.vd.address + ", " + v.vd.offset);
        }

        return valueAnalysis(addr, v);
    }

    @Override
    public Register visitFunCallExpr(FunCallExpr fce) {
        emitStart(fce);
        writer.println("# Function call : " + fce.name);

        // print_i
        if (fce.name .equals("print_i"))
            return emitPrint_i(fce);
        // print_c
        if (fce.name.equals("print_c"))
            return emitPrint_c(fce);
        // print_s
        if (fce.name.equals("print_s"))
            return emitPrint_s(fce);
        // read_i
        if (fce.name.equals("read_i"))
            return emitRead_i(fce);
        // read_c
        if (fce.name.equals("read_c"))
            return emitRead_c(fce);
        if (fce.name.equals("mcmalloc"))
            return emitMcmalloc(fce);

        previousNode.addLast(fce);
        // Before call:
        // -------------------------------------------------------------
        // 1) Store used registers
        storeAllRegs();

        // 2) Load ret value address
        //    of corresponding fce to $a0
        writer.println("\taddiu\t$a0, " + fce.writeToVD.address + ", " + fce.writeToVD.offset);

        // 3) From ArgN to Arg0 copy the arguments
        List<Assign> copyArgs = CAT.get(fce);
        int argNum = copyArgs.size()-1;
        Iterator it = copyArgs.listIterator(copyArgs.size());
        // offset dependent on SP
        int offset = 0;
        while (((ListIterator) it).hasPrevious()){
            writer.println("\t# Copying argument " + argNum);
            // get copy assignment
            Assign as = (Assign)((ListIterator) it).previous();
            // get left var expr
            VarExpr lve = (VarExpr)as.assign_to;
            // get byte syze
            offset -= lve.vd.byteSize;
            // set the offset and address (negative of $SP)
            lve.vd.address = "$sp";
            lve.vd.offset = offset;
            // execute copy
            as.accept(this);
            argNum--;           // just for printing error checking
        }
        previousNode.removeLast();

        // CALL:
        // ---------------------
        writer.println("\tjal\t" + fce.fd.name);

        // AFTER CALL:
        // ---------------------
        // 1) Load registers
        loadAllRegs();

        // 2) depending on addr/value policy return return value
        Register addr = getRegister();
        writer.println("\taddiu\t" + addr.toString() + ", " + fce.writeToVD.address + ", " + fce.writeToVD.offset);

        return valueAnalysis(addr, fce);
    }

    @Override
    public Register visitFieldAccessExpr(FieldAccessExpr fae) {
        emitStart(fae);

        // Get address
        previousNode.addLast(fae);
        Register addr = fae.structure.accept(this);
        previousNode.removeLast();

        // Increment to fieldname
        Register field = getRegister();
        writer.println("\tli\t" + field.toString() + ", " + AT.getOffset(fae));
        writer.println("\taddu\t" + addr.toString() + ", " + addr.toString() + ", " + field.toString());
        freeRegister(field);

        return valueAnalysis(addr, fae);
    }

    @Override
    public Register visitArrayAccessExpr(ArrayAccessExpr aae) {
        emitStart(aae);

        // Get address
        previousNode.addLast(aae);
        Register addr = aae.array.accept(this);
        previousNode.removeLast();

        ASTNode prevNode = previousNode.getLast();

//        if (prevNode.getClass() == Assign.class){
//            if(((Assign)prevNode).assign_from == aae){
//
//            }
//
//        }
        if(isPointerAddressable(aae.array.accept(AF))){
          if (prevNode.getClass() != ValueAtExpr.class &&
                prevNode.getClass() != Assign.class &&
                prevNode.getClass() != ArrayAccessExpr.class &&
                aae.array.getClass() != ArrayAccessExpr.class)
            writer.println("\tlw\t" + addr.toString() + ", 0(" + addr.toString() + ") #from AAE !valueat");
        }
        // Check if Pointer array access and get the address it points to
//        if(isPointerAddressable(aae.array.accept(AF))) {
//            if (!(prevNode.getClass() == Assign.class)) {
//                writer.println("\tlw  " + addr.toString() + ", 0(" + addr.toString() + ") #from AAE !assign");
//            } else {
//                if(!(((Assign)prevNode).assign_to == aae))
//                    writer.println("\tlw  " + addr.toString() + ", 0(" + addr.toString() + ") #from AAE !assign_to");
//            }
//        }


        // Get index
        previousNode.addLast(emptyNode);   // We always need an int vlaue for index
        Register index = aae.index.accept(this);
        previousNode.removeLast();

        // Increment to index
        Register offset = getRegister();
        writer.println("\tli\t" + offset.toString() + ", " + AT.getOffset(aae));
        writer.println("\tmul\t" + index.toString() + ", " + index.toString() + ", " + offset.toString());
        freeRegister(offset);
        writer.println("\taddu\t" + addr.toString() + ", " + addr.toString() + ", " + index.toString());
        freeRegister(index);

        if(isPointerAddressable(aae.array.accept(AF))) {
            if(prevNode.getClass() == ArrayAccessExpr.class)
                writer.println("\tlw\t" + addr.toString() + ", 0(" + addr.toString() + ") #from AAE prev is AAe");
        }

        return valueAnalysis(addr, aae);
    }

    @Override
    public Register visitValueAtExpr(ValueAtExpr vae) {
        emitStart(vae);

        // Get address
        previousNode.addLast(vae);
        Register addr = vae.value.accept(this);
        previousNode.removeLast();

        // Get the address inside the pointer
        writer.println("\tlw\t" + addr.toString() + ", 0(" + addr.toString() + ")");
        return valueAnalysis(addr, vae);
    }

    @Override
    public Register visitTypecastExpr(TypecastExpr te) {
        emitStart(te);

        // Get address
        previousNode.addLast(te);
        Register addr = te.expr.accept(this);
        previousNode.removeLast();

        return valueAnalysis(addr, te);
    }

    @Override
    public Register visitSizeOfExpr(SizeOfExpr soe) {
        emitStart(soe);

        Register reg = getRegister();
        if (soe.type == BaseType.CHAR) {
            writer.println("\tli\t" + reg.toString() + ", " + 1);
        }else if (soe.type.getClass() == BaseType.class){
            writer.println("\tli\t" + reg.toString() + ", " + 4);
        }else if (soe.type.getClass() == PointerType.class){
            writer.println("\tli\t" + reg.toString() + ", " + 4);
        }else if (soe.type.getClass() == StructType.class){
            String structName = ((StructType)soe.type).string;
            StructTypeDecl std = structs.get(structName);
            writer.println("\tli\t" + reg.toString() + ", " + std.byteSize);
        }

        emitEnd(soe);
        return reg;
    }


    // OPERATIONS
    // --------------------------------------------------------------------------
    @Override
    public Register visitBinOp(BinOp bo) {
        emitStart(bo);
        previousNode.addLast(bo);

        Register lr = bo.expr_left.accept(this);
        Register rr;
        Op op = bo.op;
        switch (op){
            case ADD: {
                rr = bo.expr_right.accept(this);
                writer.println("\taddu\t" + lr.toString() + ", " + lr.toString() + ", " + rr.toString());
                break;
            }
            case SUB: {
                rr = bo.expr_right.accept(this);
                writer.println("\tsubu\t" + lr.toString() + ", " + lr.toString() + ", " + rr.toString());
                break;
            }
            case MUL: {
                rr = bo.expr_right.accept(this);
                writer.println("\tmul\t" + lr.toString() + ", " + lr.toString() + ", " + rr.toString());
                break;
            }
            case DIV: {
                rr = bo.expr_right.accept(this);
                writer.println("\tdiv\t" + lr.toString() + ", " + lr.toString() + ", " + rr.toString());
                writer.println("\tmflo\t" + lr.toString());
                break;
            }
            case MOD: {
                rr = bo.expr_right.accept(this);
                writer.println("\tdiv\t" + lr.toString() + ", " + lr.toString() + ", " + rr.toString());
                writer.println("\tmfhi\t" + lr.toString());
                break;
            }
            case AND: {
                // Search for early false
                String labelF = "L" + labelCount++;
                String labelExit = "L" + labelCount++;

                writer.println("\tbeqz\t" + lr.toString() + ", " + labelF);   // Check 0 && X
                rr = bo.expr_right.accept(this);
                writer.println("\tbeqz\t" + rr.toString() + ", " + labelF);   // Check (!0) && 0
                // if not Zeros => true
                writer.println("\tli\t" + lr.toString() + ", " + 1);
                writer.println("\tj\t" + labelExit);
                // if Zeros => false
                writer.println(labelF + ":");
                writer.println("\tli\t" + lr.toString() + ", " + 0);
                // end label
                writer.println(labelExit + ":");
                break;
            }
            case OR: {
                // Search for early true
                String labelT = "L" + labelCount++;
                String labelExit = "L" + labelCount++;
                // If left hand is true exit OR
                writer.println("\tbnez\t" + lr.toString() + ", " + labelT);   // Check (!0) || X
                // Else check right-hand expr
                rr = bo.expr_right.accept(this);
                writer.println("\tbnez\t" + rr.toString() + ", " + labelT);   // Check 0 || (!0)
                // if all Zeros => false
                writer.println("\tli\t" + lr.toString() + ", " + 0);
                writer.println("\tj\t" + labelExit);
                // if not boh zero => true
                writer.println(labelT + ":");
                writer.println("\tli\t" + lr.toString() + ", " + 1);
                // end label
                writer.println(labelExit + ":");
                break;
            }
            case EQ: {
                rr = bo.expr_right.accept(this);
                String labelF = "L" + labelCount++;
                String labelT = "L" + labelCount++;
                writer.println("\tbeq\t" + lr.toString() + ", " + rr.toString() + ", " + labelT); // Check X == Y
                // if X!=Y
                writer.println("\tli\t" + lr.toString() + ", " + 0);
                writer.println("\tj\t" + labelF);
                // if X==Y
                writer.println(labelT + ":");
                writer.println("\tli\t" + lr.toString()+ ", " + 1);
                // End label
                writer.println(labelF + ":");
                break;
            }
            case NE: {
                rr = bo.expr_right.accept(this);
                String labelF = "L" + labelCount++;
                String labelT = "L" + labelCount++;
                writer.println("\tbne\t" + lr.toString() + ", " + rr.toString() + ", " + labelT); // Check X != Y
                // if X==Y
                writer.println("\tli\t" + lr.toString() + ", " + 0);
                writer.println("\tj\t" + labelF);
                // if X!=Y
                writer.println(labelT + ":");
                writer.println("\tli\t" + lr.toString()+ ", " + 1);
                // End label
                writer.println(labelF + ":");
                break;
            }
            case LT: {
                rr = bo.expr_right.accept(this);
                writer.println("\tslt\t" + lr.toString() + ", " + lr.toString() + ", " + rr.toString());
                break;
            }
            case LE: {
                rr = bo.expr_right.accept(this);
                writer.println("\tsle\t" + lr.toString() + ", " + lr.toString() + ", " + rr.toString());
                break;
            }
            case GE: { // X >= Y  <=> Y <= X
                rr = bo.expr_right.accept(this);
                writer.println("\tsle\t" + lr.toString() + ", " + rr.toString() + ", " + lr.toString());
                break;
            }
            case GT: { // X > Y   <=> Y < x
                rr = bo.expr_right.accept(this);
                writer.println("\tslt\t" + lr.toString() + ", " + rr.toString() + ", " + lr.toString());
                break;
            }
            // Should not reach default
            default: {
                rr = getRegister();
                System.out.println("ERROR at BIN OP");
                break;
            }

        }
        freeRegister(rr);
        // EXIT
        previousNode.removeLast();
        emitEnd(bo);
        return lr;
    }

    // STATEMENTS
    // --------------------------------------------------------------------------
    @Override
    public Register visitBlock(Block b) {
        previousNode.addLast(b);
        for (Stmt stmt : b.stmts){
            stmt.accept(this);
        }
        previousNode.removeLast();
        return null;
    }

    @Override
    public Register visitWhile(While w) {
        previousNode.addLast(w);

        String labelT  = "$WHILE" + whileLabelCount;
        String labelF  = "$WHILE_END" + whileLabelCount++;

        // Check if we would skip the while
        Register reg = w.expr.accept(this);
        writer.println("\tbeqz\t" + reg.toString() + ", " + labelF);
      //  writer.println("\tj    " + labelF);

        // In while
        writer.println(labelT + ":" );
        freeRegister(reg);
        w.stmt.accept(this);

        // check if we are staying in the while
        reg = w.expr.accept(this);
        writer.println("\tbnez\t" + reg.toString() + ", " + labelT);
        // leaving the while (or skip)
        writer.println(labelF + ":");
        freeRegister(reg);

        previousNode.removeLast();
        return null;
    }

    @Override
    public Register visitIf(If i) {
        emitStart(i);
        previousNode.addLast(i);

        // GENERATE
        Register reg = i.expr.accept(this);
        String labelSkipElse = "$IF_STMT" + ifLabelCount;
        String labelSkipIf = "$ELSE" + ifLabelCount++;

        // If it is true skip ELSE stmt
        writer.println("\tbnez\t" + reg.toString() + ", " + labelSkipElse);
        freeRegister(reg);
        // else start
        if (i.stmtElse != null) {
            i.stmtElse.accept(this);
        }
        writer.println("\tj\t" + labelSkipIf);
        // else finish
        // if stmt
        writer.println(labelSkipElse + ":");
        i.stmt.accept(this);
        // if stmt end
        writer.println(labelSkipIf + ":");

        // EXIT
        previousNode.removeLast();
        emitEnd(i);
        return null;
    }

    @Override
    public Register visitExprStmt(ExprStmt es) {
        previousNode.addLast(es);

        Register reg = es.expr.accept(this);
        if (reg != null)
            freeRegister(reg);

        previousNode.removeLast();
        return null;
    }

    @Override
    public Register visitAssign(Assign a) {
        emitStart(a);
        previousNode.addLast(a);

        // GENERATE
        Register fromReg = a.assign_from.accept(this);
        Register toReg = a.assign_to.accept(this);
        // a.assign_to can be Var, FieldAccess, ArrayAccess, ValueAt
        // find addresability $WORD /$WORDC / $BYTE / $struct (to and from have same addressability)
        String addressability = a.assign_to.accept(AF);
        if (isByteAddressable(addressability)){
            // STORE BYTE
            writer.println("\t# BYTE STORING");
            writer.println("\tsb\t" + fromReg + ", 0(" + toReg + ")");
        }
        else if (isWordAddressable(addressability)){
            // STORE WORD
            writer.println("\t# WORD STORING");
            writer.println("\tsw\t" + fromReg + ", 0(" + toReg + ")");
        }
        else {
            // IT IS A STRUCT
            // GO THROUGH struct.size / 4 and STORE WORDS
            writer.println("\t# STRUCT STORING");
            int os = 0;
            // addressability will return the name of the struct
            int size = structs.get(addressability).byteSize;
            while (os < size){
                Register transferReg = getRegister();
                writer.println("\tlw\t" + transferReg + ", " + os + "(" + fromReg.toString() + ")");
                writer.println("\tsw\t" + transferReg + ", " + os + "(" + toReg.toString() + ")");
                os += 4;
                freeRegister(transferReg);
            }
        }

        freeRegister(fromReg);
        freeRegister(toReg);
        previousNode.removeLast();
        emitEnd(a);
        return null;
    }

    @Override
    public Register visitReturn(Return r) {
        writer.println("\tj\t" + curFD.name + "_epilogue");
        return null;
    }

    // LITERALS
    // --------------------------------------------------------------------------
    @Override
    public Register visitIntLiteral(IntLiteral il) {
        Register reg = getRegister();
        writer.println("\tli\t" + reg.toString() + ", " + il.value);
        return reg;
    }
    @Override
    public Register visitChrLiteral(ChrLiteral cl) {
        Register reg = getRegister();
        writer.println("\tli\t" + reg.toString() + ", " + formatChar(cl.value));
        return reg;
    }
    @Override
    public Register visitStrLiteral(StrLiteral sl) {
        return sl.varExpr.accept(this);
    }

    // VALUE ANALYSIS
    // --------------------------------------------------------------------------
    private Register valueAnalysis(Register addr, ASTNode valueNode){

        // We know if we need the value depending on the previous node and position
        ASTNode prevNode = previousNode.getLast();

        if(requireValue(prevNode, valueNode)){
            //Register value = getRegister();
            String addressability = valueNode.accept(AF);
           // System.out.println(valueNode.toString());
            //System.out.println("IN VALUE ANALYSIS: " + addressability);
            if (isByteAddressable(addressability)){
                //                     was value
                writer.println("\tlb\t" + addr.toString() + ", 0(" + addr.toString() + ")");
                //freeRegister(addr);
                emitEnd(valueNode);
                return addr;
            }
            if (isWordAddressable(addressability)){
                //                     was value
                writer.println("\tlw\t" + addr.toString() + ", 0(" + addr.toString() + ") # Val Analysis");
                //freeRegister(addr);
                emitEnd(valueNode);
                return addr;
            }
        }

        // EXIT
        emitEnd(valueNode);
        return addr;
    }
    // Check if we need the value of the expression or the address
    private boolean requireValue(ASTNode prevNode, ASTNode curNode){

        String addressability;
        // Check assign
        if(prevNode.getClass() == Assign.class){
            if(((Assign)prevNode).assign_to == curNode) return false;

            // here we are at assign_from
            // get addressability of assign_to
            addressability = ((Assign)prevNode).assign_to.accept(AF);
            // System.out.println(addressability);
            if (!isStructAddressable(addressability) && !isPointerAddressable(addressability))
                return true;

            // check pointer to pointer assignment
            if (isPointerAddressable(addressability)){
                addressability = curNode.accept(AF);
                //   System.out.println("Pointer to " + addressability);
                if(isPointerAddressable(addressability))
                    return true;
                return false;
            }
            else return false;
        }

        addressability = curNode.accept(AF);
        if(prevNode.getClass() == BinOp.class)
            return true;
        if(prevNode.getClass() == TypecastExpr.class && isBTCharAddressable(addressability))
            return true;


        if(prevNode.getClass() == FunCallExpr.class){
            if(((FunCallExpr)prevNode).fd.name.equals("print_i"))return true;
            if(((FunCallExpr)prevNode).fd.name.equals("print_c"))return true;
            if(((FunCallExpr)prevNode).fd.name.equals("mcmalloc"))return true;
        }
        if(prevNode.getClass() == While.class)
            return true;
        if(prevNode.getClass() == If.class)
            return true;

        //  System.out.println(prevNode.toString() + "( " + curNode + ")");
        // System.out.println("IN HERE WITH ADDRESSABILITY : " + addressability);
        return false;
    }


    // EMITTING
    // --------------------------------------------------------------------------
    private void emitStart(ASTNode node){ writer.println("# " + node + " : start"); }
    private void emitEnd(ASTNode node){ writer.println("# " + node + " : end"); }
    private void emitStartJump(){
        writer.println(".text");
        writer.println("\tj\tmain");
    }
    private void emitMainEntry () {

        // SAFE GUARD
        writer.println("main: ");
        writer.println("\t# GUARD");
        writer.println("\taddiu\t$sp, $sp, " + (-curFD.returnVar.byteSize));
        writer.println("\tlw\t$0, 0($sp)");
        writer.println("\tmove\t$a0, $sp");
        writer.println("\t# GUARD END");
        // END OF SAFE GUARD

        emitFunctionEntry(curFD);
    }
    private void emitMainEpilogue(){
        if(curFD.returnVar.type != BaseType.INT){
            writer.println("main_epilogue:");
            writer.println("\tli\t$a0, 0");
            writer.println("\tli\t$v0, 17");
            writer.println("\tsyscall");
        } else {
            writer.println("main_epilogue:");
            writer.println("\tlw\t$a0, " + curFD.frameSize + "($fp)");
            writer.println("\tli\t$v0, 10");
            writer.println("\tsyscall");
        }
    }
    private void emitFunctionEntry(FunDecl fd){
        // entrance label
        if(!fd.name.equals("main") )
        writer.println(fd.name + ": ");
        // move sp
        writer.println("\taddiu\t$sp, $sp, " + (-fd.frameSize));
        // store address of ret value
        writer.println("\tsw\t$a0, " + (fd.frameSize - (fd.argumentsByteSize + 4)) + "($sp)");
        // store ra                                                         // offset it by 8 (for retOffs and RA)
        writer.println("\tsw\t$ra, " + (fd.frameSize - (fd.argumentsByteSize + 8)) + "($sp)");
        // store old fp
        writer.println("\tsw\t$fp, " + fd.variablesByteSize + "($sp)");
        // fp = sp
        writer.println("\tmove\t$fp, $sp");
    }
    private void emitFunctionEpilogue(FunDecl fd){
        // exit label
        writer.println(fd.name + "_epilogue:");
        // sp = fp
        writer.println("\tmove\t$sp, $fp");
        // load $ra
        writer.println("\tlw\t$ra, " + (fd.variablesByteSize + 4) + "($fp)");
        // load OLD_FP
        writer.println("\tlw\t$fp, " + (fd.variablesByteSize + 0) + "($fp)");
        // move back sp
        writer.println("\taddiu\t$sp, $sp, " + fd.frameSize);
        // Jump to RA
        writer.println("\tjr\t$ra");
    }

    // Minic-std.lib
    // -------------
    private Register emitPrint_i(FunCallExpr fce){
        previousNode.addLast(fce);
        Register printReg = fce.arguments.get(0).accept(this);
        previousNode.removeLast();
        writer.println("\tli\t$v0, 1");
        writer.println("\taddu\t$a0, $0, " + printReg.toString());
        freeRegister(printReg);
        writer.println("\tsyscall");
        return null;
    }
    private Register emitPrint_c(FunCallExpr fce){
        previousNode.addLast(fce);
        Register printReg = fce.arguments.get(0).accept(this);
        previousNode.removeLast();
        writer.println("\tli\t$v0, 11");
        writer.println("\tadd\t$a0, $0, " + printReg.toString());
        freeRegister(printReg);
        writer.println("\tsyscall");
        return null;
    }
    private Register emitPrint_s(FunCallExpr fce){
        previousNode.addLast(fce);
        Register printReg = fce.arguments.get(0).accept(this);
        previousNode.removeLast();
        writer.println("\tli\t$v0, 4");
        writer.println("\taddu\t$a0, $0, " + printReg.toString());
        freeRegister(printReg);
        writer.println("\tsyscall");

        return null;
    }
    private Register emitRead_i(FunCallExpr fce){
        writer.println("\tli\t$v0, 5");
        writer.println("\tsyscall");
        Register readReg = getRegister();
        writer.println("\taddu\t" + readReg.toString() + ", $0, $v0");
        return readReg;
    }
    private Register emitRead_c(FunCallExpr fce){
        writer.println("\tli\t$v0, 12");
        writer.println("\tsyscall");
        Register readReg = getRegister();
        writer.println("\taddu\t" + readReg.toString() + ", $0, $v0");
        return readReg;
    }
    private Register emitMcmalloc(FunCallExpr fce){
        previousNode.addLast(fce);
        Register argReg = fce.arguments.get(0).accept(this);
        previousNode.removeLast();
        writer.println("\tli\t$v0, 9");
        writer.println("\taddu\t$a0, $0, " + argReg.toString());
        writer.println("\tsyscall");

        // srbk retrieves an address
        writer.println("\taddu\t" + argReg.toString() + ", $0, $v0");
        return argReg;
    }

    // UTILITIES
    // --------------------------------------------------------------------------
    // Formats a character
    private String formatChar(char c){
        String formatString = "\'";
        if (c == '\n') formatString = formatString + "\\n";
        else if (c == '\t') formatString = formatString + "\\t";
        else if (c == '\b') formatString = formatString + "\\b";
        else if (c == '\r') formatString = formatString + "\\r";
        else if (c == '\f') formatString = formatString + "\\f";
        else formatString = formatString + Character.toString(c);

        formatString = formatString + "\'";
        return formatString;
    }


    // Find addressability
    // --------------------

    // chck $STRUCT_NAME
    private boolean isStructAddressable(String addressability){
        if (!isByteAddressable(addressability) && !isWordAddressable(addressability))return true;
        return false;
    }
    // check everything of 4 BYTES
    private boolean isWordAddressable(String addressability){
        if(isBTIntAddressable(addressability)) return true;
        if(isBTCharAddressable(addressability)) return true;
        if(isPointerAddressable(addressability)) return true;
        return false;
    }
    // check $POINTER
    private boolean isPointerAddressable(String addressability){
        return addressability.equals("$POINTER");
    }
    // check $WORD
    private boolean isBTIntAddressable(String addressability){
        return addressability.equals("$WORD");
    }
    // check $WORDC
    private boolean isBTCharAddressable(String addressability){
        return addressability.equals("$WORDC");
    }
    // check $BYTE
    private boolean isByteAddressable(String addressability){
        return addressability.equals("$BYTE");
    }

    // Logging
    // -------
    private void printTables(){
        if(isLogged){
            RET.printTable();
            FCT.printTable();
            CAT.printTable();
            AT.printTable();
        }
    }

    // UNIMPLEMENTED
    // --------------------------------------------------------------------------
    @Override
    public Register visitStructTypeDecl(StructTypeDecl st) { return null; }
    @Override
    public Register visitVarDecl(VarDecl vd) { return null; }
    @Override
    public Register visitExpr(Expr e) { return null; }
    @Override
    public Register visitOp(Op o){ return null; }
    @Override
    public Register visitStmt(Stmt stmt) { return null; }
    @Override
    public Register visitType(Type t){ return null; }
    @Override
    public Register visitBaseType(BaseType bt) { return null; }
    @Override
    public Register visitPointerType(PointerType pt){ return null; }
    @Override
    public Register visitStructType(StructType stype){ return null; }
    @Override
    public Register visitArrayType(ArrayType at){ return null; }

}
