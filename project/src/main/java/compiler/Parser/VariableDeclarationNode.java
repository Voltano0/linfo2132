package compiler.Parser;

class VariableDeclarationNode extends ASTNode {
    boolean isFinal;
    String type;
    String name;
    ASTNode initializer;

    public VariableDeclarationNode(boolean isFinal, String type, String name, ASTNode initializer) {
        this.isFinal = isFinal;
        this.type = type;
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public String toString() {
        return "VariableDeclarationNode{" + "isFinal=" + isFinal + ", type='" + type + '\'' + ", name='" + name + '\'' + ", initializer=" + initializer + '}' + '\n';
    }
}
