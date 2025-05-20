package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class AssignmentStatement extends ASTNode {
    private ASTNode left;
    private ASTNode right;

    public AssignmentStatement(ASTNode left, ASTNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("AssignmentStatement:\n");
        sb.append(indent).append("  Left:\n").append(left.prettyPrint(indent + "    ")).append("\n");
        sb.append(indent).append("  Right:\n").append(right.prettyPrint(indent + "    "));
        return sb.toString();
    }

    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }

    public ASTNode getLeft() {
        return left;
    }
    public ASTNode getRight() {
        return right;
    }

}
