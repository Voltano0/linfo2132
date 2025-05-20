package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class WhileStatement extends ASTNode {
    private ASTNode condition;
    private ASTNode block;

    public WhileStatement(ASTNode condition, ASTNode block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("WhileStatement:\n");
        sb.append(indent).append("  Condition:\n").append(condition.prettyPrint(indent + "    ")).append("\n");
        sb.append(indent).append("  Block:\n").append(block.prettyPrint(indent + "    "));
        return sb.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }


    public ASTNode getBlock() {
        return block;
    }

    public ASTNode getCondition() {
        return condition;
    }
}
