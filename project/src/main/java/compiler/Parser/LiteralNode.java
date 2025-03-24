package compiler.Parser;

class LiteralNode extends ASTNode {
    Object value;

    public LiteralNode(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LiteralNode{" + "value=" + value + '}' + '\n';
    }
}
