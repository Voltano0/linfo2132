package compiler.Parser;
import compiler.Semantic.*;

public class TypeNode extends ASTNode {
    private String typeName;

    public TypeNode(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "Type: " + typeName;
    }

    @Override
    public void accept(SemanticAnalysis visitor) {
        visitor.visit(this);
    }
    public String getTypeName() {
        return typeName;
    }
}
