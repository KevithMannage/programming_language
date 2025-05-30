package Standardizer;

public class AST {
    private Node rootNode;
    
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
    
    private void preOrderTraverse(Node node, int depth) {
        for (int i = 0; i < depth; i++) { 
            System.out.print(".");
        }
        System.out.println(node.getNodeData());
        node.childNodes.forEach((child) -> preOrderTraverse(child, depth + 1));
    }
    
    public void printAST() {
        this.preOrderTraverse(this.getRootNode(), 0);
    }
}
