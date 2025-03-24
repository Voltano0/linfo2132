package compiler.Parser;

public class VariableExpression extends ASTNode {
    private String variableName;

    public VariableExpression(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "Identifier: " + variableName;
    }
}
