package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class ConstantDeclaration extends ASTNode {
    private String name;
    private TypeNode type;
    private ASTNode expression;

    public ConstantDeclaration(String name, TypeNode type, ASTNode expression) {
        this.name = name;
        this.type = type;
        this.expression = expression;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("ConstantDeclaration: ").append(name).append("\n");
        sb.append(indent).append("  Type:\n").append(type.prettyPrint(indent + "    ")).append("\n");
        sb.append(indent).append("  Value:\n").append(expression.prettyPrint(indent + "    "));
        return sb.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public ASTNode getExpression() {
        return expression;
    }
    public String getName() {
        return name;
    }
    public TypeNode getType() {
        return type;
    }
}
