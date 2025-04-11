package compiler.Parser;
import compiler.Semantic.*;

import java.util.List;

public class FunctionCallExpression extends ASTNode {
    private String functionName;
    private List<ASTNode> arguments;

    public FunctionCallExpression(String functionName, List<ASTNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("FunctionCall: ").append(functionName).append("\n");
        sb.append(indent).append("  Arguments:\n");
        for (ASTNode arg : arguments) {
            sb.append(arg.prettyPrint(indent + "    ")).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void accept(SemanticAnalysis visitor) {
        visitor.visit(this);
    }

    public List<ASTNode> getArguments() {
        return arguments;
    }
    public String getFunctionName() {
        return functionName;
    }
}
