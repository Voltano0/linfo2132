import static org.junit.Assert.*;
import org.junit.Test;
import java.io.IOException;
import java.io.StringReader;
import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;

public class TestLexer {

    @Test
    public void testTokens() throws IOException {
        String input = "$Good luck\n" +
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
                "}";   //
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);

        Symbol token;
        while ((token = lexer.getNextSymbol()).getType() != Symbol.TokenType.EOF) {
            System.out.println(token); // Affiche chaque token
        }

        assertNotNull("Lexer ne doit pas retourner null", lexer);
    }
}
