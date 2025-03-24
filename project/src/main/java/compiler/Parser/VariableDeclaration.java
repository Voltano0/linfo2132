package compiler.Parser;

public class VariableDeclaration extends ASTNode {
    private String variableName;
    private ASTNode type;
    private ASTNode initializer; // May be null

    public VariableDeclaration(String variableName, ASTNode type, ASTNode initializer) {
        this.variableName = variableName;
        this.type = type;
        this.initializer = initializer;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("VariableDeclaration: ").append(variableName).append("\n");
        sb.append(indent).append("  Type: ").append(type.prettyPrint("")).append("\n");
        if (initializer != null) {
            sb.append(indent).append("  Initializer:\n");
            sb.append(initializer.prettyPrint(indent + "    "));
        }
        return sb.toString();
    }
}
