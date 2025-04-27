package Standardizer;

import java.util.ArrayList;

public class Standardizer {

    public static void standardizeNode(Node currentNode) {
        if (!currentNode.isStandardized) {
            for (Node child : currentNode.childNodes) {
                standardizeNode(child);
            }
            switch (currentNode.getNodeData()) {
                case "let":
                    standardizeLet(currentNode);
                    break;
                case "where":
                    standardizeWhere(currentNode);
                    break;
                case "function_form":
                    standardizeFunctionForm(currentNode);
                    break;
                case "lambda":
                    standardizeLambda(currentNode);
                    break;
                case "within":
                    standardizeWithin(currentNode);
                    break;
                case "@":
                    standardizeAt(currentNode);
                    break;
                case "and":
                    standardizeAnd(currentNode);
                    break;
                case "rec":
                    standardizeRec(currentNode);
                    break;
                default:
                    break;
            }
        }
        currentNode.isStandardized = true;
    }

    private static void standardizeLet(Node currentNode) {
        Node temp1 = currentNode.childNodes.get(0).childNodes.get(1);
        temp1.setParentNode(currentNode);
        temp1.setNodeDepth(currentNode.getNodeDepth() + 1);
        Node temp2 = currentNode.childNodes.get(1);                    
        temp2.setParentNode(currentNode.childNodes.get(0));
        temp2.setNodeDepth(currentNode.getNodeDepth() + 2);
        currentNode.childNodes.set(1, temp1);
        currentNode.childNodes.get(0).setNodeData("lambda");
        currentNode.childNodes.get(0).childNodes.set(1, temp2);
        currentNode.setNodeData("gamma");
    }

    private static void standardizeWhere(Node currentNode) {
        Node temp = currentNode.childNodes.get(0);
        currentNode.childNodes.set(0, currentNode.childNodes.get(1));
        currentNode.childNodes.set(1, temp);
        currentNode.setNodeData("let");
        standardizeNode(currentNode);
    }

    private static void standardizeFunctionForm(Node currentNode) {
        Node expression = currentNode.childNodes.get(currentNode.childNodes.size() - 1);                    
        Node currentLambda = NodeFactory.createNode("lambda", currentNode.getNodeDepth() + 1, currentNode, new ArrayList<Node>(), true);
        currentNode.childNodes.add(1, currentLambda);
        while (!currentNode.childNodes.get(2).equals(expression)) {
            Node variable = currentNode.childNodes.get(2);
            currentNode.childNodes.remove(2);
            variable.setNodeDepth(currentLambda.getNodeDepth() + 1);
            variable.setParentNode(currentLambda);
            currentLambda.childNodes.add(variable);
            if (currentNode.childNodes.size() > 3) {
                currentLambda = NodeFactory.createNode("lambda", currentLambda.getNodeDepth() + 1, currentLambda, new ArrayList<Node>(), true);
                currentLambda.getParentNode().childNodes.add(currentLambda);
            }
        }
        currentLambda.childNodes.add(expression);
        currentNode.childNodes.remove(2);
        currentNode.setNodeData("=");
    }

    private static void standardizeLambda(Node currentNode) {
        if (currentNode.childNodes.size() > 2) {
            Node lastChild = currentNode.childNodes.get(currentNode.childNodes.size() - 1);
            Node currentLambda = NodeFactory.createNode("lambda", currentNode.getNodeDepth() + 1, currentNode, new ArrayList<Node>(), true);
            currentNode.childNodes.add(1, currentLambda);
            while (!currentNode.childNodes.get(2).equals(lastChild)) {
                Node variable = currentNode.childNodes.get(2);
                currentNode.childNodes.remove(2);
                variable.setNodeDepth(currentLambda.getNodeDepth() + 1);
                variable.setParentNode(currentLambda);
                currentLambda.childNodes.add(variable);
                if (currentNode.childNodes.size() > 3) {
                    currentLambda = NodeFactory.createNode("lambda", currentLambda.getNodeDepth() + 1, currentLambda, new ArrayList<Node>(), true);
                    currentLambda.getParentNode().childNodes.add(currentLambda);
                }
            }
            currentLambda.childNodes.add(lastChild);
            currentNode.childNodes.remove(2);
        }
    }

    private static void standardizeWithin(Node currentNode) {
        Node firstX = currentNode.childNodes.get(0).childNodes.get(0);                    
        Node secondX = currentNode.childNodes.get(1).childNodes.get(0);
        Node firstE = currentNode.childNodes.get(0).childNodes.get(1);
        Node secondE = currentNode.childNodes.get(1).childNodes.get(1);
        Node gamma = NodeFactory.createNode("gamma", currentNode.getNodeDepth() + 1, currentNode, new ArrayList<Node>(), true);
        Node lambda = NodeFactory.createNode("lambda", currentNode.getNodeDepth() + 2, gamma, new ArrayList<Node>(), true);                    
        firstX.setNodeDepth(firstX.getNodeDepth() + 1);
        firstX.setParentNode(lambda);                    
        secondX.setNodeDepth(firstX.getNodeDepth() - 1);
        secondX.setParentNode(currentNode);                    
        firstE.setNodeDepth(firstE.getNodeDepth());
        firstE.setParentNode(gamma);                    
        secondE.setNodeDepth(secondE.getNodeDepth() + 1);
        secondE.setParentNode(lambda);                    
        lambda.childNodes.add(firstX);
        lambda.childNodes.add(secondE);
        gamma.childNodes.add(lambda);
        gamma.childNodes.add(firstE);
        currentNode.childNodes.clear();
        currentNode.childNodes.add(secondX);
        currentNode.childNodes.add(gamma);
        currentNode.setNodeData("=");
    }

    private static void standardizeAt(Node currentNode) {
        Node gamma1 = NodeFactory.createNode("gamma", currentNode.getNodeDepth() + 1, currentNode, new ArrayList<Node>(), true);
        Node firstChild = currentNode.childNodes.get(0);
        firstChild.setNodeDepth(firstChild.getNodeDepth() + 1);
        firstChild.setParentNode(gamma1);
        Node secondChild = currentNode.childNodes.get(1);
        secondChild.setNodeDepth(secondChild.getNodeDepth() + 1);
        secondChild.setParentNode(gamma1);
        gamma1.childNodes.add(secondChild);
        gamma1.childNodes.add(firstChild);
        currentNode.childNodes.remove(0);
        currentNode.childNodes.remove(0);
        currentNode.childNodes.add(0, gamma1);                    
        currentNode.setNodeData("gamma");
    }

    private static void standardizeAnd(Node currentNode) {
        Node comma = NodeFactory.createNode(",", currentNode.getNodeDepth() + 1, currentNode, new ArrayList<Node>(), true);
        Node tau = NodeFactory.createNode("tau", currentNode.getNodeDepth() + 1, currentNode, new ArrayList<Node>(), true);
        for (Node equal : currentNode.childNodes) {
            equal.childNodes.get(0).setParentNode(comma);
            equal.childNodes.get(1).setParentNode(tau);
            comma.childNodes.add(equal.childNodes.get(0));
            tau.childNodes.add(equal.childNodes.get(1));
        }
        currentNode.childNodes.clear();
        currentNode.childNodes.add(comma);
        currentNode.childNodes.add(tau);
        currentNode.setNodeData("=");
    }

    private static void standardizeRec(Node currentNode) {
        Node firstX = currentNode.childNodes.get(0).childNodes.get(0);
        Node firstE = currentNode.childNodes.get(0).childNodes.get(1);
        Node functionNode = NodeFactory.createNode(firstX.getNodeData(), currentNode.getNodeDepth() + 1, currentNode, firstX.childNodes, true);
        Node gamma = NodeFactory.createNode("gamma", currentNode.getNodeDepth() + 1, currentNode, new ArrayList<Node>(), true);
        Node Y = NodeFactory.createNode("<Y*>", currentNode.getNodeDepth() + 2, gamma, new ArrayList<Node>(), true);
        Node lambda = NodeFactory.createNode("lambda", currentNode.getNodeDepth() + 2, gamma, new ArrayList<Node>(), true);                    
        firstX.setNodeDepth(lambda.getNodeDepth() + 1);
        firstX.setParentNode(lambda);
        firstE.setNodeDepth(lambda.getNodeDepth() + 1);
        firstE.setParentNode(lambda);
        lambda.childNodes.add(firstX);
        lambda.childNodes.add(firstE);
        gamma.childNodes.add(Y);
        gamma.childNodes.add(lambda);
        currentNode.childNodes.clear();
        currentNode.childNodes.add(functionNode);
        currentNode.childNodes.add(gamma);
        currentNode.setNodeData("=");
    }
}
