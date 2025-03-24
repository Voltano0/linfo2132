package compiler.Parser;

import java.util.List;

class FunctionDeclarationNode extends ASTNode {
    String name;
    List<ParameterNode> parameters;
    String returnType;
    BlockNode body;

    public FunctionDeclarationNode(String name, List<ParameterNode> parameters, String returnType, BlockNode body) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    @Override
    public String toString() {
        return "FunctionDeclarationNode{" + "name='" + name + '\'' + ", parameters=" + parameters + ", returnType='" + returnType + '\'' + ", body=" + body + '}' + '\n';
    }
}
