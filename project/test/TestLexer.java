import static org.junit.Assert.*;
import org.junit.Test;
import java.io.IOException;
import java.io.StringReader;
import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;

public class TestLexer {

    @Test
    public void testTokens() throws IOException {
        String input = "x int = 42;"; //
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);

        Symbol token;
        while ((token = lexer.getNextSymbol()).getType() != Symbol.TokenType.EOF) {
            System.out.println(token); // Affiche chaque token
        }

        assertNotNull("Lexer ne doit pas retourner null", lexer);
    }
}
