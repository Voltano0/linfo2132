package compiler.Parser;

public class ReturnStatement extends ASTNode {
    private ASTNode expression; // Optional; may be null

    public ReturnStatement(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String prettyPrint(String indent) {
        if (expression != null) {
            return indent + "ReturnStatement:\n" + expression.prettyPrint(indent + "  ");
        } else {
            return indent + "ReturnStatement";
        }
    }
}
