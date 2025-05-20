package compiler.Parser;
import compiler.CodeGen.CodeGenVisitor;
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
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    public FunctionDeclaration getMainFn() {
        for (ASTNode statement : statements) {
            if (statement instanceof FunctionDeclaration) {
                FunctionDeclaration function = (FunctionDeclaration) statement;
                if (function.getFunctionName().equals("main")) {
                    return function;
                }
            }
        }
        return null; // No main function found
    }
}
