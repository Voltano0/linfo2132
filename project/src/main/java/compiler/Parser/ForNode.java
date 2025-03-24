package compiler.Parser;

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
        return "ForNode{" + "initializer=" + initializer + ", condition=" + condition + ", increment=" + increment + ", body=" + body + '}' + '\n';
    }
}
