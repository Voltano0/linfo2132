package compiler.Parser;

public class Parameter extends ASTNode {
    private String parameterName;
    private ASTNode type;

    public Parameter(String parameterName, ASTNode type) {
        this.parameterName = parameterName;
        this.type = type;
    }

    @Override
    public String prettyPrint(String indent) {
        return indent + "Parameter: " + parameterName + " : " + type.prettyPrint("");
    }
}
