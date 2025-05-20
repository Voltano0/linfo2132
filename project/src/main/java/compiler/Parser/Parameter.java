package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class Parameter extends ASTNode {
    private String parameterName;
    private TypeNode type;

    public Parameter(String parameterName, TypeNode type) {
        this.parameterName = parameterName;
        this.type = type;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "Parameter: " + parameterName + " : " + type.prettyPrint("");
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public String getParameterName() {
        return parameterName;
    }
    public TypeNode getType() {
        return type;
    }
}
