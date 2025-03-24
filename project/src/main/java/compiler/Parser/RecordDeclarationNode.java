package compiler.Parser;

import java.util.List;

class RecordDeclarationNode extends ASTNode {
    String name;
    List<FieldNode> fields;

    public RecordDeclarationNode(String name, List<FieldNode> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "RecordDeclarationNode{" + "name='" + name + '\'' + ", fields=" + fields + '}' + '\n';
    }
}
