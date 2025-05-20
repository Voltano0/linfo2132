package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
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
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
    public String getTypeName() {
        return typeName;
    }

    public boolean isWide() {
        return "long".equals(typeName) || "double".equals(typeName);
    }
    public boolean isIntOrBool() {
        return typeName.equals("int") || typeName.equals("bool");
    }
}
