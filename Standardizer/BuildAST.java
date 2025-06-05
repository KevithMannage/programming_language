package Standardizer;

import java.util.ArrayList;

public class BuildAST {

    // This class is responsible for building an Abstract Syntax Tree (AST) from a list of strings.
    public BuildAST() {
    }

    public AST buildAbstractSyntaxTree(ArrayList<String> inputData) {
        Node rootNode = NodeFactory.createNode(inputData.get(0), 0);
        Node previousNode = rootNode;
        int currentDepth = 0;

        for (String item : inputData.subList(1, inputData.size())) {
            int index = 0;
            int depth = 0;

            // Count leading dots to determine the depth of the node
            while (item.charAt(index) == '.') {
                depth++;
                index++;
            }

            // If the item is empty after leading dots, skip it
            Node currentNode = NodeFactory.createNode(item.substring(index), depth);

            if (currentDepth < depth) {
                previousNode.childNodes.add(currentNode);
                currentNode.setParentNode(previousNode);
            } else {
                while (previousNode.getNodeDepth() != depth) {
                    previousNode = previousNode.getParentNode();
                }
                previousNode.getParentNode().childNodes.add(currentNode);
                currentNode.setParentNode(previousNode.getParentNode());
            }

            // Update the previous node and current depth for the next iteration
            previousNode = currentNode;
            currentDepth = depth;
        }

        return new AST(rootNode);
    }
}
