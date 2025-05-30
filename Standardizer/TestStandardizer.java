package Standardizer;

import java.util.List;
import Lexical_Analyser.LexicalAnalyser;
import Lexical_Analyser.Token;
import java.util.ArrayList;
import Parser.Parser;
import Parser.Node;

public class TestStandardizer {

    public static void main(String[] args) {
        String inputFileName = "input.txt";
        LexicalAnalyser scanner = new LexicalAnalyser(inputFileName);
        List<Token> tokens;
        List<Node> AST;

        try {
            //Tokenize the input using the Lexical Analyzer
            tokens = scanner.scan();
            System.out.println("Tokens:");
            for (Token token : tokens) {
                System.out.println("<" + token.type + ", " + token.value + ">");
            }

            //Parse tokens to generate the AST using the Parser
            Parser parser = new Parser(tokens);
            AST = parser.parse();

            if (AST == null) {
                return;
            }

            //Print the original AST 
            System.out.println("\nOriginal AST:");
            for (Node node : AST) {
                System.out.println("<" + node.type + ", " + node.value + ", " + node.noOfChildren + ">");
            }

            //Convert AST into a format that the Standardizer can use
            List<String> stringAST = parser.convertAST_toStringAST();
            System.out.println("\nString AST before standardization:");
            for (String line : stringAST) {
                System.out.println(line);
            }

            //Build AST for standardization
            BuildAST astBuilder = new BuildAST();
            AST ast = astBuilder.buildAbstractSyntaxTree(new ArrayList<>(stringAST));

            //Standardize the AST
            ast.standardize();

            // Print the standardized AST
            System.out.println("\nStandardized AST:");
            ast.printAST();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
