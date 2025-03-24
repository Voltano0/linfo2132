package compiler.Parser;

import java.util.List;

class FunctionCallNode extends ASTNode {
    String functionName;
    List<ASTNode> arguments;

    public FunctionCallNode(String functionName, List<ASTNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "FunctionCallNode{" + "functionName='" + functionName + '\'' + ", arguments=" + arguments + '}' + '\n';
    }
}
