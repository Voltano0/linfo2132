package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class FieldDeclaration extends ASTNode {
    private String fieldName;
    private TypeNode type;

    public FieldDeclaration(String fieldName, TypeNode type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "FieldDeclaration: " + fieldName + " : " + type.prettyPrint("");
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public String getFieldName() {
        return fieldName;
    }
    public TypeNode getType() {
        return type;
    }
}
