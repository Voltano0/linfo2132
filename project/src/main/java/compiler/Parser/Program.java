package compiler.Parser;
import compiler.Semantic.*;
import java.util.List;

public class Program extends ASTNode {
    List<ASTNode> statements;

    public Program(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public String prettyPrint(String indent) {
        return "ProgramNode" + '\n' +'\t' + "statements=" + '\n' + statements + '\n';
    }

    @Override
    public String toString() {
        return "ProgramNode" + '\n' +'\t' + "statements=" + '\n'+ statements + '\n';
    }
    @Override
    public void accept(SemanticAnalysis visitor) {
        visitor.visit(this);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }
}
