package compiler.Parser;

class WhileNode extends ASTNode {
    ASTNode condition;
    BlockNode body;

    public WhileNode(ASTNode condition, BlockNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "WhileNode{" + "condition=" + condition + ", body=" + body + '}' + '\n' ;
    }
}