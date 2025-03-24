package compiler.Parser;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private Lexer lexer;
    private Symbol currentSymbol;
    private Symbol nextSymbol;

    public Parser(Lexer lexer) throws IOException {
        this.lexer = lexer;
        // initialize lookahead tokens
        this.currentSymbol = lexer.getNextSymbol();
        this.nextSymbol = lexer.getNextSymbol();
    }

    // Advance one token (update currentSymbol and nextSymbol)
    private void advance() throws IOException {
        currentSymbol = nextSymbol;
        nextSymbol = lexer.getNextSymbol();
    }

    // Expect the current token to have the specified type
    private void expect(Symbol.TokenType type) throws IOException {
        if (currentSymbol.getType() == Symbol.TokenType.EOF) {
            return;
        }
        if (currentSymbol.getType() != type) {
            throw new IOException("Expected " + type + " but found " + currentSymbol.getType());
        }
        advance();
    }

    // Overloaded expect to check both type and exact value
    private void expect(Symbol.TokenType type, String value) throws IOException {
        if (currentSymbol.getType() == Symbol.TokenType.EOF) {
            return;
        }
        if (currentSymbol.getType() != type || !currentSymbol.getValue().equals(value)) {
            throw new IOException("Expected " + type + " with value '" + value + "' but found " +
                    currentSymbol.getType() + " with value '" + currentSymbol.getValue() + "'");
        }
        advance();
    }

    // Top-level parsing entry point: parse an entire program.
    public Program parseProgram() throws IOException {
        List<ASTNode> declarations = new ArrayList<>();
        while (currentSymbol.getType() != Symbol.TokenType.EOF) {
            declarations.add(parseTopLevelDeclaration());
        }
        return new Program(declarations);
    }

    // Parse one top-level declaration: constant, record definition, global variable, or function.
    private ASTNode parseTopLevelDeclaration() throws IOException {
        // Constant declaration: starts with keyword "final"
        if (currentSymbol.getType() == Symbol.TokenType.KEYWORD && currentSymbol.getValue().equals("final")) {
            return parseConstantDeclaration();
        }
        // Record definition: first token is a RECORD (e.g. "Point")
        if (currentSymbol.getType() == Symbol.TokenType.RECORD) {
            return parseRecordDefinition();
        }
        // Function declaration: starts with keyword "fun"
        if (currentSymbol.getType() == Symbol.TokenType.KEYWORD && currentSymbol.getValue().equals("fun")) {
            return parseFunctionDeclaration();
        }
        // Otherwise, assume a variable declaration (global variable)
        if (currentSymbol.getType() == Symbol.TokenType.IDENTIFIER) {
            return parseVariableDeclaration();
        }
        throw new IOException("Unexpected token at top-level: " + currentSymbol);
    }

    // Parse a constant declaration:
    //    final <identifier> <type> = <expression> ;
    private ASTNode parseConstantDeclaration() throws IOException {
        expect(Symbol.TokenType.KEYWORD, "final");
        String name = currentSymbol.getValue();
        expect(Symbol.TokenType.IDENTIFIER);
        ASTNode typeNode = parseType();
        expect(Symbol.TokenType.OPERATOR, "=");
        ASTNode expr = parseExpression();
        expect(Symbol.TokenType.EOL);
        return new ConstantDeclaration(name, typeNode, expr);
    }

    // Parse a record definition:
    //    <RecordName> rec { <field declarations> }
    private ASTNode parseRecordDefinition() throws IOException {
        String recordName = currentSymbol.getValue();
        expect(Symbol.TokenType.RECORD); // consume record type name
        expect(Symbol.TokenType.KEYWORD, "rec");
        expect(Symbol.TokenType.OPERATOR, "{");
        List<ASTNode> fields = new ArrayList<>();
        while (!(currentSymbol.getType() == Symbol.TokenType.OPERATOR &&
                currentSymbol.getValue().equals("}"))) {
            // Each field: <type> <identifier> ;
            ASTNode fieldType = parseType();
            String fieldName = currentSymbol.getValue();
            expect(Symbol.TokenType.IDENTIFIER);
            expect(Symbol.TokenType.EOL);
            fields.add(new FieldDeclaration(fieldName, fieldType));
        }
        expect(Symbol.TokenType.OPERATOR, "}");
        return new RecordDefinition(recordName, fields);
    }

    // Parse a variable declaration:
    //    <identifier> <type> [= <expression>] ;
    private ASTNode parseVariableDeclaration() throws IOException {
        String varName = currentSymbol.getValue();
        expect(Symbol.TokenType.IDENTIFIER);
        ASTNode typeNode = parseType();
        ASTNode initializer = null;
        if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("=")) {
            advance(); // consume '='
            initializer = parseExpression();
        }
        expect(Symbol.TokenType.EOL);
        return new VariableDeclaration(varName, typeNode, initializer);
    }

    // Parse a function declaration:
    //    fun <identifier> ( <parameter list> ) [<return type>] { <statements> }
    private ASTNode parseFunctionDeclaration() throws IOException {
        expect(Symbol.TokenType.KEYWORD, "fun");
        String functionName = currentSymbol.getValue();
        expect(Symbol.TokenType.IDENTIFIER);
        expect(Symbol.TokenType.OPERATOR, "(");
        List<ASTNode> parameters = parseParameterList();
        expect(Symbol.TokenType.OPERATOR, ")");
        // Optional return type: if the current token indicates a type, parse it.
        ASTNode returnType = null;
        if ((currentSymbol.getType() == Symbol.TokenType.KEYWORD &&
                (currentSymbol.getValue().equals("int") || currentSymbol.getValue().equals("float") ||
                        currentSymbol.getValue().equals("bool") || currentSymbol.getValue().equals("string"))) ||
                currentSymbol.getType() == Symbol.TokenType.RECORD) {
            returnType = parseType();
        }
        ASTNode body = parseBlock();
        return new FunctionDeclaration(functionName, parameters, returnType, body);
    }

    // Parse a parameter list: zero or more parameters separated by commas.
    private List<ASTNode> parseParameterList() throws IOException {
        List<ASTNode> parameters = new ArrayList<>();
        if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals(")")) {
            return parameters;
        }
        parameters.add(parseParameter());
        while (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals(",")) {
            advance(); // consume comma
            parameters.add(parseParameter());
        }
        return parameters;
    }

    // Parse a single parameter: <type> <identifier>
    private ASTNode parseParameter() throws IOException {
        ASTNode typeNode = parseType();
        String paramName = currentSymbol.getValue();
        expect(Symbol.TokenType.IDENTIFIER);
        return new Parameter(paramName, typeNode);
    }

    // Parse a block: { <statements> }
    private ASTNode parseBlock() throws IOException {
        expect(Symbol.TokenType.OPERATOR, "{");
        List<ASTNode> statements = new ArrayList<>();
        while (!(currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("}"))) {
            statements.add(parseStatement());
        }
        expect(Symbol.TokenType.OPERATOR, "}");
        return new Block(statements);
    }

    // Parse a statement. This method distinguishes control structures (if, while, for, return, free)
    // from variable declarations and assignments or expression statements.
    public ASTNode parseStatement() throws IOException {
        if (currentSymbol.getType() == Symbol.TokenType.KEYWORD) {
            String keyword = currentSymbol.getValue();
            switch (keyword) {
                case "if":
                    return parseIfStatement();
                case "while":
                    return parseWhileStatement();
                case "for":
                    return parseForStatement();
                case "return":
                    return parseReturnStatement();
                case "free":
                    return parseFreeStatement();
                default:
                    break;
            }
        }
        // Distinguish a local variable declaration from an assignment.
        if (currentSymbol.getType() == Symbol.TokenType.IDENTIFIER) {
            // If the lookahead token is a type indicator then it's a declaration.
            if ((nextSymbol.getType() == Symbol.TokenType.KEYWORD &&
                    (nextSymbol.getValue().equals("int") || nextSymbol.getValue().equals("float") ||
                            nextSymbol.getValue().equals("bool") || nextSymbol.getValue().equals("string"))) ||
                    nextSymbol.getType() == Symbol.TokenType.RECORD) {
                return parseVariableDeclaration();
            }
        }
        // Otherwise, treat as an assignment or expression statement.
        return parseAssignmentOrExpressionStatement();
    }

    // Parse an if statement: if ( <expression> ) <block> [else <block>]
    private ASTNode parseIfStatement() throws IOException {
        expect(Symbol.TokenType.KEYWORD, "if");
        expect(Symbol.TokenType.OPERATOR, "(");
        ASTNode condition = parseExpression();
        expect(Symbol.TokenType.OPERATOR, ")");
        ASTNode thenBlock = parseBlock();
        ASTNode elseBlock = null;
        if (currentSymbol.getType() == Symbol.TokenType.KEYWORD && currentSymbol.getValue().equals("else")) {
            advance(); // consume 'else'
            elseBlock = parseBlock();
        }
        return new IfStatement(condition, thenBlock, elseBlock);
    }

    // Parse a while statement: while ( <expression> ) <block>
    private ASTNode parseWhileStatement() throws IOException {
        expect(Symbol.TokenType.KEYWORD, "while");
        expect(Symbol.TokenType.OPERATOR, "(");
        ASTNode condition = parseExpression();
        expect(Symbol.TokenType.OPERATOR, ")");
        ASTNode block = parseBlock();
        return new WhileStatement(condition, block);
    }

    // Parse a for statement:
    //    for ( <identifier> , <expr> , <expr> , <expr> ) <block>
    private ASTNode parseForStatement() throws IOException {
        expect(Symbol.TokenType.KEYWORD, "for");
        expect(Symbol.TokenType.OPERATOR, "(");
        String iterator = currentSymbol.getValue();
        expect(Symbol.TokenType.IDENTIFIER);
        expect(Symbol.TokenType.OPERATOR, ",");
        ASTNode startExpr = parseExpression();
        expect(Symbol.TokenType.OPERATOR, ",");
        ASTNode endExpr = parseExpression();
        expect(Symbol.TokenType.OPERATOR, ",");
        ASTNode stepExpr = parseExpression();
        expect(Symbol.TokenType.OPERATOR, ")");
        ASTNode block = parseBlock();
        return new ForStatement(iterator, startExpr, endExpr, stepExpr, block);
    }

    // Parse a return statement: return [<expression>] ;
    private ASTNode parseReturnStatement() throws IOException {
        expect(Symbol.TokenType.KEYWORD, "return");
        ASTNode expr = null;
        if (!(currentSymbol.getType() == Symbol.TokenType.EOL)) {
            expr = parseExpression();
        }
        expect(Symbol.TokenType.EOL);
        return new ReturnStatement(expr);
    }

    // Parse a free statement: free <expression> ;
    private ASTNode parseFreeStatement() throws IOException {
        expect(Symbol.TokenType.KEYWORD, "free");
        ASTNode expr = parseExpression();
        expect(Symbol.TokenType.EOL);
        return new FreeStatement(expr);
    }

    // Parse either an assignment statement or a plain expression statement.
    // Assignment: <lvalue> = <expression> ;
    private ASTNode parseAssignmentOrExpressionStatement() throws IOException {
        ASTNode expr = parseExpression();
        if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("=")) {
            advance(); // consume '='
            ASTNode rightExpr = parseExpression();
            // Accept an EOL if present, otherwise if the next token is a valid statement starter or a block terminator, proceed.
            if (currentSymbol.getType() == Symbol.TokenType.EOL) {
                advance(); // consume EOL
            } else if (!(currentSymbol.getType() == Symbol.TokenType.OPERATOR &&
                    currentSymbol.getValue().equals("}")) &&
                    !(currentSymbol.getType() == Symbol.TokenType.KEYWORD ||
                            currentSymbol.getType() == Symbol.TokenType.IDENTIFIER ||
                            currentSymbol.getType() == Symbol.TokenType.RECORD ||
                            currentSymbol.getType() == Symbol.TokenType.BUILTIN)) {
                throw new IOException("Expected EOL but found " + currentSymbol);
            }
            return new AssignmentStatement(expr, rightExpr);
        } else {
            // For an expression statement, allow an optional EOL if the next token is a valid statement starter or a block terminator.
            if (currentSymbol.getType() == Symbol.TokenType.EOL) {
                advance(); // consume EOL
            } else if (!(currentSymbol.getType() == Symbol.TokenType.OPERATOR &&
                    currentSymbol.getValue().equals("}")) &&
                    !(currentSymbol.getType() == Symbol.TokenType.KEYWORD ||
                            currentSymbol.getType() == Symbol.TokenType.IDENTIFIER ||
                            currentSymbol.getType() == Symbol.TokenType.RECORD ||
                            currentSymbol.getType() == Symbol.TokenType.BUILTIN)) {
                throw new IOException("Expected EOL but found " + currentSymbol);
            }
            return new ExpressionStatement(expr);
        }
    }


    // Expression parsing: using recursive descent with precedence levels.
    private ASTNode parseExpression() throws IOException {
        return parseLogicalOr();
    }

    private ASTNode parseLogicalOr() throws IOException {
        ASTNode left = parseLogicalAnd();
        while (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("||")) {
            String op = currentSymbol.getValue();
            advance();
            ASTNode right = parseLogicalAnd();
            left = new BinaryExpression(op, left, right);
        }
        return left;
    }

    private ASTNode parseLogicalAnd() throws IOException {
        ASTNode left = parseEquality();
        while (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("&&")) {
            String op = currentSymbol.getValue();
            advance();
            ASTNode right = parseEquality();
            left = new BinaryExpression(op, left, right);
        }
        return left;
    }

    private ASTNode parseEquality() throws IOException {
        ASTNode left = parseRelational();
        while ((currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("=="))
                || (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("!")
                && nextSymbol.getType() == Symbol.TokenType.OPERATOR && nextSymbol.getValue().equals("="))) {
            String op;
            if (currentSymbol.getValue().equals("!")) {
                op = "!=";
                advance(); // consume '!'
                advance(); // consume '='
            } else {
                op = currentSymbol.getValue();
                advance();
            }
            ASTNode right = parseRelational();
            left = new BinaryExpression(op, left, right);
        }
        return left;
    }


    private ASTNode parseRelational() throws IOException {
        ASTNode left = parseAdditive();
        while (currentSymbol.getType() == Symbol.TokenType.OPERATOR &&
                (currentSymbol.getValue().equals("<") || currentSymbol.getValue().equals(">") ||
                        currentSymbol.getValue().equals("<=") || currentSymbol.getValue().equals(">="))) {
            String op = currentSymbol.getValue();
            advance();
            ASTNode right = parseAdditive();
            left = new BinaryExpression(op, left, right);
        }
        return left;
    }

    private ASTNode parseAdditive() throws IOException {
        ASTNode left = parseMultiplicative();
        while (currentSymbol.getType() == Symbol.TokenType.OPERATOR &&
                (currentSymbol.getValue().equals("+") || currentSymbol.getValue().equals("-"))) {
            String op = currentSymbol.getValue();
            advance();
            ASTNode right = parseMultiplicative();
            left = new BinaryExpression(op, left, right);
        }
        return left;
    }

    private ASTNode parseMultiplicative() throws IOException {
        ASTNode left = parseUnary();
        while (currentSymbol.getType() == Symbol.TokenType.OPERATOR &&
                (currentSymbol.getValue().equals("*") || currentSymbol.getValue().equals("/") ||
                        currentSymbol.getValue().equals("%"))) {
            String op = currentSymbol.getValue();
            advance();
            ASTNode right = parseUnary();
            left = new BinaryExpression(op, left, right);
        }
        return left;
    }

    private ASTNode parseUnary() throws IOException {
        if (currentSymbol.getType() == Symbol.TokenType.OPERATOR &&
                (currentSymbol.getValue().equals("-") || currentSymbol.getValue().equals("!"))) {
            String op = currentSymbol.getValue();
            advance();
            ASTNode expr = parseUnary();
            return new UnaryExpression(op, expr);
        }
        return parsePrimary();
    }

    // Parse primary expressions: literals, variable references, function or constructor calls,
    // and parenthesized expressions.
    private ASTNode parsePrimary() throws IOException {
        // Literals: integer, float, string, boolean.
        if (currentSymbol.getType() == Symbol.TokenType.INTEGER ||
                currentSymbol.getType() == Symbol.TokenType.FLOAT ||
                currentSymbol.getType() == Symbol.TokenType.STRING ||
                currentSymbol.getType() == Symbol.TokenType.BOOLEAN) {
            ASTNode literal = new LiteralExpression(currentSymbol.getValue());
            advance();
            return literal;
        }
        // Variable reference or function call.
        // Now also accepting BUILTIN tokens.
        if (currentSymbol.getType() == Symbol.TokenType.IDENTIFIER ||
                currentSymbol.getType() == Symbol.TokenType.BUILTIN) {
            String name = currentSymbol.getValue();
            advance();
            // Function call: identifier or builtin followed by "(".
            if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("(")) {
                advance(); // consume "("
                List<ASTNode> arguments = new ArrayList<>();
                if (!(currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals(")"))) {
                    arguments.add(parseExpression());
                    while (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals(",")) {
                        advance(); // consume ","
                        arguments.add(parseExpression());
                    }
                }
                expect(Symbol.TokenType.OPERATOR, ")");
                return new FunctionCallExpression(name, arguments);
            }
            return new VariableExpression(name);
        }
        // Record constructor call: a record name (token type RECORD) optionally followed by "(".
        if (currentSymbol.getType() == Symbol.TokenType.RECORD) {
            String recordName = currentSymbol.getValue();
            advance();
            // If the next token is an opening parenthesis, it's a constructor call.
            if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("(")) {
                advance(); // consume "("
                List<ASTNode> arguments = new ArrayList<>();
                if (!(currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals(")"))) {
                    arguments.add(parseExpression());
                    while (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals(",")) {
                        advance();
                        arguments.add(parseExpression());
                    }
                }
                expect(Symbol.TokenType.OPERATOR, ")");
                return new RecordConstructorExpression(recordName, arguments);
            } else {
                // Not a constructor call: treat it as a variable reference.
                return new VariableExpression(recordName);
            }
        }
        // Parenthesized expression.
        if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("(")) {
            advance(); // consume "("
            ASTNode expr = parseExpression();
            expect(Symbol.TokenType.OPERATOR, ")");
            return expr;
        }
        throw new IOException("Unexpected token in expression: " + currentSymbol);
    }


    // Parse a type: base types (int, float, bool, string) or record types,
    // with an optional array indicator "[]".
    private ASTNode parseType() throws IOException {
        String typeName = "";
        if (currentSymbol.getType() == Symbol.TokenType.KEYWORD) {
            typeName = currentSymbol.getValue();
            advance();
        } else if (currentSymbol.getType() == Symbol.TokenType.RECORD) {
            typeName = currentSymbol.getValue();
            advance();
        } else {
            throw new IOException("Expected a type but found: " + currentSymbol);
        }
        // Check for array type.
        if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("[")) {
            advance(); // consume '['
            expect(Symbol.TokenType.OPERATOR, "]");
            typeName += "[]";  // simple representation for an array type
        }
        return new TypeNode(typeName);
    }
}
