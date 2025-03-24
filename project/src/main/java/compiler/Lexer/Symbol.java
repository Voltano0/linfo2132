package compiler.Lexer;

public class Symbol {
    public enum TokenType {
        IDENTIFIER, KEYWORD, INTEGER, FLOAT, STRING, BOOLEAN,
        OPERATOR, PUNCTUATION, COMMENT, EOF, RECORD, EOL
    }

    private TokenType type;
    private String value;

    public Symbol(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + ": " + value;
    }
}
