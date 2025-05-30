package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class FreeStatement extends ASTNode {
    private ASTNode expression;

    public FreeStatement(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "FreeStatement:\n" + expression.prettyPrint(indent + "  ");
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public ASTNode getExpression() {
        return expression;
    }
}

