package Parser;

public class Node {
	public NodeType type;
	public String value;
	public int noOfChildren; //root node depth is 0
	
	public Node(NodeType type,String value,int children) {
		this.type=type;
		this.value=value;
		this.noOfChildren=children;
	}

}
