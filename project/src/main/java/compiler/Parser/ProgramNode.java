package compiler.Parser;

import java.util.List;

class ProgramNode extends ASTNode {
    List<ASTNode> statements;

    public ProgramNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "ProgramNode{" + "statements=" + statements + '}' + '\n';
    }
}
