package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class ReturnStatement extends ASTNode {
    private ASTNode expression; // Optional; may be null
    private TypeNode returnType;
    public ReturnStatement(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String prettyPrint(String indent) {
        if (expression != null) {
            return indent + "ReturnStatement:\n" + expression.prettyPrint(indent + "  ");
        } else {
            return indent + "ReturnStatement";
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public ASTNode getExpression() {
        return expression;
    }
    public void setReturnType(TypeNode expression) {
        this.returnType = expression;

    }
    public TypeNode getReturnType() {
        return returnType;
    }
}

