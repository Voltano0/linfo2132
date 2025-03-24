package compiler.Parser;

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
        return "IfNode{" + "condition=" + condition + ", thenBlock=" + thenBlock + ", elseBlock=" + elseBlock + '}' + '\n';
    }
}
