package compiler.Parser;
import compiler.Semantic.*;

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

    @Override
    public void accept(SemanticAnalysis v) {
        v.visit(this);
    }
    public ASTNode getLeft() {
        return left;
    }
    public String getOperator() {
        return operator;
    }
    public ASTNode getRight() {
        return right;
    }
}
