package Standardizer;

import java.util.ArrayList;

public class NodeFactory {

    // This class is responsible for creating Node instances for the Abstract Syntax Tree (AST).
    public NodeFactory() {
    }

    // Factory method to create a Node with only nodeData and nodeDepth
    public static Node createNode(String nodeData, int nodeDepth) {
        Node node = new Node();
        node.setNodeData(nodeData);
        node.setNodeDepth(nodeDepth);
        node.childNodes = new ArrayList<Node>();
        return node;
    }

    // Factory method to create a Node with nodeData, nodeDepth, and parentNode
    public static Node createNode(String nodeData, int nodeDepth, Node parentNode, ArrayList<Node> childNodes, boolean isStandardized) {
        Node node = new Node();
        node.setNodeData(nodeData);
        node.setNodeDepth(nodeDepth);
        node.setParentNode(parentNode);
        node.childNodes = childNodes;
        node.isStandardized = isStandardized;
        return node;
    }
}
