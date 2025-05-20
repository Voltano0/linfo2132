package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;
import java.util.List;

public class FunctionDeclaration extends ASTNode {
    private String functionName;
    private List<Parameter> parameters;
    private TypeNode returnType; // May be null for void functions
    private ASTNode body;

    public FunctionDeclaration(String functionName, List<Parameter> parameters, TypeNode returnType, ASTNode body) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    @Override
    public String prettyPrint(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("FunctionDeclaration: ").append(functionName).append("\n");
        sb.append(indent).append("  Parameters:\n");
        for (Parameter param : parameters) {
            sb.append(param.prettyPrint(indent + "    ")).append("\n");
        }
        if (returnType != null) {
            sb.append(indent).append("  ReturnType:\n");
            sb.append(returnType.prettyPrint(indent + "    ")).append("\n");
        }
        sb.append(indent).append("  Body:\n");
        sb.append(body.prettyPrint(indent + "    "));
        return sb.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public ASTNode getBody() {
        return body;
    }
    public String getFunctionName() {
        return functionName;
    }
    public List<Parameter> getParameters() {
        return parameters;
    }
    public TypeNode getReturnType() {
        return returnType;
    }
}
