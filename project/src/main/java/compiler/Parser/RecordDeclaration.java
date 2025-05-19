package compiler.Parser;
import compiler.Semantic.*;

import java.util.List;

public class RecordDeclaration extends ASTNode{
    private String recordName;
    private String variableName;
    private List<ASTNode> arguments;

    public RecordDeclaration(String recordName, String variableName, List<ASTNode> arguments) {
        this.recordName = recordName;
        this.variableName = variableName;
        this.arguments = arguments;
    }

    public String getRecordName() {
        return recordName;
    }
    public String getVariableName() {
        return variableName;
    }
    public List<ASTNode> getArguments() {
        return arguments;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("RecordDeclaration: ").append(recordName).append("\n");
        sb.append(indent).append("  Variable Name: ").append(variableName).append("\n");
        sb.append(indent).append("  Arguments:\n");
        for (ASTNode arg : arguments) {
            sb.append(arg.prettyPrint(indent + "    ")).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void accept(SemanticAnalysis visitor) {
        visitor.visit(this);
    }
}
