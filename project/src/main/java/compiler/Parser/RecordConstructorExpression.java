package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

import java.util.List;

public class RecordConstructorExpression extends ASTNode {
    private String recordName;
    private List<ASTNode> arguments;

    public RecordConstructorExpression(String recordName, List<ASTNode> arguments) {
        this.recordName = recordName;
        this.arguments = arguments;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("RecordConstructor: ").append(recordName).append("\n");
        sb.append(indent).append("  Arguments:\n");
        for (ASTNode arg : arguments) {
            sb.append(arg.prettyPrint(indent + "    ")).append("\n");
        }
        return sb.toString();
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public List<ASTNode> getArguments() {
        return arguments;
    }
    public String getRecordName() {
        return recordName;
    }
}
