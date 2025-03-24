package compiler.Parser;

import java.util.*;

public abstract class ASTNode {
    abstract public String toString();
}


class ProgramNode extends ASTNode {
    List<ASTNode> statements;

    public ProgramNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "ProgramNode{" + "statements=" + statements + '}';
    }
}



class VariableDeclarationNode extends ASTNode {
    boolean isFinal;
    String type;
    String name;
    ASTNode initializer;

    public VariableDeclarationNode(boolean isFinal, String type, String name, ASTNode initializer) {
        this.isFinal = isFinal;
        this.type = type;
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public String toString() {
        return "VariableDeclarationNode{" + "isFinal=" + isFinal + ", type='" + type + '\'' + ", name='" + name + '\'' + ", initializer=" + initializer + '}';
    }
}










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
        return "FunctionDeclarationNode{" + "name='" + name + '\'' + ", parameters=" + parameters + ", returnType='" + returnType + '\'' + ", body=" + body + '}';
    }
}

class ParameterNode extends ASTNode {
    String type;
    String name;

    public ParameterNode(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return "ParameterNode{" + "type='" + type + '\'' + ", name='" + name + '\'' + '}';
    }
}

class BlockNode extends ASTNode {
    List<ASTNode> statements;

    public BlockNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "BlockNode{" + "statements=" + statements + '}';
    }
}

class IfNode extends ASTNode {
    ASTNode condition;
    BlockNode thenBlock;
    BlockNode elseBlock;

    public IfNode(ASTNode condition, BlockNode thenBlock, BlockNode elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public String toString() {
        return "IfNode{" + "condition=" + condition + ", thenBlock=" + thenBlock + ", elseBlock=" + elseBlock + '}';
    }
}




class WhileNode extends ASTNode {
    ASTNode condition;
    BlockNode body;

    public WhileNode(ASTNode condition, BlockNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "WhileNode{" + "condition=" + condition + ", body=" + body + '}';
    }
}

class ForNode extends ASTNode {
    ASTNode initializer;
    ASTNode condition;
    ASTNode increment;
    BlockNode body;

    public ForNode(ASTNode initializer, ASTNode condition, ASTNode increment, BlockNode body) {
        this.initializer = initializer;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    @Override
    public String toString() {
        return "ForNode{" + "initializer=" + initializer + ", condition=" + condition + ", increment=" + increment + ", body=" + body + '}';
    }
}



class BinaryOperationNode extends ASTNode {
    ASTNode left;
    String operator;
    ASTNode right;

    public BinaryOperationNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "BinaryOperationNode{" + "left=" + left + ", operator='" + operator + '\'' + ", right=" + right + '}';
    }
}

class UnaryOperationNode extends ASTNode {
    String operator;
    ASTNode operand;

    public UnaryOperationNode(String operator, ASTNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return "UnaryOperationNode{" + "operator='" + operator + '\'' + ", operand=" + operand + '}';
    }
}

class LiteralNode extends ASTNode {
    Object value;

    public LiteralNode(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LiteralNode{" + "value=" + value + '}';
    }
}

class IdentifierNode extends ASTNode {
    String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IdentifierNode{" + "name='" + name + '\'' + '}';
    }
}

class FunctionCallNode extends ASTNode {
    String functionName;
    List<ASTNode> arguments;

    public FunctionCallNode(String functionName, List<ASTNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "FunctionCallNode{" + "functionName='" + functionName + '\'' + ", arguments=" + arguments + '}';
    }
}