package compiler.Parser;

public class ConstantDeclaration extends ASTNode {
    private String name;
    private ASTNode type;
    private ASTNode expression;

    public ConstantDeclaration(String name, ASTNode type, ASTNode expression) {
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
}
