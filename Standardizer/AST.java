package Standardizer;

public class AST {
    // This class represents an Abstract Syntax Tree (AST) and provides methods to manipulate and traverse it.
    private Node rootNode;
    
    // Constructor to initialize the AST with a root node
    public AST(Node rootNode) {
        this.setRootNode(rootNode);
    }
    
    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }
    
    public Node getRootNode() {
        return this.rootNode;
    }
    
    public void standardize() {  
        if (!this.rootNode.isStandardized) {
            Standardizer.standardizeNode(this.rootNode);
        }
    }
    
    // Method to traverse the AST in pre-order and print the node data with indentation based on depth
    private void preOrderTraverse(Node node, int depth) {
        for (int i = 0; i < depth; i++) { 
            System.out.print(".");
        }
        System.out.println(node.getNodeData());
        // Recursively traverse each child node, increasing the depth by 1
        node.childNodes.forEach((child) -> preOrderTraverse(child, depth + 1));
    }
    
    public void printAST() {
        this.preOrderTraverse(this.getRootNode(), 0);
    }
}
