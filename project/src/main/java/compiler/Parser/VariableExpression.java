package compiler.Parser;
import compiler.Semantic.*;

public class VariableExpression extends ASTNode {
    private String variableName;

    public VariableExpression(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "Identifier: " + variableName;
    }

    @Override
    public void accept(SemanticAnalysis visitor) {
        visitor.visit(this);
    }

    public String getVariableName() {
        return variableName;
    }
}
