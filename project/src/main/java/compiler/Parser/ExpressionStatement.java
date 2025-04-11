package compiler.Parser;
import compiler.Semantic.*;

public class ExpressionStatement extends ASTNode {
    private ASTNode expression;

    public ExpressionStatement(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "ExpressionStatement:\n" + expression.prettyPrint(indent + "  ");
    }

    @Override
    public void accept(SemanticAnalysis visitor) {
        visitor.visit(this);
    }

    public ASTNode getExpression() {
        return expression;
    }
}
