package compiler.Semantic;

public class SemanticError {
    private String errorType;
    private String message;

    public SemanticError(String errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return errorType + ": " + message;
    }
}