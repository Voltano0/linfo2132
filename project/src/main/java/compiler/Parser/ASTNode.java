package compiler.Parser;

public abstract class ASTNode {
    public abstract String prettyPrint(String indent);

    @Override
    public String toString() {
        return prettyPrint("");
    }
}
