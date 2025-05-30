package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class ForStatement extends ASTNode {
    private String iterator;
    private ASTNode startExpr;
    private TypeNode typeStart;
    private ASTNode endExpr;

    private ASTNode stepExpr;
    private ASTNode block;


    public ForStatement(String iterator, ASTNode startExpr, ASTNode endExpr, ASTNode stepExpr, ASTNode block) {
        this.iterator = iterator;
        this.startExpr = startExpr;
        this.endExpr = endExpr;
        this.stepExpr = stepExpr;
        this.block = block;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("ForStatement: ").append(iterator).append("\n");
        sb.append(indent).append("  Start:\n").append(startExpr.prettyPrint(indent + "    ")).append("\n");
        sb.append(indent).append("  End:\n").append(endExpr.prettyPrint(indent + "    ")).append("\n");
        sb.append(indent).append("  Step:\n").append(stepExpr.prettyPrint(indent + "    ")).append("\n");
        sb.append(indent).append("  Block:\n").append(block.prettyPrint(indent + "    "));
        return sb.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
    public TypeNode getTypeStart() {
        return typeStart;
    }
    public void setTypeStart(TypeNode typeStart) {
        this.typeStart = typeStart;
    }
    public ASTNode getBlock() {
        return block;
    }
    public ASTNode getEndExpr() {
        return endExpr;
    }
    public String getIterator() {
        return iterator;
    }
    public ASTNode getStartExpr() {
        return startExpr;
    }
    public ASTNode getStepExpr() {
        return stepExpr;
    }

}
