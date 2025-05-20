package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class ExpressionStatement extends ASTNode {
    private ASTNode expression;
    private String type;
    public ExpressionStatement(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "ExpressionStatement:\n" + expression.prettyPrint(indent + "  ");
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public ASTNode getExpression() {
        return expression;
    }
}
