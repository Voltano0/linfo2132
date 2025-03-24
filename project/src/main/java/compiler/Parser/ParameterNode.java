package compiler.Parser;

class ParameterNode extends ASTNode {
    String type;
    String name;

    public ParameterNode(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return "ParameterNode{" + "type='" + type + '\'' + ", name='" + name + '\'' + '}' + '\n';
    }
}
