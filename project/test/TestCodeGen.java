import compiler.Lexer.Lexer;
import compiler.Parser.ASTNode;
import compiler.Parser.Parser;
import compiler.Parser.Program;
import compiler.Semantic.SemanticAnalysis;
import compiler.CodeGen.CodeGen;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class TestCodeGen {

    /**
     * This is a test class for the compiler.
     * It generates a source file, compiles it, and performs semantic analysis.
     * The generated class file is saved in the specified output directory.
     */
    static String test1 = "$Good luck\n" +
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
    static String test2 = "fun main() {\n" +
            "  write(\"hello\");\n" +
            "  writeln(\"\");\n" +
            "}";
    // Test integer arithmetic and function calls
    static String test3 = "fun add(a int, b int) int {\n" +
            "  return a + b;\n" +
            "}\n" +
            "\n" +
            "fun main() {\n" +
            "  writeInt(add(3, 4));\n" +
            "  writeln(\"\");\n" +
            "}";
    // Test boolean operators and comparisons
    static String test4 = "fun main() {\n" +
            "  write(readInt() != 0 && readInt() == 2);\n" +
            "  writeln(\"\");\n" +
            "}";
    // Test record definition, constructor, and field access
    static String test5 = "Point rec {\n" +
            "  x int;\n" +
            "  y int;\n" +
            "}\n" +
            "\n" +
            "fun main() {\n" +
            "  p Point = Point(5, 9);\n" +
            "  writeInt(p.x);\n" +
            "  writeInt(p.y);\n" +
            "  writeln(\"\");\n" +
            "}";
    // Test for‐loop and while‐loop with assignment
    static String test6 = "fun main() {\n" +
            "  sum int = 0;\n" +
            "  for(i, 1, 5, 1) {\n" +
            "    sum = sum + i;\n" +
            "  }\n" +
            "  writeInt(sum);\n" +
            "  writeln(\"\");\n" +
            "\n" +
            "  count int = readInt();\n" +
            "  while (count != 0) {\n" +
            "    writeInt(count);\n" +
            "    count = count - 1;\n" +
            "  }\n" +
            "  writeln(\"\");\n" +
            "}";
    // Test string concatenation and built-ins
    static String test7 = "fun main() {\n" +
            "  s string = \"foo\";\n" +
            "  s = s + \"bar\";\n" +
            "  writeInt(len(s));\n" +
            "  writeln(\"\");\n" +
            "}\n";


    public static void full(String source, String filePath, String lanngPath){
        // Write the file with UTF-8 encoding
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            writer.write(source);
            System.out.println("Source written to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            e.printStackTrace();
        }

        // Read the file with UTF-8 encoding (for verification or further processing)
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }

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
                CodeGen codeGen = new CodeGen((Program) node, lanngPath.toString());
                codeGen.generate();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    public static void main(String[] args) {
        //delete the try folder


        full(test5, "project/test/test5.lang", "project/try/test5/source.class");




    }
}
