package gen;
import ast.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AccessTable {

    private Map<Expr,Integer> exprMap = new HashMap<>();

    public void addAccess (Expr accessExpr, Integer i){
        exprMap.put(accessExpr,i);
    }
    public Integer getOffset (Expr accessExpr){
        Integer i = exprMap.get(accessExpr);
        if (i == null) {
            System.out.println("ERROR: AccessTable cannot find access expr: " + accessExpr.toString());
            return 0;
        }
        return i;
    }
    public void printTable(){
        Map<Expr, Integer> copyTable = exprMap;
        System.out.println("############################################################");
        System.out.println("#                PRINTING ACCESS TABLE:                    #");
        System.out.println();
        Iterator it = copyTable.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("(Key = " + pair.getKey().toString() +
                    " : Item Offset = " + pair.getValue());
        }
        System.out.println();
        System.out.println("############################################################");

    }
    public void cleanTable(){
        exprMap.clear();
    }
}
