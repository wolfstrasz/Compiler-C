package gen;
import ast.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.ListIterator;

public class AddressAnalyser extends BaseASTVisitor<Void> {
    PrintWriter writer;
    public void analyse (Program p, PrintWriter writer){
        this.writer = writer;
        writer.println("############################################################");

        p.accept(this);
    }

    @Override
    public Void visitProgram(Program p) {
        //System.out.println("############################################################");
        //System.out.println("#                      ADDRESS ANALYSER                    #");
        // Set global variables
        //System.out.println("___________________________GLOBALS__________________________");
        for(VarDecl vd : p.varDecls){
            vd.isGlobal = true;
            vd.offset = 0;
            vd.address = vd.varName;
            writer.println(vd.varName + ":\t.space " + Integer.toString(vd.byteSize));

            //System.out.print("\t\t\t\t\t\t" + "address: " + vd.offset + "(" + vd.address + ")");
            //System.out.print("\r\t\t\t\t\t" + vd.byteSize);
            //System.out.println("\r# " + vd.varName);

        }
        //System.out.println("___________________________LOCALS___________________________");
        for(FunDecl fd : p.funDecls)
            fd.accept(this);

        //System.out.println("############################################################");
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {
        //System.out.println();
        //System.out.println("FUNCTION: " + fd.name + " : frame size = " + fd.frameSize);
        int offset = fd.frameSize;

        // Set arguments addresses
        Iterator it = fd.params.listIterator(fd.params.size());
        while(((ListIterator) it).hasPrevious()){
            VarDecl vd = (VarDecl)((ListIterator) it).previous();
            offset -= vd.byteSize;
            vd.offset = offset;

            //System.out.print("\t\t\t\t\t\t" + "address: " + vd.offset + "(" + vd.address + ")");
            //System.out.print("\r\t\t\t\t\t" + vd.byteSize);
            //System.out.println("\r# " + vd.varName);
        }
        // Set arguments stack size in bytes
        fd.argumentsByteSize = fd.frameSize - offset;
        //System.out.println("FUNCTION: " + fd.name + " : arg size = " + fd.argumentsByteSize);

        // Set space for return value address to be stored
        offset -= 4;
        fd.retValAddress_Offset = offset;
        //System.out.print("\t\t\t\t\t\t" + "address: " + fd.retValAddress_Offset + "($fp)");
        //System.out.print("\r\t\t\t\t\t4");
        //System.out.println("\r# retValAddress");

        // Set space for the return address to be stored
        offset -= 4;
        fd.retAddr_Offset = offset;
        //System.out.print("\t\t\t\t\t\t" + "address: " + fd.retAddr_Offset + "($fp)");
        //System.out.print("\r\t\t\t\t\t4");
        //System.out.println("\r# retAddr");

        // Set space for the old FP value to be stored
        offset -= 4;
        fd.oldFP_Offset = offset;
        //System.out.print("\t\t\t\t\t\t" + "address: " + fd.oldFP_Offset + "($fp)");
        //System.out.print("\r\t\t\t\t\t4");
        //System.out.println("\r# oldFP");

        // Set all variables stack size in bytes
        fd.variablesByteSize = offset;
        //System.out.println("FUNCTION: " + fd.name + " : vars size = " + fd.variablesByteSize);

        // Set function call return expr addresses and then for the local var declarations
        it = fd.block.varDecls.listIterator(fd.block.varDecls.size());
        while(((ListIterator) it).hasPrevious()){
            VarDecl vd = (VarDecl)((ListIterator) it).previous();
            offset -= vd.byteSize;
            vd.offset = offset;

            //System.out.print("\t\t\t\t\t\t" + "address: " + vd.offset + "(" + vd.address + ")");
            //System.out.print("\r\t\t\t\t\t" + vd.byteSize);
            //System.out.println("\r# " + vd.varName);
        }
        //System.out.println();

        return null;
    }

}
