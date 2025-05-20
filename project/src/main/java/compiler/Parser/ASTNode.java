package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
import compiler.Semantic.*;

public abstract class ASTNode {
    public abstract String prettyPrint(String indent);
    @Override
    public String toString() {
        return prettyPrint("");
    }
    public abstract void accept(ASTVisitor v);
}
