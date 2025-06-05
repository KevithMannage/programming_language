package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import  Lexical_Analyser.Token;
import  Lexical_Analyser.TokenType;
public class Parser {
    private List<Token> tokens;
    private List<Node> AST; // Last element will be root of the tree
    private ArrayList<String> stringAST;
    private boolean hasError; // Flag to track parsing errors

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        AST = new ArrayList<>();
        stringAST = new ArrayList<>();
        hasError = false; // Initialize error flag
    }
    public boolean hasError() {
    return hasError;
    }

    public List<Node> parse() {
        tokens.add(new Token(TokenType.EndOfTokens, ""));
        boolean success = E(); // Parse and check success
        if (success && !hasError && tokens.get(0).type.equals(TokenType.EndOfTokens)) {
//            System.out.println("Parsing Successful!...........");
            return AST;
        } else {
            System.out.println("Parsing Unsuccessful!...........");
            // System.out.println("REMAINING UNPARSED TOKENS:");
            // for (Token token : tokens) {
            //     System.out.println("<" + token.type + ", " + token.value + ">");
            // }
            return null; // Return null on any error
        }
    }

    public ArrayList<String> convertAST_toStringAST() {
        if (hasError || AST.isEmpty()) {
			if(AST.isEmpty()){
				 System.out.println("Cannot convert AST to String: Parsing failed or AST is empty.");
			 }
           
			 if(hasError){
				System.out.println("Error");
			 }
            return new ArrayList<>(); 
        }
//        System.out.println("Converting AST to String.......");

        String dots = "";
        List<Node> stack = new ArrayList<>();

        // Initialize the stack with the root node
        while (!AST.isEmpty()) {
            if (stack.isEmpty()) {
                if (AST.get(AST.size() - 1).noOfChildren == 0) {
                    addStrings(dots, AST.remove(AST.size() - 1));
                } else {
                    Node node = AST.remove(AST.size() - 1);
                    stack.add(node);
                }
                // Add a dot for the root node
            } else {
                if (AST.get(AST.size() - 1).noOfChildren > 0) {
                    Node node = AST.remove(AST.size() - 1);
                    stack.add(node);
                    dots += ".";
                } else {
                    stack.add(AST.remove(AST.size() - 1));
                    dots += ".";
                    while (!stack.isEmpty() && stack.get(stack.size() - 1).noOfChildren == 0) {
                        addStrings(dots, stack.remove(stack.size() - 1));
                        if (stack.isEmpty()) break;
                        dots = dots.substring(0, dots.length() - 1);
                        Node node = stack.remove(stack.size() - 1);
                        node.noOfChildren--;
                        stack.add(node);
                    }
                }
            }
        }

        // Reverse the list
        Collections.reverse(stringAST);
        return stringAST;
    }

    // Helper method to add strings to the stringAST based on node type
    void addStrings(String dots, Node node) {
        switch (node.type) {
            case identifier:
                stringAST.add(dots + "<ID:" + node.value + ">");
                break;
            case integer:
                stringAST.add(dots + "<INT:" + node.value + ">");
                break;
            case string:
                stringAST.add(dots + "<STR:" + node.value + ">");
                break;
            case true_value:
                stringAST.add(dots + "<" + node.value + ">");
                break;
            case false_value:
                stringAST.add(dots + "<" + node.value + ">");
                break;
            case nil:
                stringAST.add(dots + "<" + node.value + ">");
                break;
            case dummy:
                stringAST.add(dots + "<" + node.value + ">");
                break;
            case fcn_form:
                stringAST.add(dots + "function_form");
                break;
            default:
                stringAST.add(dots + node.value);
        }
    }

    // Expressions
    boolean E() {
        if (hasError) return false;
//        System.out.println("E()");
        int n = 0;
        Token token = tokens.get(0);
        if (token.type.equals(TokenType.KEYWORD) && Arrays.asList("let", "fn").contains(token.value)) {
            if (token.value.equals("let")) {
                tokens.remove(0);
                if (!D()) return false;
                if (!tokens.get(0).value.equals("in")) {
                    System.out.println("Parse error at E: 'in' Expected");
                    hasError = true;
                    return false;
                }
                tokens.remove(0);
                if (!E()) return false;
                AST.add(new Node(NodeType.let, "let", 2));
            } else {
                tokens.remove(0); // Remove fn
                do {
                    if (!Vb()) return false;
                    n++;
                } while (tokens.get(0).type.equals(TokenType.IDENTIFIER) || tokens.get(0).value.equals("("));
                if (!tokens.get(0).value.equals(".")) {
                    System.out.println("Parse error at E: '.' Expected");
                    hasError = true;
                    return false;
                }
                tokens.remove(0);
                if (!E()) return false;
                AST.add(new Node(NodeType.lambda, "lambda", n + 1));
            }
        } else {
            if (!Ew()) return false;
        }
        return true;
    }

    boolean Ew() {
        if (hasError) return false;
//        System.out.println("Ew()");
        if (!T()) return false;
        if (tokens.get(0).value.equals("where")) {
            tokens.remove(0); // Remove where
            if (!Dr()) return false;
            AST.add(new Node(NodeType.where, "where", 2));
        }
        return true;
    }

    // Tuple Expressions
    boolean T() {
        if (hasError) return false;
//        System.out.println("T()");
        if (!Ta()) return false;
        int n = 1;
        while (tokens.get(0).value.equals(",")) {
            tokens.remove(0); // Remove comma
            if (!Ta()) return false;
            ++n;
        }
        if (n > 1) {
            AST.add(new Node(NodeType.tau, "tau", n));
        }
        return true;
    }

    boolean Ta() {
        if (hasError) return false;
//        System.out.println("Ta()");
        if (!Tc()) return false;
        while (tokens.get(0).value.equals("aug")) {
            tokens.remove(0); // Remove aug
            if (!Tc()) return false;
            AST.add(new Node(NodeType.aug, "aug", 2));
        }
        return true;
    }

    boolean Tc() {
        if (hasError) return false;
//        System.out.println("Tc()");
        if (!B()) return false;
        if (tokens.get(0).value.equals("->")) {
            tokens.remove(0); // Remove '->'
            if (!Tc()) return false;
            if (!tokens.get(0).value.equals("|")) {
                System.out.println("Parse error at Tc: conditional '|' expected");
                hasError = true;
                return false;
            }
            tokens.remove(0); // Remove '|'
            if (!Tc()) return false;
            AST.add(new Node(NodeType.conditional, "->", 3));
        }
        return true;
    }

    // Boolean Expressions
    boolean B() {
        if (hasError) return false;
//        System.out.println("B()");
        if (!Bt()) return false;
        while (tokens.get(0).value.equals("or")) {
            tokens.remove(0); // Remove 'or'
            if (!Bt()) return false;
            AST.add(new Node(NodeType.op_or, "or", 2));
        }
        return true;
    }

    boolean Bt() {
        if (hasError) return false;
//        System.out.println("Bt()");
        if (!Bs()) return false;
        while (tokens.get(0).value.equals("&")) {
            tokens.remove(0); // Remove '&'
            if (!Bs()) return false;
            AST.add(new Node(NodeType.op_and, "&", 2));
        }
        return true;
    }

    boolean Bs() {
        if (hasError) return false;
//        System.out.println("Bs()");
        if (tokens.get(0).value.equals("not")) {
            tokens.remove(0); // Remove 'not'
            if (!Bp()) return false;
            AST.add(new Node(NodeType.op_not, "not", 1));
        } else {
            if (!Bp()) return false;
        }
        return true;
    }

    boolean Bp() {
        if (hasError) return false;
//        System.out.println("Bp()");
        if (!A()) return false;
        Token token = tokens.get(0);
        if (Arrays.asList(">", ">=", "<", "<=").contains(token.value)
                || Arrays.asList("gr", "ge", "ls", "le", "eq", "ne").contains(token.value)) {
            tokens.remove(0);
            if (!A()) return false;
            switch (token.value) {
                case ">":
                    AST.add(new Node(NodeType.op_compare, "gr", 2));
                    break;
                case ">=":
                    AST.add(new Node(NodeType.op_compare, "ge", 2));
                    break;
                case "<":
                    AST.add(new Node(NodeType.op_compare, "ls", 2));
                    break;
                case "<=":
                    AST.add(new Node(NodeType.op_compare, "le", 2));
                    break;
                default:
                    AST.add(new Node(NodeType.op_compare, token.value, 2));
                    break;
            }
        }
        return true;
    }

    // Arithmetic Expressions
    boolean A() {
        if (hasError) return false;
//        System.out.println("A()");
        if (tokens.get(0).value.equals("+")) {
            tokens.remove(0); // Remove unary plus
            if (!At()) return false;
        } else if (tokens.get(0).value.equals("-")) {
            tokens.remove(0); // Remove unary minus
            if (!At()) return false;
            AST.add(new Node(NodeType.op_neg, "neg", 1));
        } else {
            if (!At()) return false;
        }
        while (Arrays.asList("+", "-").contains(tokens.get(0).value)) {
            Token currentToken = tokens.get(0);
            tokens.remove(0); // Remove plus or minus
            if (!At()) return false;
            if (currentToken.value.equals("+")) {
                AST.add(new Node(NodeType.op_plus, "+", 2));
            } else {
                AST.add(new Node(NodeType.op_minus, "-", 2));
            }
        }
        return true;
    }

    boolean At() {
        if (hasError) return false;
//        System.out.println("At()");
        if (!Af()) return false;
        while (Arrays.asList("*", "/").contains(tokens.get(0).value)) {
            Token currentToken = tokens.get(0);
            tokens.remove(0); // Remove multiply or divide
            if (!Af()) return false;
            if (currentToken.value.equals("*")) {
                AST.add(new Node(NodeType.op_mul, "*", 2));
            } else {
                AST.add(new Node(NodeType.op_div, "/", 2));
            }
        }
        return true;
    }

    boolean Af() {
        if (hasError) return false;
//        System.out.println("Af()");
        if (!Ap()) return false;
        if (tokens.get(0).value.equals("**")) {
            tokens.remove(0); // Remove power operator
            if (!Af()) return false;
            AST.add(new Node(NodeType.op_pow, "**", 2));
        }
        return true;
    }

    boolean Ap() {
        if (hasError) return false;
//        System.out.println("Ap()");
        if (!R()) return false;
        while (tokens.get(0).value.equals("@")) {
            tokens.remove(0); // Remove @
            if (!tokens.get(0).type.equals(TokenType.IDENTIFIER)) {
                System.out.println("Parsing error at Ap: IDENTIFIER EXPECTED");
                hasError = true;
                return false;
            }
            AST.add(new Node(NodeType.identifier, tokens.get(0).value, 0));
            tokens.remove(0); // Remove IDENTIFIER
            if (!R()) return false;
            AST.add(new Node(NodeType.at, "@", 3));
        }
        return true;
    }

    // Rators and Rands
    boolean R() {
        if (hasError) return false;
//        System.out.println("R()");
        if (!Rn()) return false;
        while ((Arrays.asList(TokenType.IDENTIFIER, TokenType.INTEGER, TokenType.STRING).contains(tokens.get(0).type))
                || (Arrays.asList("true", "false", "nil", "dummy").contains(tokens.get(0).value))
                || (tokens.get(0).value.equals("("))) {
            if (!Rn()) return false;
            AST.add(new Node(NodeType.gamma, "gamma", 2));
//            System.out.println("gamma node added");
        }
        return true;
    }

    // Rands
    boolean Rn() {
        if (hasError) return false;
//        System.out.println("Rn()");
        switch (tokens.get(0).type) {
            case IDENTIFIER:
                AST.add(new Node(NodeType.identifier, tokens.get(0).value, 0));
                tokens.remove(0);
                break;
            case INTEGER:
                AST.add(new Node(NodeType.integer, tokens.get(0).value, 0));
                tokens.remove(0);
                break;
            case STRING:
                AST.add(new Node(NodeType.string, tokens.get(0).value, 0));
                tokens.remove(0);
                break;
            case KEYWORD:
                switch (tokens.get(0).value) {
                    case "true":
                        AST.add(new Node(NodeType.true_value, tokens.get(0).value, 0));
                        tokens.remove(0);
                        break;
                    case "false":
                        AST.add(new Node(NodeType.false_value, tokens.get(0).value, 0));
                        tokens.remove(0);
                        break;
                    case "nil":
                        AST.add(new Node(NodeType.nil, tokens.get(0).value, 0));
                        tokens.remove(0);
                        break;
                    case "dummy":
                        AST.add(new Node(NodeType.dummy, tokens.get(0).value, 0));
                        tokens.remove(0);
                        break;
                    default:
                        System.out.println("Parse Error at Rn: Unexpected KEYWORD");
                        hasError = true;
                        return false;
                }
                break;
            case PUNCTUATION:
                if (tokens.get(0).value.equals("(")) {
                    tokens.remove(0); // Remove '('
                    if (!E()) return false;
                    if (!tokens.get(0).value.equals(")")) {
                        System.out.println("Parsing error at Rn: Expected a matching ')'");
                        hasError = true;
                        return false;
                    }
                    tokens.remove(0); // Remove ')'
                } else {
                    System.out.println("Parsing error at Rn: Unexpected PUNCTUATION");
                    hasError = true;
                    return false;
                }
                break;
            default:
                System.out.println("Parsing error at Rn: Expected a Rn, but got different");
                hasError = true;
                return false;
        }
        return true;
    }

    // Definitions
    boolean D() {
        if (hasError) return false;
//        System.out.println("D()");
        if (!Da()) return false;
        if (tokens.get(0).value.equals("within")) {
            tokens.remove(0); // Remove 'within'
            if (!D()) return false;
            AST.add(new Node(NodeType.within, "within", 2));
        }
        return true;
    }

    boolean Da() {
        if (hasError) return false;
//        System.out.println("Da()");
        if (!Dr()) return false;
        int n = 1;
        // Check for multiple definitions separated by 'and'
        while (tokens.get(0).value.equals("and")) {
            tokens.remove(0);
            if (!Dr()) return false;
            n++;
        }
        // If there are multiple definitions, add an 'and' node
        if (n > 1) {
            AST.add(new Node(NodeType.and, "and", n));
        }
        return true;
    }

    boolean Dr() {
        if (hasError) return false;
//        System.out.println("Dr()");
        boolean isRec = false;
        if (tokens.get(0).value.equals("rec")) {
            tokens.remove(0);
            isRec = true;
        }
        // Check for 'let' or 'fn' keyword
        if (!Db()) return false;
        if (isRec) {
            AST.add(new Node(NodeType.rec, "rec", 1));
        }
        return true;
    }

    boolean Db() {
        if (hasError) return false;
//        System.out.println("Db()");
        if (tokens.get(0).type.equals(TokenType.PUNCTUATION) && tokens.get(0).value.equals("(")) {
            tokens.remove(0);
            // Handle empty parameters
            if (!D()) return false;
            if (!tokens.get(0).value.equals(")")) {
                System.out.println("Parsing error at Db #1");
                hasError = true;
                return false;
            }
            tokens.remove(0);
        } else if (tokens.get(0).type.equals(TokenType.IDENTIFIER)) {
            if (tokens.size() > 1 && (tokens.get(1).value.equals("(") || tokens.get(1).type.equals(TokenType.IDENTIFIER))) {
                AST.add(new Node(NodeType.identifier, tokens.get(0).value, 0));
                tokens.remove(0); // Remove ID
                int n = 1; // Identifier child
 				boolean singleParam = tokens.get(0).value.equals("(") && tokens.get(1).type.equals(TokenType.IDENTIFIER) && tokens.get(2).value.equals(")");
				if (singleParam) {
					tokens.remove(0); // Remove '('
					AST.add(new Node(NodeType.identifier,tokens.get(0).value,0));
					tokens.remove(0); // Remove IDENTIFIER
					tokens.remove(0); // Remove ')'
					n++;
				} else {
					do {
						Vb();
						n++;
					} while(tokens.get(0).type.equals(TokenType.IDENTIFIER) || tokens.get(0).value.equals("("));
				}                if (!tokens.get(0).value.equals("=")) {
                    System.out.println("Parsing error at Db #2");
                    hasError = true;
                    return false;
                }
                tokens.remove(0);
                if (!E()) return false;
               
                AST.add(new Node(NodeType.fcn_form, "fcn_form", n + 1));
            } else if (tokens.size() > 1 && tokens.get(1).value.equals("=")) {
                AST.add(new Node(NodeType.identifier, tokens.get(0).value, 0));
                tokens.remove(0); // Remove identifier
                tokens.remove(0); // Remove equal
                if (!E()) return false;
                AST.add(new Node(NodeType.equal, "=", 2));
            } else if (tokens.size() > 1 && tokens.get(1).value.equals(",")) {
                if (!Vl()) return false;
                if (!tokens.get(0).value.equals("=")) {
                    System.out.println("Parsing error at Db");
                    hasError = true;
                    return false;
                }
                tokens.remove(0);
                if (!E()) return false;
                AST.add(new Node(NodeType.equal, "=", 2));
            }
        }
        return true;
    }
    // Variables
    boolean Vb() {
        if (hasError) return false;
//        System.out.println("Vb()");
        if (tokens.get(0).type.equals(TokenType.PUNCTUATION) && tokens.get(0).value.equals("(")) {
            tokens.remove(0);
            boolean isVl = false;
            // Handle empty parameters
            if (tokens.get(0).type.equals(TokenType.IDENTIFIER)) {
                if (!Vl()) return false;
                isVl = true;
            }
            // Check for closing parenthesis
            if (!tokens.get(0).value.equals(")")) {
                System.out.println("Parse error at Vb: unmatched )");
                hasError = true;
                return false;
            }
            // Remove the closing parenthesis
            tokens.remove(0);
            if (!isVl) {
                AST.add(new Node(NodeType.empty_params, "()", 0));
            }
            // Add empty parameters node to AST
        } else if (tokens.get(0).type.equals(TokenType.IDENTIFIER)) {
            AST.add(new Node(NodeType.identifier, tokens.get(0).value, 0));
            tokens.remove(0);
            // Add identifier node to AST
        } else {
            System.out.println("Parse error at Vb: IDENTIFIER or '(' expected");
            hasError = true;
            return false;
        }
        return true;
    }

    // Variable List
    boolean Vl() {
        if (hasError) return false;
//        System.out.println("Vl()");
        int n = 0;
        do {
            if (n > 0) {
                tokens.remove(0); // Remove comma
            }
            if (!tokens.get(0).type.equals(TokenType.IDENTIFIER)) {
                System.out.println("Parse error at Vl: IDENTIFIER expected");
                hasError = true;
                return false;
            }
            // Add identifier node to AST
            AST.add(new Node(NodeType.identifier, tokens.get(0).value, 0));
            tokens.remove(0);
            n++;
        } while (tokens.get(0).value.equals(","));
        AST.add(new Node(NodeType.comma, ",", n));
        return true;
    }
}