package compiler.Parser;

public class TypeNode extends ASTNode {
    private String typeName;

    public TypeNode(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "Type: " + typeName;
    }
}
