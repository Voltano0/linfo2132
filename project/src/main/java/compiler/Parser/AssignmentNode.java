package compiler.Parser;

class AssignmentNode extends ASTNode {
    String name;
    String operator;
    ASTNode value;

    public AssignmentNode(String name, String operator, ASTNode value) {
        this.name = name;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String toString() {
        return "AssignmentNode{" + "name='" + name + '\'' + ", operator='" + operator + '\'' + ", value=" + value + '}' + '\n';
    }
}
