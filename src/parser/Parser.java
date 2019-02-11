package parser;

import ast.*;

import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * @author cdubach
 * Secondary author: civet
 */
public class Parser {

    private boolean isLogged = false;
    private int logDepth = 0;
    private void logIt(int dI,String str){
        if (isLogged){
            if (dI == 1){
                for(int i = 0; i< logDepth; i++)
                    System.out.print("|");
                System.out.println(str);
                logDepth++;
            }else if (dI == -1) {
                logDepth--;
                for(int i = 0; i< logDepth; i++)
                    System.out.print("|");
                System.out.println(str);
            }else if (dI == 0) {
                for(int i = 0; i< logDepth; i++)
                    System.out.print("|");
                System.out.print("consume: ");
                System.out.println(str);
            }
        }
    }
    private Token token;

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;



    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
    }

    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TokenClass e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        if(isLogged){
            for(int i = 0; i< logDepth; i++)
                System.out.print("| ");
            System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);
        } else System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);

        error++;
        lastErrorToken = token;
    }

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());
        assert buffer.size() >= i;

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return null;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (token != null && isLogged)
            logIt(0, token.toString());
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(TokenClass... expected) {
        for (TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }

        error(expected);
        return null;
    }

    /*
     * Returns true if the current token is equals to any of the expected ones.
     */
    private boolean accept(TokenClass... expected) {
        boolean result = false;
        for (TokenClass e : expected)
            result |= (e == token.tokenClass);
        return result;
    }

    private Token exToken;
    // PROGRAM PARSING
    // -------------------------------------------------------------------------------------------------------
    private Program parseProgram() {
        parseIncludes();
        List<StructTypeDecl> stds   = parseStructDecls();
        List<VarDecl> vds           = parseVarDecls();
        List<FunDecl> fds           = parseFunDecls();
        expect(TokenClass.EOF);
        return new Program(stds, vds, fds);
    }



    // INCLUDE PARSING
    // -------------------------------------------------------------------------------------------------------
    // includes are ignored, so does not need to return an AST node
    // includeRep ::= include includeRep | .
    // include    ::= "#include" STRING_LITERAL
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            parseInclude();
            parseIncludes();
        }
    }
    private void parseInclude() {
        expect(TokenClass.INCLUDE);
        expect(TokenClass.STRING_LITERAL);
    }



    // STRUCT DECLARATION
    // -----------------------------------------------------------------------------------------------------------------
    // StructDeclRep ::= StructDecl StructDeclRep | .
    private List<StructTypeDecl> parseStructDecls() {
        List<StructTypeDecl> stds = new LinkedList<>();
        while (isStructTypeDecl())
            stds.add(parseStructDecl());
        return stds;
    }

    // StructDecl ::= StructType "{" VarDecl VarDeclRep "}" ";"
    private StructTypeDecl parseStructDecl() {
        logIt(1, "P: STRUCT DECLARATION");
        // initialise items
        List<VarDecl> varDecls = new LinkedList<>();
        List<VarDecl> secondaryVarDecls = new LinkedList<>();
        StructType structType = new StructType("ERROR");
        // Get StructType
        structType = parseStructType();
        // Get "{"
        expect(TokenClass.LBRA);
        // Get VarDecls
        varDecls.add(parseVarDecl());
        varDecls.addAll(parseVarDecls());
        // Get "{" ";"
        expect(TokenClass.RBRA);
        expect(TokenClass.SC);
        logIt(-1, "F: STRUCT DECLARATION");
        return new StructTypeDecl(structType,varDecls);
    }

    // structtype ::= "struct" IDENT
    private StructType parseStructType() {
        exToken = expect(TokenClass.STRUCT);
        exToken = expect(TokenClass.IDENTIFIER);
        if(exToken != null)
            return new StructType(exToken.data);

        // If no IDENTIFIER found return structType of ERROR;
        return new StructType("ERROR");
    }

    // Accept and lookahead function
    private boolean isStructTypeDecl(){
        if (!accept(TokenClass.STRUCT)) {                               // Check for STRUCT token
            return false;
        }
        if (lookAhead(2) == null) {                                     // Needed LOOKAHEAD of 2 to detect "{"
            return false;
        }
        if (lookAhead(2).tokenClass == TokenClass.LBRA){                // Check for "{"
            return true;
        }
        return false;
    }



    // VAR DECLARATION
    // -----------------------------------------------------------------------------------------------------------------
    // VardeclRep ::= Vardecl VardeclRep | .
    private List<VarDecl> parseVarDecls() {
        List<VarDecl> vds = new LinkedList<>();
        VarDecl vd;
        while (isVarDecl())
            vds.add(parseVarDecl());
        return vds;
    }

    // Vardecl    ::= Type IDENT ";"                       # normal declaration, e.g. int a;
    //              | Type IDENT "[" INT_LITERAL "]" ";"   # array declaration, e.g. int a[2];
    private VarDecl parseVarDecl() {
        logIt(1, "P: VAR DECLARATION");
        // initialise items
        Type type;
        String varName;

        // Get Type
        type = parseType();
        if (type == null) {
            logIt(-1, "F: VAR DECLARATION");
            return new VarDecl(new StructType("ERROR"), "ERROR");
        }

        // Get Name
        exToken = expect(TokenClass.IDENTIFIER);
        if (exToken == null) {
            logIt(-1, "F: VAR DECLARATION");
            return new VarDecl(type, "ERROR");
        }
        varName = exToken.data;

        // Check for normal declaration
        if (accept(TokenClass.SC)){
            nextToken();
            logIt(-1, "F: VAR DECLARATION");
            return new VarDecl(type,varName);
        }

        // Check for array declaration
        if (accept(TokenClass.LSBR)){
            nextToken();

            // Get Length
            exToken = expect(TokenClass.INT_LITERAL);
            if (exToken == null) {
                logIt(-1, "F: VAR DECLARATION");
                return new VarDecl(type, "ERROR");
            }
            int length = Integer.parseInt(exToken.data);

            // Check for "]" ";"
            expect(TokenClass.RSBR);
            expect(TokenClass.SC);

            ArrayType arrayType = new ArrayType(type, length);
            logIt(-1, "F: VAR DECLARATION");
            return new VarDecl(arrayType,varName);
        }

        // If it reaches here it is failed parsing for VarDecl
        error(TokenClass.SC, TokenClass.LSBR);
        logIt(-1, "F: VAR DECLARATION");
        return new VarDecl(type, "ERROR");
    }

    // Accept and lookahead function
    private boolean isVarDecl(){
        if(!accept(TokenClass.STRUCT, TokenClass.INT, TokenClass.CHAR, TokenClass.VOID))
            return false;
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Token lookAheadToken = lookAhead(2);
        if (lookAheadToken == null){
            return false;
        }
        if (lookAheadToken.tokenClass == TokenClass.LSBR ||     // check if 3rd is LSBR or SC
                lookAheadToken.tokenClass == TokenClass.SC) {   // e.g BaseType IDENT (";" | "[")
            return true;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        lookAheadToken = lookAhead(3);
        if (lookAheadToken == null){
            return false;
        }
        if (lookAheadToken.tokenClass == TokenClass.LSBR ||     // check if 4th is LSBR or SC
                lookAheadToken.tokenClass == TokenClass.SC){    // e.g (StructType | BaseType "*") IDENT (";" | "[")
            return true;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        lookAheadToken = lookAhead(4);
        if (lookAheadToken == null){
            return false;
        }
        if (lookAheadToken.tokenClass == TokenClass.LSBR ||     // check if 5th is LSBR or SC
                lookAheadToken.tokenClass == TokenClass.SC){    // e.g StructType "*" IDENT (";" | "[")
            return true;
        }
        return false;
    }



    // FUNCTION DECLARATION
    // -----------------------------------------------------------------------------------------------------------------
    // FundeclRep ::= Fundecl FundeclRep | .
    private List<FunDecl> parseFunDecls(){
        List<FunDecl> fds = new LinkedList<>();
        while (isFuncDecl())
            fds.add(parseFunDecl());
        return fds;
    }

    // Fundecl    ::= Type IDENT "(" Params ")" Block
    private FunDecl parseFunDecl() {
        logIt(1, "P: FUN DECLARATION");
        // initialise items
        Type type;
        String name;
        List<VarDecl> params = new LinkedList<>();
        Block block = new Block(new LinkedList<VarDecl>(), new LinkedList<Stmt>());

        // Get Type
        type = parseType();

        // Get Name
        exToken = expect(TokenClass.IDENTIFIER);
        if (exToken == null) {
            logIt(-1, "F: FUN DECLARATION");
            return new FunDecl(type, "ERROR", params, block);
        }
        name = exToken.data;

        // Get Params
        expect(TokenClass.LPAR);
        params.addAll (parseParams());
        expect(TokenClass.RPAR);

        // Get Block
        block = parseBlock();

        logIt(-1, "F: FUN DECLARATION");
        return new FunDecl(type, name, params, block);
    }

    // Accept and lookahead gunction
    private boolean isFuncDecl(){
        if(!accept(TokenClass.STRUCT, TokenClass.INT, TokenClass.CHAR, TokenClass.VOID))
            return false;
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Token lookAheadToken = lookAhead(2);
        if (lookAheadToken == null){
            return false;
        }
        if (lookAheadToken.tokenClass == TokenClass.LPAR) {   // check if 3rd is LPAR
            return true;                                        // e.g BaseType IDENT "(" ...
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        lookAheadToken = lookAhead(3);
        if (lookAheadToken == null){
            return false;
        }
        if (lookAheadToken.tokenClass == TokenClass.LPAR){      // check if 4th is LPAR
            return true;                                        // e.g (StructType | BaseType "*") IDENT "(" ...
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        lookAheadToken = lookAhead(4);
        if (lookAheadToken == null){
            return false;
        }
        if (lookAheadToken.tokenClass == TokenClass.LPAR){      // check if 5th is LPAR
            return true;                                        // e.g StructType "*" IDENT "(" ...
        }

        return false;
    }



    // PARAMS DECLARATION
    // -----------------------------------------------------------------------------------------------------------------
    // params         ::= type IDENT paramsListRep | .
    private List<VarDecl> parseParams() /*EPSILON POSSIBLE */ {
        logIt(1, "P: PARAMS");
        // initialise items
        List<VarDecl> vds = new LinkedList<>();
        VarDecl vd = null;

        // Check for first VarDecl
        vd = parseParamVarDecl();
        if (vd == null) {                       // Error detected in parsing a VarDecl so just return empty list
            logIt(-1, "F: PARAMS");
            return vds;
        }
        vds.add(vd);
        // Check for more VarDecl
        while (isParamsRep()){
            vd = parseParamVarDecl();
            if (vd == null) {                   // Error detected in parsing a VarDecl so just return current list
                logIt(-1, "F: PARAMS");
                return vds;
            }
            vds.add(vd);
        }

        // Return the params
        logIt(-1, "F: PARAMS");
        return vds;
    }

    // Parsing VarDecl inside Func Params
    private VarDecl parseParamVarDecl(){
        if (accept(TokenClass.INT) || accept(TokenClass.CHAR) || accept(TokenClass.VOID) || accept(TokenClass.STRUCT)) {
            // Get Type
            Type type = parseType();
            // Get Name
            Token exToken = expect(TokenClass.IDENTIFIER);
            if (exToken == null)
                return null;
            String varName = exToken.data;

            return new VarDecl(type, varName);
        }
        return null;
    }

    // Accept test for ParamsRep
    private boolean isParamsRep(){
        if (accept(TokenClass.COMMA)){
            expect(TokenClass.COMMA);
            return true;
        }
        return false;
    }



    // TYPES PARSING
    // -----------------------------------------------------------------------------------------------------------------
    // type  ::= "int" asterixOpt
    //         | "char" asterixOpt
    //         | "void" asterixOpt
    //         | structtype asterixOpt
    private Type parseType() {
        logIt(1, "P: TYPE");
        // initilise items
        Type type = new StructType("ERROR");

        // Check for BaseType
        if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID)){
            exToken = expect(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID);
            if (exToken.tokenClass == TokenClass.INT)
                type =  BaseType.INT;
            if (exToken.tokenClass == TokenClass.CHAR)
                type = BaseType.CHAR;
            if (exToken.tokenClass == TokenClass.VOID)
                type = BaseType.VOID;

            // Check for StructType
        } else if (accept(TokenClass.STRUCT)) {
            type = parseStructType();

            // Else its an ERROR
        } else {
            error(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT);
            logIt(-1, "F: TYPE");
            return type;
        }

        // If reached here we have a Type or from StructType a Struct("ERROR")
        // Check for PointerType
        //    asterixOpt ::= "*" | .
        if(accept(TokenClass.ASTERIX)) {
            expect(TokenClass.ASTERIX);
            type = new PointerType(type);
        }

        logIt(-1, "F: TYPE");
        return type;
    }



    // BLOCK DECLARATION
    // ---------------------------------------------------------------------------------------------------------
//    block      ::= "{" vardeclRep stmtRep "}"
    private Block parseBlock(){
        logIt(1, "P: BLOCK");
        // initialise items
        List<VarDecl> vds = new LinkedList<>();
        List<Stmt> stmts = new LinkedList<>();

        // Get "}"
        expect(TokenClass.LBRA);
        // Get VarDecls
        vds.addAll(parseVarDecls());
        // Get Stmts
        stmts.addAll(parseStatementRep());
        // Get "}"
        expect(TokenClass.RBRA);

        logIt(-1, "F: BLOCK");
        return new Block(vds,stmts);
    }

    // STATEMENT DECLARATION
    // ----------------------------------------------------------------------------------------------------------
    // stmtRep     ::= stmt stmtRep | .
    // stmt        ::= "if" "(" exp ")" stmt elseStmtOpt      # if then else      parseIfStmt
    //               | "while" "(" exp ")" stmt               # while             parseWhileStmt
    //               | "return" exptOpt ";"                   # return            parseReturnStmt
    //               | exp "=" exp ";"                        # assignment        parseAssignmentstmt
    //               | exp ";"                                # expression        parseExprStmt
    //               | block                                  # block             parseBlock
    // elseStmtOpt ::= "else" stmt | .                                            (inside parseIf)
    private List<Stmt> parseStatementRep(){
        List<Stmt> stmts = new LinkedList<>();
        Stmt stmt = null;
        while (isStatement()) {
            stmt = parseStatement();
            stmts.add(stmt);
        }
        return stmts;
    }

    // Accept function for Statement
    private boolean isStatement(){
        return accept(TokenClass.IF,          // IF statements OR If with else statements
                TokenClass.WHILE,                   // WHILE statements
                TokenClass.RETURN,                  // RETURN statements
                TokenClass.LBRA,                    // BLOCK statements
                TokenClass.LPAR,                    // Possible expressions: typecast, nesting
                TokenClass.MINUS,                   // Possible expressions: unary/binary minus
                TokenClass.IDENTIFIER,              // Possible exxpressions: funcall, factor
                TokenClass.INT_LITERAL,             // Factor
                TokenClass.CHAR_LITERAL,            // Factor
                TokenClass.STRING_LITERAL,          // Factor
                TokenClass.ASTERIX,                 // Possible expressions: multiply, valueat
                TokenClass.SIZEOF                   // sizeof
        );
    }

    private Stmt parseStatement() {
        Stmt stmt = null;
        logIt(1, "P: STATEMENT");
        if(accept(TokenClass.IF)){
            stmt = parseIfStmt();
        }else if(accept(TokenClass.WHILE)){
            stmt = parseWhileStmt();
        }else if(accept(TokenClass.RETURN)){
            stmt = parseReturnStmt();
        }else if(accept(TokenClass.LBRA)){
            stmt = parseBlock();
        }else {
            Expr expr_right = null;
            expr_right = parseExp();
            if (accept(TokenClass.ASSIGN)){     // ASSIGNMENT STATEMENT
                Expr expr_left = null;
                logIt(1, "P: ASSIGNMENT STATEMENT");
                nextToken();
                expr_left = parseExp();
                expect(TokenClass.SC);
                stmt = new Assign(expr_right,expr_left);
                logIt(-1, "F: ASSIGNMENT STATEMENT");
            }
            // Switched expr_left with expr_rigth because they showed in AST in wrong possitions :)
            else {         // SIMPLE EXPRESSION
                expect(TokenClass.SC);
                logIt(1, "P: EXPRESSION STATEMENT");
                //nextToken();
                stmt = new ExprStmt(expr_right);
                logIt(-1, "F: EXPRESSION STATEMENT");
            }

        }
        logIt(-1, "F: STATEMENT");
        return stmt;
    }

    // "if" "(" exp ")" stmt elseStmtOpt
    // elseStmtOpt ::= "else" stmt | .
    private If parseIfStmt() /* NOT NULL */ {
        logIt(1,"P:: IF STATEMENT");
        // initialise values
        Expr expr;
        Stmt stmt;
        Stmt elseStmt = null;

        // Get "IF" "("
        expect(TokenClass.IF);
        expect(TokenClass.LPAR);

        // Get Expr
        expr = parseExp();

        // Get ")"
        expect(TokenClass.RPAR);

        // Get Stmt
        stmt = parseStatement();

        // Get optional elseStmt
        if (accept(TokenClass.ELSE)){
            expect(TokenClass.ELSE);
            elseStmt = parseStatement();
        }

        logIt(-1, "F: IF STATEMENT");
        return new If(expr,stmt,elseStmt);
    }
    private Stmt parseElseStmtOpt(){
        Stmt stmt = null;
        if (accept(TokenClass.ELSE)){
            logIt(1, "P: ELSE OPT");
            expect(TokenClass.ELSE);
            stmt = parseStatement();

            logIt(-1, "F: ELSE OPT");
            return stmt;
        }
        logIt(0, "ELSE STATEMENT == null");
        return null;
    }

    // "while" "(" exp ")" stmt
    private While parseWhileStmt()/* NOT NULL */ {
        logIt(1, "P: WHILE");
        // initialise items
        Expr expr = null;
        Stmt stmt = null;

        // Get "WHILE" "("
        expect(TokenClass.WHILE);
        expect(TokenClass.LPAR);

        // Get Expr
        expr = parseExp();

        // Get ")"
        expect(TokenClass.RPAR);

        // Get Stmt
        stmt = parseStatement();

        logIt(-1, "F: WHILE");
        return new While(expr,stmt);
    }

    // "return" exptOpt ";"
    private Return parseReturnStmt()/* NOT NULL*/ {
        logIt(1, "P: RETURN");
        // initialise items
        Expr expr = null;
        // Get "RETURN"
        expect(TokenClass.RETURN);
        // Get Expr
        if (!accept(TokenClass.SC))
            expr = parseExpOpt();
        // Get ";"
        expect(TokenClass.SC);
        logIt(-1, "F: RETURN");
        return new Return(expr);
    }




    // EXPRESSION DECLARATION
    // -----------------------------------------------------------------------------------------------------------------
    // expOpt ::= exp | .
    private Expr parseExpOpt(){
        logIt(1,"P: OPT EXP");
        Expr expr = parseExp();
        logIt(-1, "F: OPT EXP");
        return expr;
    }

    // exp ::= orExp;
    private Expr parseExp(){
        return parseOrExp();
    }

    // Level 8
    // orExp   ::= andExp orExp'
    // orExp'  ::= "||" andExp orExp'      | .
    private Expr parseOrExp(){
        Expr lhs_expr = parseAndExp();
        return parseOrExpPrime(lhs_expr);
    }
    private Expr parseOrExpPrime(Expr lhs_expr){
        if(accept(TokenClass.OR)){
            nextToken();
            Expr rhs_expr = parseAndExp();
            return parseOrExpPrime(new BinOp(lhs_expr, Op.OR, rhs_expr));
        }
        return lhs_expr;
    }

    // Level 7
    // andExp  ::= eqExp andExp'
    // andExp' ::= "&&" eqExp andExp'      | .
    private Expr parseAndExp(){
        Expr lhs_expr = parseEqExp();
        return parseAndExpPrime(lhs_expr);
    }
    private Expr parseAndExpPrime(Expr lhs_expr){
        if(accept(TokenClass.AND)){
            nextToken();
            Expr rhs_expr = parseEqExp();
            return parseAndExpPrime(new BinOp(lhs_expr,Op.AND,rhs_expr));
        }
        return lhs_expr;
    }

    // Level 6
    // eqExp   ::= relExp eqExp'
    // eqExp'  ::= eqOP relExp eqExp'      | .
    // eqOp    ::= "!=" | "==
    private Expr parseEqExp(){
        Expr lhs_expr = parseRelExp();
        return parseEqExpPrime(lhs_expr);
    }
    private Expr parseEqExpPrime(Expr lhs_expr){
        if(accept(TokenClass.EQ)){
            nextToken();
            Expr rhs_expr = parseRelExp();
            return parseEqExpPrime(new BinOp(lhs_expr, Op.EQ, rhs_expr));
        }
        if(accept(TokenClass.NE)){
            nextToken();
            Expr rhs_expr = parseRelExp();
            return parseEqExpPrime(new BinOp(lhs_expr,Op.NE, rhs_expr));
        }
        return lhs_expr;
    }

    // Level 5
    // relExp  ::= addExp relExp'
    // relExp' ::= relOP addExp relExp'    | .
    // relOp   :: "<=" | "<" | ">" | "=>"
    private Expr parseRelExp(){
        Expr lhs_expr = parseAddExp();
        return parseRelExpPrime(lhs_expr);
    }
    private Expr parseRelExpPrime(Expr lhs_expr){
        if(accept(TokenClass.LE)){
            nextToken();
            Expr rhs_expr = parseAddExp();
            return parseRelExpPrime(new BinOp(lhs_expr,Op.LE,rhs_expr));
        }
        if(accept(TokenClass.LT)){
            nextToken();
            Expr rhs_expr = parseAddExp();
            return parseRelExpPrime(new BinOp(lhs_expr,Op.LT,rhs_expr));
        }
        if(accept(TokenClass.GE)){
            nextToken();
            Expr rhs_expr = parseAddExp();
            return parseRelExpPrime(new BinOp(lhs_expr,Op.GE,rhs_expr));
        }
        if(accept(TokenClass.GT)){
            nextToken();
            Expr rhs_expr = parseAddExp();
            return parseRelExpPrime(new BinOp(lhs_expr,Op.GT,rhs_expr));
        }
        return lhs_expr;
    }

    // Level 4
    // addExp  ::= mulExp addExp'
    // addExp' ::= addOp mulExp addExp'    | .
    // addOp   ::= "+" | "-"
    private Expr parseAddExp(){
        //logIt(1, "P: MUL EXP");
        Expr lhs_expr = parseMulExp();
        //logIt(-1, "F: MUL EXP");
        return parseAddExpPrime(lhs_expr);
    }
    private Expr parseAddExpPrime(Expr lhs_expr){
        if(accept(TokenClass.PLUS)){
            nextToken();
            Expr rhs_expr = parseMulExp();
            return parseAddExpPrime(new BinOp(lhs_expr,Op.ADD, rhs_expr));
        }
        if(accept(TokenClass.MINUS)){
            nextToken();
            Expr rhs_expr = parseMulExp();
            return parseAddExpPrime(new BinOp(lhs_expr,Op.SUB, rhs_expr));
        }
        return lhs_expr;
    }

    // Level 3
    // mulExp  ::= unaryExp mulExp'
    // mulExp  ::= mulOP unaryExp mulExp'  | .
    // mulOp   ::= "*" | "/" | "%"
    private Expr parseMulExp(){
        //logIt(1, "P: UNARY EXP");
        Expr lhs_expr = parseUnaryExp();
        //logIt(1, "P: UNARY EXP");
        return parseMulExpPrime(lhs_expr);
    }
    private Expr parseMulExpPrime(Expr lhs_expr){
        if (accept(TokenClass.ASTERIX)){
            nextToken();
            Expr rhs_expr = parseUnaryExp();
            return parseMulExpPrime(new BinOp(lhs_expr,Op.MUL,rhs_expr));
        }
        if (accept(TokenClass.REM)){
            nextToken();
            Expr rhs_expr = parseUnaryExp();
            return parseMulExpPrime(new BinOp(lhs_expr,Op.MOD,rhs_expr));
        }
        if (accept(TokenClass.DIV)){
            nextToken();
            Expr rhs_expr = parseUnaryExp();
            return parseMulExpPrime(new BinOp(lhs_expr,Op.DIV,rhs_expr));
        }
        return lhs_expr;
    }



    // Level 2
    // -----------------------------------------------------------------------------------------------------------------
    // unaryExp ::= "(" type ")" Exp                 // typecast
    //            | "sizeof" "(" type ")"            // sizeof
    //            | "*" Exp                          // valueat
    //            | "-" Exp                          // unary minus
    //            | accessExp                        // lower level expr

    private Expr parseUnaryExp(){
        if(accept(TokenClass.SIZEOF))
            return parseSizeof();
        if(accept(TokenClass.ASTERIX))
            return parseValueAt();
        if(accept(TokenClass.MINUS))
            return parseUnaryMinus();

        // Check for Typecast -> Possible for lower level: Nested expr...
        if(accept(TokenClass.LPAR))
            if (lookAhead(1) != null)
                if (isTypeStart(lookAhead(1).tokenClass))
                    return parseTypecast();

        // Else other expr from lower lvls
        return parseAccessExp();
    }
    private Expr parseTypecast(){
        logIt(1, "P: TYPECAST");
        expect(TokenClass.LPAR);
        Type type = parseType();
        expect(TokenClass.RPAR);
        Expr expr = parseUnaryExp();
        logIt(-1,"F: TYPECAST");
        return new TypecastExpr(type, expr);
    }
    private Expr parseSizeof(){
        logIt(1,"P: SIZE OF");
        expect(TokenClass.SIZEOF);
        expect(TokenClass.LPAR);
        Type type = parseType();
        expect(TokenClass.RPAR);
        logIt(-1,"F: SIZE OF");
        return new SizeOfExpr(type);
    }
    private Expr parseValueAt(){
        logIt(1, "P: VALUE AT");
        expect(TokenClass.ASTERIX);
        Expr expr = parseUnaryExp();
        logIt(-1, "F: VALUE AT");
        return new ValueAtExpr(expr);
    }
    private Expr parseUnaryMinus(){
        logIt(1, "P: UNARY MINUS");
        expect(TokenClass.MINUS);
        Expr expr = parseUnaryExp();
        logIt(-1, "F: UNARY MINUS");
        return new BinOp(new IntLiteral(0), Op.SUB, expr);      // -expr can be represented as (0 - Expr)
    }



    // Level 1:
    // -----------------------------------------------------------------------------------------------------------------
    //    accessExp ::= IDENT "(" funcallArgsList ")"  accessExp' // funcall
    //                | factor accessExp'
    //
    private Expr parseAccessExp(){
        if(accept(TokenClass.IDENTIFIER)) {
            Token lookAheadToken = lookAhead(1);
            if (lookAheadToken != null) {
                if (lookAheadToken.tokenClass == TokenClass.LPAR) {
                    Expr expr = parseFunCall();
                    return parseAccessExpPrime(expr);
                }else {
                    //Expr expr = parseFactor();      // It is an IDENTIFIER
                    exToken = expect(TokenClass.IDENTIFIER);
                    if (exToken == null)
                        return new VarExpr("ERROR");
                    VarExpr var = new VarExpr(exToken.data);
                    return parseAccessExpPrime(var);
                }
            }
        } else if(accept(TokenClass.INT_LITERAL,
                TokenClass.CHAR_LITERAL,
                TokenClass.STRING_LITERAL,
                TokenClass.LPAR)){
            Expr expr = parseFactor();
            return parseAccessExpPrime(expr);
        } else error(TokenClass.IDENTIFIER,TokenClass.INT_LITERAL,TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.LPAR);
        return new VarExpr("ERROR");
    }

    //    accessExp'::= "[" exp "]" accessExp'    // arrayaccess
    //                | "." IDENT accessExp'      // fieldaccess
    //                | .
    private Expr parseAccessExpPrime(Expr expr){
        logIt(1,"P: ACCESS PRIME");
        if(accept(TokenClass.LSBR)){
            return parseArrayAccess(expr);
        }
        if(accept(TokenClass.DOT)){
            return parseFieldAccess(expr);
        }
        logIt(-1, "F: ACCESS PRIME");
        return expr;
    }

    //    "[" exp "]" accessExp'    // arrayaccess
    private Expr parseArrayAccess(Expr array){
        logIt(1,"P: ARRAY ACCESS");
        expect(TokenClass.LSBR);
        Expr index = parseExp();
        expect(TokenClass.RSBR);
        logIt(-1,"F: ARRAY ACCESS");
        return parseAccessExpPrime(new ArrayAccessExpr(array,index));
    }

    //    "." IDENT accessExp'      // fieldaccess
    private Expr parseFieldAccess(Expr structure){
        logIt(1,"P: FIELD ACCESS");
        expect(TokenClass.DOT);

        // Get Field Name
        exToken = expect(TokenClass.IDENTIFIER);
        if (exToken == null)
            return new FieldAccessExpr(structure, "ERROR");
        String fieldname = exToken.data;

        logIt(-1,"F: FIELD ACCESS");
        return parseAccessExpPrime(new FieldAccessExpr(structure,fieldname));
    }




    //    IDENT "(" funcallArgsList ")"  // funcall
    private Expr parseFunCall(){
        List<Expr> args = new LinkedList<>();
        logIt(1,"P: FUNCALL----------------------------------------------------");
        exToken = expect(TokenClass.IDENTIFIER);
        String name = exToken.data;
        expect(TokenClass.LPAR);
        args.addAll(parseFunCallArgsList());
        expect(TokenClass.RPAR);
        logIt(-1, "F: FUNCALL");
        return new FunCallExpr(name,args);
    }
    //    funcallArgsList ::= exp funcallArgsListRep | .
    private List<Expr> parseFunCallArgsList(){
        List<Expr> args = new LinkedList<>();
//        if (accept(TokenClass.LPAR,                    // Possible expressions: typecast, nesting
//                TokenClass.MINUS,                   // Possible expressions: unary/binary minus
//                TokenClass.IDENTIFIER,              // Possible exxpressions: funcall, factor
//                TokenClass.INT_LITERAL,             // Factor
//                TokenClass.CHAR_LITERAL,            // Factor
//                TokenClass.STRING_LITERAL,          // Factor
//                TokenClass.ASTERIX,                 // Possible expressions: multiply, valueat
//                TokenClass.SIZEOF,                   // sizeof
//                TokenClass.DIV,
//                TokenClass.REM,
//                TokenClass.PLUS,
//                TokenClass.
//        )){
//                                                    logIt(1, "P: FUNCALL ARGS LIST");
//            Expr expr = parseExp();
//            if (expr == null) logIt(0, "NULL EXPR AT ARGSLIST");
//            args.add(expr); // TODO: THIS IS NULL PROBLEM
//            args.addAll(parseFunCallArgsListRep());
//                                                    logIt( -1, "F: FUNCALL ARGS LIST");
//            return args;
//        }
//        return args;
        if (!accept(TokenClass.RPAR)){
            Expr expr = parseExp();
            args.add(expr);
            args.addAll(parseFunCallArgsListRep());
        }
        return args;
    }
    //    funcallArgsListRep ::= "," exp funcallArgsListRep| .
    private List<Expr> parseFunCallArgsListRep(){
        List<Expr> args = new LinkedList<>();
        if (accept(TokenClass.RBRA)) return args;
        if (accept(TokenClass.COMMA)){
            //logIt(1, "P: FUNCALL ARGS LIST (rep)");
            expect(TokenClass.COMMA);
            Expr expr = parseExp();
            args.add(expr);
            args.addAll(parseFunCallArgsListRep());
            //logIt(-1, "F: FUNCALL ARGS LIST (rep)");
        }
        return args;
    }




    // Level 0:
    // -----------------------------------------------------------------------------------------------------------------
    // factor     ::= IDENTIFIER | INT_LITERAL | CHAR_LITERAL | STRING_LITERAL
    //              | "(" exp ")"
    private Expr parseFactor(){
        // initialise items
        Expr expr = new VarExpr("ERROR");

        // Check for nested expressions
        if (accept(TokenClass.LPAR)){
            logIt(1,"P: NESTED EXP");
            expect(TokenClass.LPAR);
            expr = parseExp();
            expect(TokenClass.RPAR);
            logIt(-1,"F: NESTED EXP");
            return expr;

            // Check for a Factor
        } else {
            logIt(1,"P: FACTOR");
            exToken = expect(TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL);
            if (exToken == null) return expr;
            if(exToken.tokenClass == TokenClass.IDENTIFIER){
                expr = new VarExpr(exToken.data);
            }
            if(exToken.tokenClass == TokenClass.INT_LITERAL){
                expr = new IntLiteral(Integer.parseInt(exToken.data));
            }
            if(exToken.tokenClass == TokenClass.CHAR_LITERAL){
                expr = new ChrLiteral(exToken.data.charAt(0));
            }
            if(exToken.tokenClass == TokenClass.STRING_LITERAL){
                expr = new StrLiteral(exToken.data);
            }
            logIt(-1, "F: FACTOR");
            return expr;
        }
    }



    // UTILITIES
    // -----------------------------------------------------------------------------------------------------------------
    private boolean isTypeStart(TokenClass tc){
        if(tc == TokenClass.INT || tc == TokenClass.CHAR || tc == TokenClass.VOID || tc == TokenClass.STRUCT)
            return true;
        return false;
    }
    // END
}

