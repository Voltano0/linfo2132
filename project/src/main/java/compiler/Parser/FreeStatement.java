package compiler.Parser;

public class FreeStatement extends ASTNode {
    private ASTNode expression;

    public FreeStatement(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "FreeStatement:\n" + expression.prettyPrint(indent + "  ");
    }
}
