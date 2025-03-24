package compiler.Parser;

import java.util.List;

public class RecordDefinition extends ASTNode {
    private String recordName;
    private List<ASTNode> fields;

    public RecordDefinition(String recordName, List<ASTNode> fields) {
        this.recordName = recordName;
        this.fields = fields;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("RecordDefinition: ").append(recordName).append("\n");
        sb.append(indent).append("  Fields:\n");
        for (ASTNode field : fields) {
            sb.append(field.prettyPrint(indent + "    ")).append("\n");
        }
        return sb.toString();
    }
}
