package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class VariableExpression extends ASTNode {
    private String variableName;
    private TypeNode type;

    public VariableExpression(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "Identifier: " + variableName;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public String getVariableName() {
        return variableName;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }
    public TypeNode getType() {
        return type;
    }
}
