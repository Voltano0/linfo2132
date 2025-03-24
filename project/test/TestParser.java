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
        String input = "x int = 42;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        ASTNode node = parser.parseProgram();
        assertNotNull("Parser ne doit pas retourner null", node);
    }
}