package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class IfStatement extends ASTNode {
    private ASTNode condition;
    private ASTNode thenBlock;
    private ASTNode elseBlock; // Optional

    public IfStatement(ASTNode condition, ASTNode thenBlock, ASTNode elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("IfStatement:\n");
        sb.append(indent).append("  Condition:\n").append(condition.prettyPrint(indent + "    ")).append("\n");
        sb.append(indent).append("  Then:\n").append(thenBlock.prettyPrint(indent + "    ")).append("\n");
        if (elseBlock != null) {
            sb.append(indent).append("  Else:\n").append(elseBlock.prettyPrint(indent + "    "));
        }
        return sb.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public ASTNode getCondition() {
        return condition;
    }
    public ASTNode getElseBlock() {
        return elseBlock;
    }
    public ASTNode getThenBlock() {
        return thenBlock;
    }
}
