package compiler.Parser;

public class LiteralExpression extends ASTNode {
    private String value;

    public LiteralExpression(String value) {
        this.value = value;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "Literal: " + value;
    }
}
