package compiler.Parser;

import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public class IndexExpression extends ASTNode {

    private ASTNode array; // The array or string being indexed
    private ASTNode index; // The index expression

    // Constructor
    public IndexExpression(ASTNode array, ASTNode index) {
        this.array = array;
        this.index = index;
    }

    // Getter for the array
    public ASTNode getArray() {
        return array;
    }

    // Getter for the index
    public ASTNode getIndex() {
        return index;
    }

    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }

    @Override
    public String prettyPrint(String indent) {
        String result = indent + toString();
        System.out.println(result);
        return result;
    }
}