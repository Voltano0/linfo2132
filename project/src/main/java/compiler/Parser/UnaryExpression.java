package compiler.Parser;
import compiler.Semantic.*;


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

    @Override
    public void accept(SemanticAnalysis visitor) {
        visitor.visit(this);
    }

    public String getOperator() {
        return operator;
    }
    
    public ASTNode getExpression() {
        return expression;
    }
}
