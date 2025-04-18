package compiler.Parser;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import java.io.StringReader;
import java.io.IOException;

public class TestParser {
    public static void main(String[] args) {
        String source = ""
                + "$ Test Parser Input\n"
                + "\n"
                + "final message string = \"Hello\";\n"
                + "final run bool = true;\n"
                + "\n"
                + "Point rec {\n"
                + "    int x;\n"
                + "    int y;\n"
                + "}\n"
                + "\n"
                + "a int = 3;\n"
                + "\n"
                + "fun square(int v) int {\n"
                + "    return v * v;\n"
                + "}\n"
                + "\n"
                + "fun main() {\n"
                + "    value int = readInt();\n"
                + "    Point p = Point(a, a + value);\n"
                + "    writeInt(square(value));\n"
                + "    writeln();\n"
                + "    i int;\n"
                + "    for (i, 1, a, 1) {\n"
                + "        while (value != 0) {\n"
                + "            if (run) {\n"
                + "                value = value - 1;\n"
                + "            } else {\n"
                + "                write(message);\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "    free p;\n"
                + "    i = (i + 2) * 2;\n"
                + "}\n";

        try {
            StringReader reader = new StringReader(source);
            Lexer lexer = new Lexer(reader);
            Parser parser = new Parser(lexer);
            Program ast = parser.parseProgram();
            System.out.println("===== AST Generated by the Parser =====");
            System.out.println(ast);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
