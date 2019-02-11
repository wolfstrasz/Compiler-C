package gen;
import ast.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CopyArgTable {
    private HashMap<FunCallExpr, List<Assign>> table = new HashMap<>();
    public void put(FunCallExpr fce, List<Assign> stmts){
        table.put(fce,stmts);
    }

    public List<Assign> get(FunCallExpr fce){
        return table.get(fce);
    }

    public void printTable(){
        Iterator it = table.entrySet().iterator();
        System.out.println("############################################################");
        System.out.println("#           PRINTING COPY ARGUMENT TABLE                   #");
        System.out.println();
        while(it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            System.out.println(me.getKey() + " : ");
            List<Assign> assignStmts = (List<Assign>)me.getValue();
            for (Assign a : assignStmts){
                VarExpr ve = (VarExpr)a.assign_to;
                System.out.println("\t Assigning to -> " + ve.name);
            }
        }
        System.out.println();
        System.out.println("############################################################");

    }
}
