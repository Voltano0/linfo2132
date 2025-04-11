package compiler.Parser;
import compiler.Semantic.*;

public class LiteralExpression extends ASTNode {
    private String value;

    public LiteralExpression(String value) {
        this.value = value;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "Literal: " + value;
    }

    @Override
    public void accept(SemanticAnalysis visitor) {
        visitor.visit(this);
    }

    public String getValue() {
        return value;
    }
}
