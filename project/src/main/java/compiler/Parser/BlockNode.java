package compiler.Parser;

import java.util.List;

class BlockNode extends ASTNode {
    List<ASTNode> statements;

    public BlockNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "BlockNode{" + "statements=" + statements + '}' + '\n';
    }
}
