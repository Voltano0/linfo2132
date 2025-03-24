package compiler.Parser;

class UnaryOperationNode extends ASTNode {
    String operator;
    ASTNode operand;

    public UnaryOperationNode(String operator, ASTNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return "UnaryOperationNode{" + "operator='" + operator + '\'' + ", operand=" + operand + '}' + '\n';
    }
}
