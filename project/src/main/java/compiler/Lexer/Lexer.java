package compiler.Lexer;

import java.io.*;

public class Lexer {
    private Reader reader;
    private int currentChar;

    // Constructor
    public Lexer(Reader reader) throws IOException {
        this.reader = reader;
        this.currentChar = reader.read();
    }

    // Advance to the next character
    private void advance() throws IOException {
        currentChar = reader.read();

    }
    // Peek at the next character
    private int peek() throws IOException {
        reader.mark(1);
        int nextChar = reader.read();
        reader.reset();
        return nextChar;
    }
    // Check if a word is a keyword
    private boolean isKeyword(String word) {
        return word.matches("free|final|rec|fun|for|while|if|else|return|int|float|string|bool");
    }

    // Get the next symbol
    public Symbol getNextSymbol() throws IOException {

        // Skip whitespace and comments
        while (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '$') {
            if (currentChar == '$') { // Comment handling
                while (currentChar != '\n' && currentChar != -1) {
                    advance();
                }
            }
            advance();
        }

        // End of File
        if (currentChar == -1) {
            return new Symbol(Symbol.TokenType.EOF, "EOF");
        }

        // Identifiers & Keywords
        if (Character.isLetter(currentChar) || currentChar == '_') {
            StringBuilder sb = new StringBuilder();
            while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                sb.append((char) currentChar);
                advance();
            }
            String value = sb.toString();
            // Keywords
            if(value.equals("true") || value.equals("false")){
                return new Symbol(Symbol.TokenType.BOOLEAN, value);
            }
            if (isKeyword(value)) {
                return new Symbol(Symbol.TokenType.KEYWORD, value); // 🔹 Corrigé ici
            }
            //Record
            if(Character.isUpperCase(value.charAt(0))){
                return new Symbol(Symbol.TokenType.RECORD, value);
            }
            return new Symbol(Symbol.TokenType.IDENTIFIER, value);
        }

        // Numbers (Integer & Float)
        if (Character.isDigit(currentChar) || (currentChar == '.' && Character.isDigit(peek()))) {
            StringBuilder sb = new StringBuilder();
            boolean isFloat = false;

            // Handle leading dot for floats
            if (currentChar == '.') {
                sb.append('0');
                isFloat = true;
                sb.append((char) currentChar);
                advance();
            }

            while (Character.isDigit(currentChar) || currentChar == '.') {
                if (currentChar == '.') {
                    if (isFloat) {
                        break; // End the current number and start a new one
                    }
                    isFloat = true;
                }
                sb.append((char) currentChar);
                advance();
            }

            // Remove leading zeros for both integers and floats
            String number = sb.toString();
            if (isFloat) {
                number = number.replaceFirst("^0+(?=\\d)", "");
            } else {
                number = number.replaceFirst("^0+(?!$)", "");
            }

            if (isFloat) {
                return new Symbol(Symbol.TokenType.FLOAT, number);
            } else {
                return new Symbol(Symbol.TokenType.INTEGER, number);
            }
        }

        // Strings
        if (currentChar == '"') {
            StringBuilder sb = new StringBuilder();
            advance();
            while (currentChar != '"' && currentChar != -1) {
                if (currentChar == '\\') { // Handle escaped characters
                    advance();
                    if (currentChar == 'n') sb.append('\n');
                    else if (currentChar == '\\') sb.append('\\');
                    else if (currentChar == '"') sb.append('"');
                } else {
                    sb.append((char) currentChar);
                }
                advance();
            }
            advance();
            return new Symbol(Symbol.TokenType.STRING, sb.toString());
        }

        // Operators & Punctuation
        String operators = "=+-*/%==!=<><=>=&&||;,.(){}[]";
        if (operators.indexOf(currentChar) != -1) {
            char op = (char) currentChar;

            advance();
            if (op == ';'){return new Symbol(Symbol.TokenType.EOL, ";");}
            return new Symbol(Symbol.TokenType.OPERATOR, String.valueOf(op));
        }

        // Unrecognized Character Error
        throw new IOException("Unrecognized character: " + (char) currentChar);
    }

}
