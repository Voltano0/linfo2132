package compiler.Parser;

class IdentifierNode extends ASTNode {
    String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IdentifierNode{" + "name='" + name + '\'' + '}' + '\n';
    }
}
