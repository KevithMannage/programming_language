package Lexical_Analyser;

import java.util.List;

public class TestLexicalAnalyzer {
    public static void main(String[] args) {
//      if (args.length != 1) {
//          System.out.println("Usage: java RPALScanner <input_file>");
//          return;
//      }

      String inputFileName = "t1.txt";
      LexicalAnalyser scanner = new LexicalAnalyser(inputFileName);
      List<Token> tokens;
		try {
			tokens = scanner.scan();
			
			// Print the generated tokens
	        for (Token token : tokens) {
	            System.out.println("<" + token.type + ", " + token.value + "  >");
	        }
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

      
  }
}
