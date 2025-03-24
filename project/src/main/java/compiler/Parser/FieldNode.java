package compiler.Parser;


class FieldNode extends ASTNode {
    String type;
    String name;

    public FieldNode(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return "FieldNode{" + "type='" + type + '\'' + ", name='" + name + '\'' + '}' + '\n';
    }
}
