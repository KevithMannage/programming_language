package Parser;

// This class represents a node in the Abstract Syntax Tree (AST) used for parsing.
public class Node {
	public NodeType type;
	public String value;
	public int noOfChildren; //root node depth is 0
	
	// Default constructor
	public Node(NodeType type,String value,int children) {
		this.type=type;
		this.value=value;
		this.noOfChildren=children;
	}

}
