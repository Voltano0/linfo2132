package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

import java.util.List;

public class RecordDeclaration extends ASTNode{
    private String recordName;
    private String variableName;
    private List<ASTNode> arguments;
    private List<TypeNode> typeArguments;

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
    public void setTypeArguments(List<TypeNode> typeArguments) {
        this.typeArguments = typeArguments;
    }
    public List<TypeNode> getTypeArguments() {
        return typeArguments;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
