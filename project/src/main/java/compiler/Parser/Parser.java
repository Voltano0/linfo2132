package compiler.Parser;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Parser.ProgramNode;
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
                return parseVariableDeclaration();
            default:
                throw new IOException("Unexpected token: " + currentSymbol.getType());
        }
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
        // Implement expression parsing based on operator precedence and associativity
        // This is a placeholder implementation
        return new LiteralNode(currentSymbol.getValue());
    }
}