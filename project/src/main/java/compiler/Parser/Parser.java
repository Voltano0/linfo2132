package compiler.Parser;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private Lexer lexer;
    private Symbol currentSymbol;

    public Parser(Lexer lexer) throws IOException {
        this.lexer = lexer;
        this.currentSymbol = lexer.getNextSymbol();
    }

    private void advance() throws IOException {
        currentSymbol = lexer.getNextSymbol();
    }
    private void expect(Symbol.TokenType type) throws IOException {
        if (currentSymbol.getType() == Symbol.TokenType.EOF){
            return;
        }
        if (currentSymbol.getType() != type) {
            throw new IOException("Expected " + type + " but found " + currentSymbol.getType());
        }
        advance();
    }
    public ProgramNode parseProgram() throws IOException {
        List<ASTNode> statements = new ArrayList<>();
        while (currentSymbol.getType() != Symbol.TokenType.EOF) {
            statements.add(parseStatement());
        }
        return new ProgramNode(statements);
    }
    private ASTNode parseStatement() throws IOException {
        switch (currentSymbol.getType()) {
            case KEYWORD:
                return switch (currentSymbol.getValue()) {
                    case "final", "int", "bool", "float", "string" -> parseVariableDeclaration();
                    case "fun" -> parseFunctionDeclaration();
                    case "if" -> parseIfStatement();
                    case "while" -> parseWhileStatement();
                    case "for" -> parseForStatement();
                    default -> throw new IOException("Unexpected keyword: " + currentSymbol.getValue());
                };
            case IDENTIFIER:
                return parseAssignmentOrVariableDeclaration();
            case RECORD:
                return parseRecordDeclaration();
            default:
                throw new IOException("Unexpected token: " + currentSymbol.getType());
        }
    }

    private ASTNode parseAssignmentOrVariableDeclaration() throws IOException {
        String name = currentSymbol.getValue();
        advance();
        if (currentSymbol.getType() == Symbol.TokenType.OPERATOR) {
            String operator = currentSymbol.getValue();
            if (operator.equals("=")) {
                return parseVariableDeclaration();
            } else if (operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/")) {
                return parseAssignment(name);
            }
        }
        throw new IOException("Unexpected token: " + currentSymbol.getType());
    }
    private RecordDeclarationNode parseRecordDeclaration() throws IOException {
        String name = currentSymbol.getValue();
        expect(Symbol.TokenType.RECORD); //
        expect(Symbol.TokenType.KEYWORD); // Expect 'rec'
        expect(Symbol.TokenType.OPERATOR); // Expect '{'
        List<FieldNode> fields = new ArrayList<>();
        while (currentSymbol.getType() != Symbol.TokenType.OPERATOR || !currentSymbol.getValue().equals("}")) {
            String fieldName = currentSymbol.getValue();
            advance();
            String fieldType = currentSymbol.getValue();
            expect(Symbol.TokenType.KEYWORD);
            fields.add(new FieldNode(fieldName, fieldType));
            if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals(";")) {
                advance();
            }
        }
        expect(Symbol.TokenType.OPERATOR); // Expect '}'
        return new RecordDeclarationNode(name, fields);
    }
    private ASTNode parseAssignment(String name) throws IOException {
        String operator = currentSymbol.getValue();
        advance();
        ASTNode value = parseExpression();
        expect(Symbol.TokenType.EOL); // Expect ';'
        return new AssignmentNode(name, operator, value);
    }

    private VariableDeclarationNode parseVariableDeclaration() throws IOException {
        boolean isFinal = false;
        if (currentSymbol.getType() == Symbol.TokenType.KEYWORD && currentSymbol.getValue().equals("final")) {
            isFinal = true;
            advance();
        }
        String name = currentSymbol.getValue();
        expect(Symbol.TokenType.IDENTIFIER);

        String type = currentSymbol.getValue();
        expect(Symbol.TokenType.KEYWORD);
        expect(Symbol.TokenType.OPERATOR);// Expect '='
        ASTNode initializer = parseExpression();
        advance();
        expect(Symbol.TokenType.EOL); // Expect ';'

        return new VariableDeclarationNode(isFinal, type, name, initializer);
    }



    private FunctionDeclarationNode parseFunctionDeclaration() throws IOException {
        expect(Symbol.TokenType.KEYWORD); // Expect 'fun'
        String name = currentSymbol.getValue();
        expect(Symbol.TokenType.IDENTIFIER);
        expect(Symbol.TokenType.OPERATOR); // Expect '('
        List<ParameterNode> parameters = new ArrayList<>();
        while (currentSymbol.getType() != Symbol.TokenType.OPERATOR || !currentSymbol.getValue().equals(")")) {
            String paramType = currentSymbol.getValue();
            advance();
            String paramName = currentSymbol.getValue();
            expect(Symbol.TokenType.IDENTIFIER);
            parameters.add(new ParameterNode(paramType, paramName));
            if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals(",")) {
                advance();
            }
        }
        expect(Symbol.TokenType.OPERATOR); // Expect ')'
        String returnType = currentSymbol.getValue();
        advance();
        BlockNode body = parseBlock();
        return new FunctionDeclarationNode(name, parameters, returnType, body);
    }

    private BlockNode parseBlock() throws IOException {
        expect(Symbol.TokenType.OPERATOR); // Expect '{'
        List<ASTNode> statements = new ArrayList<>();
        while (currentSymbol.getType() != Symbol.TokenType.OPERATOR || !currentSymbol.getValue().equals("}")) {
            statements.add(parseStatement());
        }
        expect(Symbol.TokenType.OPERATOR); // Expect '}'
        return new BlockNode(statements);
    }

    private IfNode parseIfStatement() throws IOException {
        expect(Symbol.TokenType.KEYWORD); // Expect 'if'
        ASTNode condition = parseExpression();
        BlockNode thenBlock = parseBlock();
        BlockNode elseBlock = null;
        if (currentSymbol.getType() == Symbol.TokenType.KEYWORD && currentSymbol.getValue().equals("else")) {
            advance();
            elseBlock = parseBlock();
        }
        return new IfNode(condition, thenBlock, elseBlock);
    }

    private WhileNode parseWhileStatement() throws IOException {
        expect(Symbol.TokenType.KEYWORD); // Expect 'while'
        ASTNode condition = parseExpression();
        BlockNode body = parseBlock();
        return new WhileNode(condition, body);
    }

    private ForNode parseForStatement() throws IOException {
        expect(Symbol.TokenType.KEYWORD); // Expect 'for'
        ASTNode initializer = parseExpression();
        expect(Symbol.TokenType.OPERATOR); // Expect ','
        ASTNode condition = parseExpression();
        expect(Symbol.TokenType.OPERATOR); // Expect ','
        ASTNode increment = parseExpression();
        BlockNode body = parseBlock();
        return new ForNode(initializer, condition, increment, body);
    }

    private ASTNode parseExpression() throws IOException {
        return parseBinaryOperation();
    }


    private ASTNode parsePrimaryExpression() throws IOException {
        switch (currentSymbol.getType()) {
            case IDENTIFIER:
                String name = currentSymbol.getValue();
                advance();
                if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals("(")) {
                    // Function call
                    advance();
                    List<ASTNode> arguments = new ArrayList<>();
                    while (currentSymbol.getType() != Symbol.TokenType.OPERATOR || !currentSymbol.getValue().equals(")")) {
                        arguments.add(parseExpression());
                        if (currentSymbol.getType() == Symbol.TokenType.OPERATOR && currentSymbol.getValue().equals(",")) {
                            advance();
                        }
                    }
                    expect(Symbol.TokenType.OPERATOR); // Expect ')'
                    return new FunctionCallNode(name, arguments);
                } else {
                    return new IdentifierNode(name);
                }
            case INTEGER, FLOAT:
                Object value = currentSymbol.getValue();
                advance();
                return new LiteralNode(value);

            case OPERATOR:
                if (currentSymbol.getValue().equals("(")) {
                    advance();
                    ASTNode expr = parseExpression();
                    expect(Symbol.TokenType.OPERATOR); // Expect ')'
                    return expr;
                }
                String operator = currentSymbol.getValue();
                advance();
                ASTNode operand = parsePrimaryExpression();
                return new UnaryOperationNode(operator, operand);
            default:
                throw new IOException("Unexpected token: " + currentSymbol.getType());
        }
    }

    private ASTNode parseBinaryOperation() throws IOException {
        ASTNode left = parsePrimaryExpression();
        while (currentSymbol.getType() == Symbol.TokenType.OPERATOR) {
            String operator = currentSymbol.getValue();
            advance();
            ASTNode right = parsePrimaryExpression();
            left = new BinaryOperationNode(left, operator, right);
        }
        return left;
    }
}