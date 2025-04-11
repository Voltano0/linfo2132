package compiler.Parser;
import compiler.Semantic.*;

import java.io.ObjectInputStream.GetField;
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

    @Override
    public void accept(SemanticAnalysis visitor) {
        visitor.visit(this);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }
}
