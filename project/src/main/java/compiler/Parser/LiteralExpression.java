package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class LiteralExpression extends ASTNode {
    private String value;
    private String type; // Optional; may be null

    public LiteralExpression(String value) {
        this.value = value;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "Literal: " + value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public boolean toBool() {
        return value.equals("true");
    }
    public String getValue() {
        return value;
    }
}
