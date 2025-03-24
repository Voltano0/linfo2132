package compiler.Parser;

class BinaryOperationNode extends ASTNode {
    ASTNode left;
    String operator;
    ASTNode right;

    public BinaryOperationNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "BinaryOperationNode{" + "left=" + left + ", operator='" + operator + '\'' + ", right=" + right + '}' + '\n';
    }
}
