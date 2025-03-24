package compiler.Parser;

public class ExpressionStatement extends ASTNode {
    private ASTNode expression;

    public ExpressionStatement(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "ExpressionStatement:\n" + expression.prettyPrint(indent + "  ");
    }
}
