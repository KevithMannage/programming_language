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
	
	public Parser(List<Token> tokens) {
		this.tokens=tokens;
		AST = new ArrayList<>();
		stringAST = new ArrayList<>();
	}
	
	public List<Node> parse(){
		tokens.add(new Token(TokenType.EndOfTokens,""));
		E();
		if(tokens.get(0).type.equals(TokenType.EndOfTokens)) {
//			System.out.println("ParsingSuccessful!...........");
			return AST;
		}
		else {
			System.out.println("Parsing Unsuccessful!...........");
			System.out.println("REMAINIG UNPARSED TOKENS:");
			for (Token token : tokens) {
	            System.out.println("<" + token.type + ", " + token.value + ">");
	        }
			return null;
		}
	}
	
	public ArrayList<String> convertAST_toStringAST(){
//		System.out.println("Converting AST to String.......");
		
		String dots = "";
		List<Node> stack= new ArrayList<Node>();
		
		while(!AST.isEmpty()) {
			if(stack.isEmpty()) {
				if(AST.get(AST.size()-1).noOfChildren==0) {
					addStrings(dots,AST.remove(AST.size()-1));
				}
				else {
					Node node = AST.remove(AST.size()-1);
//					node.noOfChildren--;
					stack.add(node);
//					dots += ".";
				}
			}
			else {
				if(AST.get(AST.size()-1).noOfChildren>0) {
					Node node = AST.remove(AST.size()-1);
//					node.noOfChildren--;
					stack.add(node);
					dots += ".";
				}
				else {
					stack.add(AST.remove(AST.size()-1));
					dots += ".";
					while(stack.get(stack.size()-1).noOfChildren==0) {
						addStrings(dots,stack.remove(stack.size()-1));
						if(stack.isEmpty()) break;
						dots = dots.substring(0, dots.length() - 1);
						Node node =stack.remove(stack.size()-1);
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
	
	void addStrings(String dots,Node node) {
			switch(node.type) {
				case identifier:
					stringAST.add(dots+"<ID:"+node.value+">");
					break;
				case integer:
					stringAST.add(dots+"<INT:"+node.value+">");
					break;
				case string: 
					stringAST.add(dots+"<STR:"+node.value+">");
					break;	
				case true_value:
					stringAST.add(dots+"<"+node.value+">");
					break;
				case false_value:
					stringAST.add(dots+"<"+node.value+">");
					break;
				case nil:
					stringAST.add(dots+"<"+node.value+">");
					break;
				case dummy:
					stringAST.add(dots+"<"+node.value+">");
					break;
				case fcn_form:
					stringAST.add(dots+"function_form");
					break;
				default :
					stringAST.add(dots+node.value);
			}		
	}
	
	
	/*
	 * # Expressions ##########################################################################################
		E	->'let' D 'in' E		=> 'let'
			->'fn' Vb+ '.' E		=> 'lambda'
			->Ew;
	*/
	void E() {
//		System.out.println("E()");
		
	    int n=0;
		Token token=tokens.get(0);
		if(token.type.equals(TokenType.KEYWORD) && Arrays.asList("let", "fn").contains(token.value)) { 
			if(token.value.equals("let")) {
				// System.out.println(tokens.get(0).value);
				tokens.remove(0);
				D();
				if(!tokens.get(0).value.equals("in")) {
					System.out.println("Parse error at E : 'in' Expected");
//					// return;
				}
				// System.out.println(tokens.get(0).value);
				tokens.remove(0);
				E();
				AST.add(new Node(NodeType.let,"let",2));
				
			}
			else {
				// System.out.println(tokens.get(0).value);
				tokens.remove(0); // Remove fn
				do {
					Vb();
					n++;
				} while(tokens.get(0).type.equals(TokenType.IDENTIFIER) || tokens.get(0).value.equals("(")); 
				if(!tokens.get(0).value.equals(".")) {
					System.out.println("Parse error at E : '.' Expected");
					// return;
				}
				// System.out.println(tokens.get(0).value);
				tokens.remove(0);
				E();
				AST.add(new Node(NodeType.lambda,"lambda",n+1));							
			}
		}	
		else
			Ew();	
	}
	
	/*
	 * Ew	->T 'where' Dr			=> 'where'
			->T;
	*/
	void Ew() {
//		System.out.println("Ew()");
		T();
		if(tokens.get(0).value.equals("where")){
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); // Remove where
			Dr();
			AST.add(new Node(NodeType.where,"where",2));
	    }
		
	}
	
	/* 
	 *	# Tuple Expressions ######################################################################################
		T 	-> Ta ( ',' Ta )+ => 'tau'
			-> Ta ;
	*/
	void T() {
//		System.out.println("T()");
		Ta();
		int n = 1;
		while (tokens.get(0).value.equals(",")){
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); // Remove coma(,)
			Ta();
			++n;
	    }
	    if (n > 1) {
			AST.add(new Node(NodeType.tau,"tau",n));		
	    }
	}
	
	/*
	 * 		Ta 	-> Ta 'aug' Tc => 'aug'
				-> Tc ;
	*/
	void Ta(){
//		System.out.println("Ta()");
		Tc();
		while(tokens.get(0).value.equals("aug")){
			// System.out.println(tokens.get(0).value);	
			tokens.remove(0); //Remove aug
			Tc();
			AST.add(new Node(NodeType.aug,"aug",2));		
		} 
	}	
	
	/*
	 * Tc 	-> B '->' Tc '|' Tc => '->'
			-> B ;
	*/
	void Tc(){
//		System.out.println("Tc()");
		B();
		if(tokens.get(0).value.equals("->")){
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); // Remove '->'
			Tc();
			if(!tokens.get(0).value.equals("|")){
				System.out.println("Parse error at Tc: conditional '|' expected");
				// return;
			}
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); //Remove '|'
			Tc();
			AST.add(new Node(NodeType.conditional,"->",3));		
		}
	}
	
	/*
	 * # Boolean Expressions ######################################################################################
		B 	-> B 'or' Bt 	=> 'or'
			-> Bt ;	
	*/
	void B(){ // ----------------------------------------------------
//		System.out.println("B()");
		Bt();
		while(tokens.get(0).value.equals("or")){
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); //Remove 'or'
			Bt();
			AST.add(new Node(NodeType.op_or,"or",2));		
		} 
	}
	
	/*
	 * Bt	-> Bt '&' Bs => '&'
			-> Bs ;
	*/
	void Bt(){
//		System.out.println("Bt()");
		Bs();
		while(tokens.get(0).value.equals("&")){
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); //Remove '&'
			Bs();
			AST.add(new Node(NodeType.op_and,"&",2));		
		}
	}
	
	/*
	 * Bs	-> 'not' Bp => 'not'
			-> Bp ;
	*/
	void Bs(){
//		System.out.println("Bs()");
		if(tokens.get(0).value.equals("not")){
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); //Remove 'not'
			Bp();
			AST.add(new Node(NodeType.op_not,"not",1));		
		}
		else Bp();
	}
	
	/*
	 * Bp 	-> A ('gr' | '>' ) A => 'gr'
			-> A ('ge' | '>=') A => 'ge'
			-> A ('ls' | '<' ) A => 'ls'
			-> A ('le' | '<=') A => 'le'
			-> A 'eq' A => 'eq'
			-> A 'ne' A => 'ne'
			-> A ;
	 */
	void Bp() {
//		System.out.println("Bp()");
		A();
		Token token = tokens.get(0);
		if(Arrays.asList(">", ">=", "<", "<=").contains(token.value)
				|| Arrays.asList("gr", "ge", "ls", "le", "eq", "ne").contains(token.value)){
			// System.out.println(tokens.get(0).value);
			tokens.remove(0);
			A();
			switch(token.value){
				case ">":
					AST.add(new Node(NodeType.op_compare,"gr",2));		
					break;
				case ">=":
					AST.add(new Node(NodeType.op_compare,"ge",2));		
					break;
				case "<":
					AST.add(new Node(NodeType.op_compare,"ls",2));		
					break;
				case "<=":
					AST.add(new Node(NodeType.op_compare,"le",2));		
					break;
				default:
					AST.add(new Node(NodeType.op_compare,token.value,2));	
					break;
			}
		}
	}

	/*
	 * # Arithmetic Expressions ##########################################################################################
		A 	-> A '+' At => '+'
			-> A '-' At => '-'
			->	 '+' At
			->	 '-'At =>'neg'
			-> At ;
	*/
	void A(){
//		System.out.println("A()");
		if (tokens.get(0).value.equals("+")) {
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); //Remove unary plus
			At();
	    } else if (tokens.get(0).value.equals("-")) {
	    	// System.out.println(tokens.get(0).value);
			tokens.remove(0); // Remove unary minus
			At();
			AST.add(new Node(NodeType.op_neg,"neg",1));	
	    } else {
	        At();
	    }
	    while (Arrays.asList("+", "-").contains(tokens.get(0).value)) {
	    	Token currentToken = tokens.get(0); //save present token
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); // Remove plus or minus operators
			At();
			if(currentToken.value.equals("+")) AST.add(new Node(NodeType.op_plus,"+",2));
			else AST.add(new Node(NodeType.op_minus,"-",2));
	    }
		
	}
	
	/*
	 * 		At 	-> At '*' Af => '*'
				-> At '/' Af => '/'
				-> Af ;
	*/
	void At(){
//		System.out.println("At()");
		Af();
		while(Arrays.asList("*", "/").contains(tokens.get(0).value)){
			Token currentToken = tokens.get(0); //save present token
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); // Remove multiply or divide operators
			Af();
			if(currentToken.value.equals("*")) AST.add(new Node(NodeType.op_mul,"*",2));
			else AST.add(new Node(NodeType.op_div,"/",2));
		}		
	}
	
	/*
	 * 		Af 	-> Ap '**' Af => '**'
				-> Ap ;
	*/
	void Af(){
//		System.out.println("Af()");
		Ap();
		if(tokens.get(0).value.equals("**")){
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); //Remove power operator
			Af();
			AST.add(new Node(NodeType.op_pow,"**",2));	
		}
	}
	
	/*
	 * 		Ap 	-> Ap '@' '<IDENTIFIER>' R => '@'
				-> R ;
	*/
	void Ap(){
//		System.out.println("Ap()");
		R();
		while(tokens.get(0).value.equals("@")){
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); //Remove @ operator
			
			if(!tokens.get(0).type.equals(TokenType.IDENTIFIER)){
				System.out.println("Parsing error at Ap: IDENTIFIER EXPECTED");
				// return;
			}
			AST.add(new Node(NodeType.identifier,tokens.get(0).value,0));	
			// System.out.println(tokens.get(0).value);
			tokens.remove(0); // Remove IDENTIFIER
			
			R();
			AST.add(new Node(NodeType.at,"@",3));	
		}
	}
	
	/*
	 * # Rators And Rands ##########################################################################################
			R 	-> R Rn => 'gamma'
				-> Rn ;
	*/
	void R(){
//		System.out.println("R()");
		Rn();
		while((Arrays.asList(TokenType.IDENTIFIER, TokenType.INTEGER, TokenType.STRING).contains(tokens.get(0).type))
				||(Arrays.asList("true", "false", "nil", "dummy").contains(tokens.get(0).value))
				||(tokens.get(0).value.equals("("))) {
			
			Rn();
			AST.add(new Node(NodeType.gamma,"gamma",2));
//			System.out.println("gamma node added");

		}
	}
	
	/*
	 * 			Rn 	-> '<IDENTIFIER>'
				-> '<INTEGER>'
				-> '<STRING>'
				-> 'true' => 'true'
				-> 'false' => 'false'
				-> 'nil' => 'nil'
				-> '(' E ')'
				-> 'dummy' => 'dummy' ;
	*/
	void Rn(){
//		System.out.println("Rn()");
		switch(tokens.get(0).type){
			case IDENTIFIER:
				AST.add(new Node(NodeType.identifier,tokens.get(0).value,0));	
				// System.out.println(tokens.get(0).value);
				tokens.remove(0);
				break;
			case INTEGER:
				AST.add(new Node(NodeType.integer,tokens.get(0).value,0));	
				// System.out.println(tokens.get(0).value);
				tokens.remove(0);
				break;
			case STRING:
				AST.add(new Node(NodeType.string,tokens.get(0).value,0));	
				// System.out.println(tokens.get(0).value);
				tokens.remove(0);
				break;
			case KEYWORD:
				switch(tokens.get(0).value){
					case "true":
						AST.add(new Node(NodeType.true_value,tokens.get(0).value,0));
						// System.out.println(tokens.get(0).value);
						tokens.remove(0);
						break;
					case "false":
						AST.add(new Node(NodeType.false_value,tokens.get(0).value,0));	
						// System.out.println(tokens.get(0).value);
						tokens.remove(0);
						break;
					case "nil":
						AST.add(new Node(NodeType.nil,tokens.get(0).value,0));
						// System.out.println(tokens.get(0).value);
						tokens.remove(0);
						break;
					case "dummy":
						AST.add(new Node(NodeType.dummy,tokens.get(0).value,0));
						// System.out.println(tokens.get(0).value);
						tokens.remove(0);
						break;
					default:
						System.out.println("Parse Error at Rn: Unexpected KEYWORD");
						break;
				}
				break;
			case PUNCTUATION:
				if(tokens.get(0).value.equals("(")) {
					// // System.out.println(tokens.get(0).value);
					tokens.remove(0); //Remove '('
					
					E();
					
					if(!tokens.get(0).value.equals(")")) {
						System.out.println("Parsing error at Rn: Expected a matching ')'");
						// return;
					}
					// // System.out.println(tokens.get(0).value);
					tokens.remove(0); //Remove ')'
				}
				else System.out.println("Parsing error at Rn: Unexpected PUNCTUATION");
				break;
			default:
				System.out.println("Parsing error at Rn: Expected a Rn, but got different");
				break;
		}			
		
		
	}
	
	/*
	 * Definitions
	 * # Definitions ############################################
			D 	-> Da 'within' D => 'within'
				-> Da ;
	 */
	void D(){
//		System.out.println("D()");
		Da();
		if(tokens.get(0).value.equals("within")){
			// // System.out.println(tokens.get(0).value);
			tokens.remove(0); //Remove 'within'
			D();
			AST.add(new Node(NodeType.within,"within",2));	
		}
	}
	
	/*
	 * 			Da  -> Dr ( 'and' Dr )+ => 'and'
					-> Dr ;
	*/
	void Da(){
//		System.out.println("Da()");
		Dr();
		int n = 1;
		while(tokens.get(0).value.equals("and")){
			// // System.out.println(tokens.get(0).value);
			tokens.remove(0);
			Dr();
			n++;
		}
		if(n>1) AST.add(new Node(NodeType.and,"and",n));	
	}
	
	/*
	 * 			Dr  -> 'rec' Db => 'rec'
					-> Db ;
	*/
	void Dr(){
//		System.out.println("Dr()");
		boolean isRec = false;
		if(tokens.get(0).value.equals("rec")){
			// // System.out.println(tokens.get(0).value);
			tokens.remove(0);
	        isRec = true;
	    }
	    Db();
	    if (isRec) {
			AST.add(new Node(NodeType.rec,"rec",1));	
	    }
	}
	
	/*
	 * 			Db  -> Vl '=' E => '='
				-> '<IDENTIFIER>' Vb+ '=' E => 'fcn_form'
				-> '(' D ')' ; 
	*/
	void Db() {
//		System.out.println("Db()");
		if( tokens.get(0).type.equals(TokenType.PUNCTUATION) && tokens.get(0).value.equals("(")){
			// // System.out.println(tokens.get(0).value);
			tokens.remove(0);
			D();
			if(!tokens.get(0).value.equals(")")) {
				System.out.println("Parsing error at Db #1");
				// return;
			}
			// // System.out.println(tokens.get(0).value);
			tokens.remove(0);
		}
		else if(tokens.get(0).type.equals(TokenType.IDENTIFIER)){
			if(tokens.get(1).value.equals("(") || tokens.get(1).type.equals(TokenType.IDENTIFIER)) { // Expect a fcn_form
				AST.add(new Node(NodeType.identifier,tokens.get(0).value,0));
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
				}
				if(!tokens.get(0).value.equals("=")) {
					System.out.println("Parsing error at Db #2");
				}
				tokens.remove(0); // ) remove
				E();
				AST.add(new Node(NodeType.fcn_form,"fcn_form",n+1));		
			}
		// else if(tokens.get(0).type.equals(TokenType.IDENTIFIER)){
		// 	if(tokens.get(1).value.equals("(") || tokens.get(1).type.equals(TokenType.IDENTIFIER)) { // Expect a fcn_form
		// 		AST.add(new Node(NodeType.identifier,tokens.get(0).value,0));
		// 		// System.out.println(tokens.get(0).value);
		// 		tokens.remove(0); // Remove ID
				
		// 		int n = 1; // Identifier child
		// 		do {
		// 			Vb();
		// 			n++;
		// 		} while(tokens.get(0).type.equals(TokenType.IDENTIFIER) || tokens.get(0).value.equals("("));
		// 		if(!tokens.get(0).value.equals("=")) {
		// 			System.out.println("Parsing error at Db #2");
		// 			// return;
		// 		}
		// 		// System.out.println(tokens.get(0).value);
		// 		tokens.remove(0);
		// 		E();
				
		// 		AST.add(new Node(NodeType.fcn_form,"fcn_form",n+1));		
				
		// 	}
			else if (tokens.get(1).value.equals("=")) {
				AST.add(new Node(NodeType.identifier,tokens.get(0).value,0));
				// System.out.println(tokens.get(0).value);
				tokens.remove(0); // Remove identifier
				// System.out.println(tokens.get(0).value);
				tokens.remove(0); // Remove equal
				E();
				AST.add(new Node(NodeType.equal,"=",2));		
			}
			else if (tokens.get(1).value.equals(",")){
				Vl();
				if(!tokens.get(0).value.equals("=")) {
					System.out.println("Parsing error at Db");
					// return;
				}
				// System.out.println(tokens.get(0).value);
				tokens.remove(0);
				E();
				
				AST.add(new Node(NodeType.equal,"=",2));		
			}
			
			
		}

	}

		
	/*
	 * # Variables ##############################################
		Vb  -> '<IDENTIFIER>'
			-> '(' Vl ')'
			-> '(' ')' => '()';
	 */
	void Vb(){
//		System.out.println("Vb()");
		if(tokens.get(0).type.equals(TokenType.PUNCTUATION) && tokens.get(0).value.equals("(")) {
			// System.out.println(tokens.get(0).value);
			tokens.remove(0);
			boolean isVl=false;
			
			if(tokens.get(0).type .equals(TokenType.IDENTIFIER) ){
				Vl();
				isVl = true;
			}
			if(!tokens.get(0).value.equals(")")){
				System.out.println("Parse error unmatch )");
				// return;
			}
			// System.out.println(tokens.get(0).value);
			tokens.remove(0);
			if(!isVl) AST.add(new Node(NodeType.empty_params,"()",0));	
			
		} else if(tokens.get(0).type .equals(TokenType.IDENTIFIER) ){
			AST.add(new Node(NodeType.identifier,tokens.get(0).value,0));
			// System.out.println(tokens.get(0).value);
			tokens.remove(0);
	    }
		
	}
	
	/*
	 * 		Vl -> '<IDENTIFIER>' list ',' => ','?;
	*/
	void Vl() {
//		System.out.println("Vl()");
		int n = 0;
		do {
			if(n>0) {
				// System.out.println(tokens.get(0).value);
				tokens.remove(0);
			}
			if(!tokens.get(0).type.equals(TokenType.IDENTIFIER)) {
				System.out.println("Parse error: a ID was expected )");
			}
			AST.add(new Node(NodeType.identifier,tokens.get(0).value,0));
			// System.out.println(tokens.get(0).value);
			tokens.remove(0);
			n++;
		} while (tokens.get(0).value.equals(","));
		AST.add(new Node(NodeType.comma,",",n));	
	}

}