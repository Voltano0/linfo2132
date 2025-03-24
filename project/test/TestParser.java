import static org.junit.Assert.*;

import compiler.Parser.ASTNode;
import org.junit.Test;
import java.io.IOException;
import java.io.StringReader;
import compiler.Lexer.Lexer;
import compiler.Parser.Parser;


public class TestParser {

    @Test
    public void testSimpleAssignment() throws IOException {
        String input = "$ COMMENT This is a comment\n" +
                "\n" +
                "$ IDENTIFIER and KEYWORD\n" +
                "int myVariable = 42;  $ INTEGER\n" +
                "float myFloat = 3.14; $ FLOAT\n" +
                "String myString = \"Hello, World!\"; $ STRING\n" +
                "boolean myBoolean = true; $ BOOLEAN\n" +
                "\n" +
                "$ OPERATOR and PUNCTUATION\n" +
                "myVariable += 00342; $ OPERATOR\n" +
                "char punctuation = \";\"; $ PUNCTUATION\n" +
                "\n" +
                "$ RECORD (Java record declaration)\n" +
                "record Person(String name, int age) {} $ RECORD\n" +
                "\n" +
                "$ End of line (EOL) test\n" +
                "System.out.println(myString); $ EOL should be detected here\n" +
                "\n" +
                "$ EOF (End of file marker should be handled by lexer implicitly)\n"
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        ASTNode node = parser.parseProgram();
        System.out.println(node);
        assertNotNull("Parser ne doit pas retourner null", node);
    }
}