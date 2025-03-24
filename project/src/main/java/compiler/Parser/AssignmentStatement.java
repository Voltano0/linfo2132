package compiler.Parser;

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
}
