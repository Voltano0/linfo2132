package compiler.Parser;
import compiler.Semantic.*; 

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

    @Override
    public void accept(SemanticAnalysis visitor) {
        visitor.visit(this);
    }

    public String getParameterName() {
        return parameterName;
    }
    public ASTNode getType() {
        return type;
    }
}
