package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class VariableDeclaration extends ASTNode {
    private String variableName;
    private TypeNode type;
    private ASTNode initializer; // May be null

    public VariableDeclaration(String variableName, TypeNode type, ASTNode initializer) {
        this.variableName = variableName;
        this.type = type;
        this.initializer = initializer;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("VariableDeclaration: ").append(variableName).append("\n");
        sb.append(indent).append("  Type: ").append(type.prettyPrint(indent + "    ")).append("\n");
        if (initializer != null) {
            sb.append(indent).append("  Initializer:\n");
            sb.append(initializer.prettyPrint(indent + "    "));
        }
        return sb.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public ASTNode getInitializer() {
        return initializer;
    }

    public TypeNode getType() {
        return type;
    }

    public String getVariableName() {
        return variableName;
    }
}
