package compiler.Parser;

public class UnaryExpression extends ASTNode {
    private String operator;
    private ASTNode expression;

    public UnaryExpression(String operator, ASTNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("UnaryExpression:\n");
        sb.append(indent).append("  Operator: ").append(operator).append("\n");
        sb.append(expression.prettyPrint(indent + "  "));
        return sb.toString();
    }
}
