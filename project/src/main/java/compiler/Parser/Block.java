package compiler.Parser;

import java.util.List;

public class Block extends ASTNode {
    private List<ASTNode> statements;

    public Block(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Block:\n");
        for (ASTNode stmt : statements) {
            sb.append(stmt.prettyPrint(indent + "  ")).append("\n");
        }
        return sb.toString();
    }
}
