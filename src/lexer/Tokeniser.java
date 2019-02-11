package lexer;

import lexer.Token.TokenClass;

import java.io.EOFException;
import java.io.IOException;

/**
 * @author cdubach
 */
public class Tokeniser {

    private Scanner scanner;

    private int error = 0;
    public int getErrorCount() {
	return this.error;
    }

    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
    }

    private void error(char c, int line, int col) {
        System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
	error++;
    }


    public Token nextToken() {
        Token result;
        try {
             result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }

    /*
     * To be completed
     */
    private Token next() throws IOException {
        int line = scanner.getLine();
        int column = scanner.getColumn();

        // get the next character
        char c = scanner.next();

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        // line comment or multi-line comment or DIV token
        if (c == '/'){
            char c2;
            // Check for EOF
            try {
                c2 = scanner.peek();
            } catch (EOFException e){
                // Found DIV token at EOF
                //error(c,line,column);
                return new Token(TokenClass.DIV, line, column);
            }
            // Check for Line comment
            if (c2 == '/'){
                scanner.next();
                return skipLineComment(c, line, column);
            }
            // Check for Multi-line comment
            if (c2 == '*'){
                scanner.next();
                return skipMultiLineComment (c, line, column);
            }

            // If we reached here than it is a DIV token and in middle of file;
            if (c2 != '/'  && c2 != '*')
            return new Token(TokenClass.DIV, line, column);
        }
        // LITERALS
        // ---------------------------
        // recognises INT_LITERAL
        if (Character.isDigit(c)){
            return recogniseINT_LITERAL(c, line ,column);
        }
        // recognises CHAR_LITERAL
        if ( c == '\''){
            return recogniseCHAR_LITERAL(c, line, column);
        }

        // recognises STRING_LITERAL
        if ( c == '\"'){
            return recogniseSTRING_LITERAL(c, line, column);
        }

        // IDENTIFIERS, TYPES, KEYWORDS
        // ----------------------------
        // recognises IDENTIFIER/TYPE/KEYWORD tokens
        if (Character.isLetter(c) || c =='_')
            return recogniseIDorTYPEorKEYWORD(c, line, column);

        // INCLUDE
        // -------
        // recognises the INCLUDE token
        if (c == '#')
            return recogniseINCLUDE (c, line, column);

        // OPERATORS
        // ---------
        // recognises the PLUS operator
        if (c == '+')
            return new Token(TokenClass.PLUS, line, column);

        // recognises the MINUS operator
        if (c == '-')
            return new Token(TokenClass.MINUS, line, column);

        // recognises the ASTERIX operator
        if (c == '*')
            return new Token(TokenClass.ASTERIX, line, column);

        // recognises the DIV operator
        // TODO: REMINDER THIS HAS TO BE AFTER analysis for COMMENTS
        //if (c == '/')
        //    return new Token(TokenClass.DIV, line, column);

        // recognises the REM operator
        if (c == '%')
            return new Token(TokenClass.REM, line, column);

        // DELIMITERS
        // ----------
        // recognises the LBRA delimiter
        if (c == '{')
            return new Token(TokenClass.LBRA, line, column);

        // recognises the RBRA delimiter
        if (c == '}')
            return new Token(TokenClass.RBRA, line, column);

        // recognises the LPAR delimiter
        if (c == '(')
            return new Token(TokenClass.LPAR, line, column);

        // recognises the RPAR delimiter
        if (c == ')')
            return new Token(TokenClass.RPAR, line, column);

        // recognises the LSBR delimiter
        if (c == '[')
            return new Token(TokenClass.LSBR, line, column);

        // recognises the RSBR delimiter
        if (c == ']')
            return new Token(TokenClass.RSBR, line, column);

        // recognises the SC delimiter
        if (c == ';')
            return new Token(TokenClass.SC, line, column);

        // recognises the COMMA delimiter
        if (c == ',')
            return new Token(TokenClass.COMMA, line, column);

        // STRUCT MEMBER ACCESS
        // --------------------
        // recognises the DOT struct member access
        if (c == '.')
            return new Token(TokenClass.DOT, line, column);

        // LOGICAL OPERATORS
        // -----------------
        // recognises the AND logical operator
        if (c == '&')
            return recogniseAND(c, line, column);
        // recognises the OR logical operator
        if (c == '|')
            return recogniseOR(c, line, column);

        // COMPARISONS && ASSIGN
        // ---------------------
        // recognises the EQ comparison and ASSIGN token
        if (c == '=')
            return recogniseEQandASSIGN(c, line, column);

        // recognises the NE comparison
        if (c == '!')
            return recogniseNE(c, line, column);

        // recognises the LT and LE comparisons
        if (c == '<')
            return recogniseLTandLE(c, line, column);

        // recognises the GT and GE comparisons
        if (c == '>')
            return recogniseGTandGE(c, line, column);

        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }

    private Token recogniseAND(char startChar, int line, int column) throws IOException {
        char c;
        // Catch EOF exception
        try {
            c = scanner.peek();
        } catch (EOFException e) {
            // No token & defined in token class
            error(startChar, line, column);
            return new Token(TokenClass.INVALID, line, column);
        }
        if (c == '&') {
            // Found AND token
            scanner.next();                                         // if character of token found move forward
            return new Token(TokenClass.AND, line, column);
        }

        // No token & defined in token class
        error(startChar, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }
    private Token recogniseOR(char startChar, int line, int column) throws IOException {
        char c;
        // Catch EOF exception
        try {
            c = scanner.peek();
        } catch (EOFException e) {
            // No token | defined in token class
            error(startChar, line, column);
            return new Token(TokenClass.INVALID, line, column);
        }
        if (c == '|') {
            // Found OR token
            scanner.next();                                         // if character of token found move forward
            return new Token(TokenClass.OR, line, column);
        }
        // No token | defined in token class
        error(startChar, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }
    private Token recogniseNE(char startChar, int line, int column) throws IOException {
        char c;
        // Catch EOF exception
        try {
            c = scanner.peek();
        } catch (EOFException e) {
            // No token ! defined in token class
            error(startChar, line, column);
            return new Token(TokenClass.INVALID, line, column);
        }
        if (c == '=') {
            // Found NE token
            scanner.next();                                         // if character of token found move forward
            return new Token(TokenClass.NE, line, column);
        }
        // No token ! defined in token class
        error(startChar, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }
    private Token recogniseEQandASSIGN(char startChar, int line, int column) throws IOException {
        char c;
        // Catch EOF exception
        try {
            c = scanner.peek();
        } catch (EOFException e) {
            // Found ASSIGN token at EOF
            return new Token(TokenClass.ASSIGN, line, column);
        }
        if (c == '=') {
            // Found EQ token
            scanner.next();                                         // if character of token found move forward
            return new Token(TokenClass.EQ, line, column);
        }
        // Found ASSIGN token
        return new Token(TokenClass.ASSIGN, line, column);
    }
    private Token recogniseLTandLE(char startChar, int line, int column) throws IOException {
        char c;
        // Catch EOF exception
        try {
            c = scanner.peek();
        } catch (EOFException e) {
            // Found LT token at EOF
            return new Token(TokenClass.LT, line, column);
        }
        if (c == '=') {
            // Found LE token
            scanner.next();                                         // if character of token found move forward
            return new Token(TokenClass.LE, line, column);
        }
        // Found LT token
        return new Token(TokenClass.LT, line, column);
    }
    private Token recogniseGTandGE(char startChar, int line, int column) throws IOException {
        char c;
        // catch EOF exception
        try {
            c = scanner.peek();
        } catch (EOFException e) {
            // Found GT token at EOF
            return new Token(TokenClass.GT, line, column);
        }
        if (c == '=') {
            // Found GE token
            scanner.next();                                         // if character of token found move forward
            return new Token(TokenClass.GE, line, column);
        }
        // Found GT token
        return new Token(TokenClass.GT, line, column);
    }
    private Token recogniseINT_LITERAL(char startChar, int line, int column) throws IOException {
        // Build a string and add initial digit
        char c = startChar;
        StringBuilder data = new StringBuilder();
        data.append(c);
        // Search for more digits
        try {
            c = scanner.peek();
        } catch (EOFException e) {
            // Found INT_LITERAL at EOF
            return new Token(TokenClass.INT_LITERAL, data.toString(), line, column);
        }
        while (Character.isDigit(c)) {
            // Add to string and forward scanner
            data.append(c);
            scanner.next();
            // Search for more digits
            try {
                c = scanner.peek();
            } catch (EOFException e) {
                // Found INT_LITERAL at EOF
                return new Token(TokenClass.INT_LITERAL, data.toString(), line, column);
            }
        }
        // Reach here if peeked a non-digit character in mid of file
        return new Token(TokenClass.INT_LITERAL, data.toString(), line, column);
    }
    private Token recogniseCHAR_LITERAL(char startChar, int line, int column) throws IOException {
        StringBuilder data = new StringBuilder();
        boolean flagInvalid = false;
        char c;
        do {
            try {
                c = scanner.peek();
            } catch (EOFException e) {
                // Found unfinished CHAR_LITERAL at EOF
                error(startChar, line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
            if(c == '\n'){
                // Found new line that break continuity of CHAR_LITERAL token
                error(startChar, line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
            if(c == '\\'){         // Testing for escape character
                scanner.next();
                char escapeChar;
                try {
                    escapeChar = scanner.peek();
                } catch (EOFException e){
                    // Found unfinished CHAR_LITERAL at EOF
                    error(startChar, line, column);
                    return new Token(TokenClass.INVALID, line, column);
                }
                // Test escape character
                if (escapeChar == 't')      {data.append('\t');scanner.next();}
                else if (escapeChar == 'b') {data.append('\b');scanner.next();}
                else if (escapeChar == 'n') {data.append('\n');scanner.next();}
                else if (escapeChar == 'r') {data.append('\r');scanner.next();}
                else if (escapeChar == 'f') {data.append('\f');scanner.next();}
                else if (escapeChar == '\\') {data.append('\\');scanner.next();}
                else if (escapeChar == '\'') {data.append('\'');scanner.next();}
                else if (escapeChar == '\"') {data.append('\"');scanner.next();}
                else if (escapeChar == '\0') {data.append('\0');scanner.next();}
                else { data.append(escapeChar);scanner.next();flagInvalid = true;} // if it is invalid escape char mark token as INVALID
            }else if (c != '\'') {  // Append any other that is not QUOTE or BACKSLASH or ENDLINE
                data.append(c);
                scanner.next();
            }
        }while(c != '\'');
        // If here we found end of CHAR_LITERAL
        if (data.length() > 1 || flagInvalid || data.length() == 0){
            // Found INVALID char_literal token (too long, invalid escape char or empty)
            error(c, line, column);
            return new Token(TokenClass.INVALID, line, column);
        }
        // If we reached here it is a valid CHAR_LITERAL token
        scanner.next();
        return new Token(TokenClass.CHAR_LITERAL, data.toString(), line, column);
    }
    private Token recogniseSTRING_LITERAL(char startChar, int line, int column) throws IOException {
        StringBuilder data = new StringBuilder();
        boolean flagInvalid = false;
        char c;
        do {
            try {
                c = scanner.peek();
            } catch (EOFException e) {
                // Found unfinished STRING_LITERAL at EOF
                error(startChar, line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
            if(c == '\n'){
                // Found new line that break continuity of STRING_LITERAL token
                error(startChar, line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
            if(c == '\\'){         // Testing for escape character;
                scanner.next();
                char escapeChar;
                try {
                    escapeChar = scanner.peek();
                } catch (EOFException e){
                    // Found unfinished STRING_LITERAL at EOF
                    error(c, line, column);
                    return new Token(TokenClass.INVALID, line, column);
                }
                // Test escape characters
                if (escapeChar == 't') {data.append('\t');scanner.next();}
                else if (escapeChar == 'b') {data.append('\b');scanner.next();}
                else if (escapeChar == 'n') {data.append('\n');scanner.next();}
                else if (escapeChar == 'r') {data.append('\r');scanner.next();}
                else if (escapeChar == 'f') {data.append('\f');scanner.next();}
                else if (escapeChar == '\\') {data.append('\\');scanner.next();}
                else if (escapeChar == '\'') {data.append('\'');scanner.next();}
                else if (escapeChar == '\"') {data.append('\"');scanner.next();}
                else if (escapeChar == '\0') {data.append('\0');scanner.next();}
                else { data.append(escapeChar);scanner.next();flagInvalid = true;} // if it is invalid escape char mark token as INVALID
            } else if (c != '\"') { // Append any other that is not DOUBLE_QUOTE or BACKSLASH or ENDLINE
                data.append(c);
                scanner.next();
            }
        }while(c != '\"');
        // If here we found end of STRING_LITERAL
        if (flagInvalid){
            // Found INVALID string_literal token ( invalid escape char inside)
            error(c, line, column);
            return new Token(TokenClass.INVALID, line, column);
        }
        // If we reached here it is a valid STRING_LITERAL token
        scanner.next();
        return new Token(TokenClass.STRING_LITERAL, data.toString(), line, column);
    }
    private Token recogniseIDorTYPEorKEYWORD (char startChar, int line, int column) throws IOException {
        // Build a string and add initial letter
        char c = startChar;
        StringBuilder data = new StringBuilder();
        data.append(c);
        // Search for more letters/digits
        try {
            c = scanner.peek();
        } catch (EOFException e) {
            // Found IDENTIFIER at EOF
            return checkTokenITK(data, line, column);
        }
        while (Character.isLetterOrDigit(c) || c == '_') {
            // Add to string and forward scanner
            data.append(c);
            scanner.next();
            // Search for more letters/digits
            try {
                c = scanner.peek();
            } catch (EOFException e) {
                // Found IDENTIFIER at EOF
                return checkTokenITK(data, line, column);
            }
        }
        // Reach here if peeked a non-digit/non-letter character in mid of file
        return checkTokenITK(data, line, column);
    }
    private Token recogniseINCLUDE (char startChar, int line, int column) throws IOException {
        // Build a string and add initial '#'
        StringBuilder data = new StringBuilder();
        char c = startChar;
        data.append(c);
        try {
            c = scanner.peek();
        } catch (EOFException e) {
            // Found unfinished INCLUDE token at EOF
            error(startChar, line, column);
            return new Token(TokenClass.INVALID, line, column);
        }
        while (!(Character.isWhitespace(c) || c == '\"')) {
            data.append(c);
            scanner.next();
            try {
                c = scanner.peek();
            } catch (EOFException e) {
                // Found unfinished INCLUDE token at EOF
                error(startChar, line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
        }
        // Check if we have exactly "#include" and not anything else like "#incluDE"
        if (data.toString().equals("#include"))
            return new Token(TokenClass.INCLUDE, line, column);

        // If we reach here we have an INVALID token
        error (startChar, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }
    private Token checkTokenITK(StringBuilder sb, int line, int column){
        String data = sb.toString();
        if (data.equals("int"))
            return new Token(TokenClass.INT, line, column);
        if (data.equals("void"))
            return new Token(TokenClass.VOID, line, column);
        if (data.equals("char"))
            return new Token(TokenClass.CHAR, line, column);
        if (data.equals("if"))
            return new Token(TokenClass.IF, line, column);
        if (data.equals("else"))
            return new Token(TokenClass.ELSE, line, column);
        if (data.equals("while"))
            return new Token(TokenClass.WHILE, line, column);
        if (data.equals("return"))
            return new Token(TokenClass.RETURN, line, column);
        if (data.equals("struct"))
            return new Token(TokenClass.STRUCT, line, column);
        if (data.equals("sizeof"))
            return new Token(TokenClass.SIZEOF, line, column);
        return new Token (TokenClass.IDENTIFIER, data, line, column);
    }
    private Token skipLineComment (char startChar, int line, int column) throws IOException{
        // Line comment => Read everything until EOL or EOF
        char c;
        do {
            // Check for EOF
            try {
                c = scanner.peek();
            } catch (EOFException e){
                // Return EOF token
                return new Token(TokenClass.EOF, line, column);
            }
            scanner.next();
        } while (c != '\n');
        // Successfully skipped the line comment and return next token
        return next();
    }
    private Token skipMultiLineComment (char startChar, int line, int column) throws IOException {
        // WE ARE AT /*
        boolean isFinished = false;
        char c;
        while (!isFinished){
            // Peek and Check for EOF
            try {
                c = scanner.peek();
            } catch (EOFException e){
                // Unfinished multi-line comment
                error(startChar, line, column);
                isFinished = true;
                return new Token(TokenClass.INVALID, line, column);
            }
            scanner.next();
            // Check for possible end of multi-line comment
            if (c == '*'){
                // Check for EOF
                try {
                    c = scanner.peek();
                } catch (EOFException e){
                    // Unfinished multi-line comment ending at '*'
                    error(startChar, line, column);
                    return new Token(TokenClass.INVALID, line, column);
                }
                // Check for end of multi-line comment search for a token
                if (c == '/'){
                    scanner.next();
                    // Successfully skipped multi-line comment => EXIT loop
                    isFinished = true;
                }
            }
        }
        return next();
    }
}
