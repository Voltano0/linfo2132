import compiler.Lexer.Lexer;
import compiler.Parser.ASTNode;
import compiler.Parser.Parser;
import compiler.Parser.Program;
import compiler.Semantic.SemanticAnalysis;
import compiler.CodeGen.CodeGen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

public class TestSemantic {
    public static void main(String[] args) {
        String source = "$Good luck\n" +
                "\n" +
                "final message string = \"Hello\";\n" +
                "final run bool = true;\n" +
                "\n" +
                "Point rec {\n" +
                "    x int;\n" +
                "    y int;\n" +
                "}\n" +
                "\n" +
                "a int = 3;\n" +
                "\n" +
                "fun square(v int) int {\n" +
                "    return v*v;\n" +
                "}\n" +
                "\n" +
                "fun main() {\n" +
                "    value int = readInt();\n" +
                "    p Point = Point(a, a+value);\n" +
                "    writeInt(square(value));\n" +
                "    writeln(\"\");\n" +
                "    i int;\n" +
                "    for (i, 1, a, 1) {\n" +
                "        while (value!=0) {\n" +
                "            if (run){\n" +
                "                value = value - 1;\n" +
                "            } else {\n" +
                "                write(message);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    i = (i+2)*2;\n" +
                "}";
        try {
            // lexical & pars
            StringReader reader = new StringReader(source);
            Lexer lexer = new Lexer(reader);
            Parser parser = new Parser(lexer);
            ASTNode node = parser.parseProgram();

            // Perform semantic analysis

            SemanticAnalysis semanticAnalysis = new SemanticAnalysis();
            node.accept(semanticAnalysis);

            if (semanticAnalysis.hasErrors()) {
                System.err.println("Semantic errors found:");
                semanticAnalysis.printErrors();
            } else {
                System.out.println("Semantic analysis completed successfully.");
                // You can add code generation here if needed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
