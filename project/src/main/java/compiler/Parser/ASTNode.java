package compiler.Parser;
import compiler.Semantic.*;

public abstract class ASTNode {
    public abstract String prettyPrint(String indent);
    public abstract void accept(SemanticAnalysis v);

    @Override
    public String toString() {
        return prettyPrint("");
    }

}
