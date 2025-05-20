package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

import java.util.List;

public class FunctionCallExpression extends ASTNode {
    private String functionName;
    private List<ASTNode> arguments;
    private List<String> parameterTypes;
    private TypeNode returnType;
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
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
    public List<String> getParameterTypes() {
        return parameterTypes;
    }
    public TypeNode getReturnType() {
        return returnType;
    }
    public void setReturnType(TypeNode returnType) {
        this.returnType = returnType;
    }
    public List<ASTNode> getArguments() {
        return arguments;
    }
    public String getFunctionName() {
        return functionName;
    }
}
