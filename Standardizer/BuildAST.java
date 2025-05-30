package Standardizer;

import java.util.ArrayList;

public class BuildAST {

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

            previousNode = currentNode;
            currentDepth = depth;
        }

        return new AST(rootNode);
    }
}
