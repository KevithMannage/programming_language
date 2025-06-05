package Standardizer;

import java.util.ArrayList;

public class Node {

    // This class represents a node in the Abstract Syntax Tree (AST).
    private String nodeData; 
    private int nodeDepth; 
    private Node parentNode; 
    public ArrayList<Node> childNodes;
    public boolean isStandardized = false;

    public Node() {
    }

    // Getter and setter for nodeData
    public String getNodeData() {
        return this.nodeData;
    }

    public void setNodeData(String nodeData) {
        this.nodeData = nodeData;
    }

    // Getter and setter for nodeDepth
    public int getNodeDepth() {
        return this.nodeDepth;
    }

    public void setNodeDepth(int nodeDepth) {
        this.nodeDepth = nodeDepth;
    }

    // Getter and setter for parentNode
    public Node getParentNode() {
        return this.parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    // Method to get number of children
    public int getDegree() {
        return childNodes.size();
    }

}
