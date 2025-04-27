package Lexical_Analyser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LexicalAnalyser {
    private String inputFileName;
    private List<Token> tokens;	

    public LexicalAnalyser(String inputFileName) {
        this.inputFileName = inputFileName;
        tokens = new ArrayList<>();
    }

    public List<Token> scan() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            int lineCount=0;
            while ((line = reader.readLine()) != null) {
            	lineCount++;
                try {
					tokenizeLine(line);
				} catch (Exception e) {
					throw new Exception(e.getMessage()+" in LINE: "+lineCount+"\nERROR in lexical_analysis.");
				}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tokens;
    }

    private void tokenizeLine(String line) throws Exception {
        // Implement tokenization logic based on the lexicon
        // Use regular expressions or manual parsing to identify tokens
        // and add them to the 'tokens' list.

        // Tokenization patterns
    	String digit = "[0-9]";
    	String letter = "[a-zA-Z]";
    	Pattern operatorSymbol = Pattern.compile("[+\\-*/<>&.@/:=~|$!#%^_\\[\\]{}\"`\\?]");
    	Pattern escape = Pattern.compile("(\\\\'|\\\\t|\\\\n|\\\\\\\\)");
    	
        Pattern identifierPattern = Pattern.compile(letter+"("+letter+"|"+digit+"|"+"_)*");
        Pattern integerPattern = Pattern.compile(digit + "+");
        Pattern operatorPattern = Pattern.compile(operatorSymbol + "+");
        
        Pattern punctuationPattern = Pattern.compile("[(),;]");
        Pattern spacesPattern = Pattern.compile("(\s|\t)+");
        
        Pattern stringPattern = Pattern.compile("'("+letter+"|"+digit+"|"+operatorSymbol+"|"+escape+"|"+punctuationPattern+"|"+spacesPattern+")*'");
        // the above pattern of string will not accept 'EmptyListOf'Numbers' and 'EmptyL\istOfNumbers'
        // but it will accept the 'EmptyListOf\'Numbers' and 'EmptyL\istOfNumbers'
        Pattern commentPattern = Pattern.compile("//.*");


        Matcher matcher;
        
        int currentIndex = 0;	
        while (currentIndex < line.length()) {
            char currentChar = line.charAt(currentIndex);

            // Skip spaces and comments
            Matcher spaceMatcher = spacesPattern.matcher(line.substring(currentIndex));
            Matcher commentMatcher = commentPattern.matcher(line.substring(currentIndex));
            if(commentMatcher.lookingAt()) {
            	String comment = commentMatcher.group();
//            	tokens.add(new Token(TokenType.DELETE, comment));
            	currentIndex += comment.length();
            	continue;
            }
            if (spaceMatcher.lookingAt()) {
            	String space = spaceMatcher.group();
//            	tokens.add(new Token(TokenType.DELETE, space));
          		currentIndex += space.length();
          		continue;
            }
            
           
            // Identifier
            matcher = identifierPattern.matcher(line.substring(currentIndex));
            if (matcher.lookingAt()) {
                String identifier = matcher.group();
                
                //Recognize Keywords
                List<String> keywords = List.of(
                        "let", "in", "fn", "where", "aug", "or", "not", "gr", "ge", "ls", 
                        "le", "eq", "ne", "true", "false", "nil", "dummy", "within", "and", "rec"
                    );
//                List<String> operatorString = List.of(
//                        "let", "in", "fn", "where", "aug", "or", "not", "gr", "ge", "ls", 
//                        "le", "eq", "ne", "true", "false", "nil", "dummy", "within", "and", "rec"
//                    );
                if(keywords.contains(identifier)) 
                	tokens.add(new Token(TokenType.KEYWORD, identifier));
                else
                	tokens.add(new Token(TokenType.IDENTIFIER, identifier));
                currentIndex += identifier.length();
                continue;
            }

            // Integer
            matcher = integerPattern.matcher(line.substring(currentIndex));
            if (matcher.lookingAt()) {
                String integer = matcher.group();
                tokens.add(new Token(TokenType.INTEGER, integer));
                currentIndex += integer.length();
                continue;
            }

            // Operator
            matcher = operatorPattern.matcher(line.substring(currentIndex));
            if (matcher.lookingAt()) {
                String operator = matcher.group();
                tokens.add(new Token(TokenType.OPERATOR, operator));
                currentIndex += operator.length();
                continue;
            }

            // String
            matcher = stringPattern.matcher(line.substring(currentIndex));
            if (matcher.lookingAt()) {
                String string = matcher.group();
                tokens.add(new Token(TokenType.STRING, string));
                currentIndex += string.length();
                continue;
            }

            // Punctuation
            matcher = punctuationPattern.matcher(Character.toString(currentChar));
            if (matcher.matches()) {
                tokens.add(new Token(TokenType.PUNCTUATION, Character.toString(currentChar)));
                currentIndex++;
                continue;
            }
            
            // ------- MUST THROW EXCEPTION WHEN THE NEX TOKEN IS NOT IN THE ANY TYPE OF THE TOKEN --------
            throw new Exception("Unable to tokenize the CHARACTER: "+currentChar+" at INDEX: "+currentIndex);
            	
        }    
    }

}
