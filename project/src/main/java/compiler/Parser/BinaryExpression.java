package compiler.Parser;

public class BinaryExpression extends ASTNode {
    private String operator;
    private ASTNode left;
    private ASTNode right;

    public BinaryExpression(String operator, ASTNode left, ASTNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Expr:\n");
        sb.append(left.prettyPrint(indent + "  ")).append("\n");
        sb.append(indent).append("  Operator: ").append(operator).append("\n");
        sb.append(right.prettyPrint(indent + "  "));
        return sb.toString();
    }
}
