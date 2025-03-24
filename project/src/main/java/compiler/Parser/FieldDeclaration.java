package compiler.Parser;

public class FieldDeclaration extends ASTNode {
    private String fieldName;
    private ASTNode type;

    public FieldDeclaration(String fieldName, ASTNode type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "FieldDeclaration: " + fieldName + " : " + type.prettyPrint("");
    }
}
