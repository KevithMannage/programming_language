package Engine;

import Symbols.*;
import java.util.ArrayList;

import Standardizer.AST;
import Standardizer.Node;

public class CSEMachineFactory {
    private E e0 = new E(0);
    private int i = 1;
    private int j = 0;
    
    public CSEMachineFactory() {
        
    }
    
    public Symbol getSymbol(Node node) {
        switch (node.getNodeData()) {
            // unary operators
            case "not": 
            case "neg":
                return new Uop(node.getNodeData());
            // binary operators
            case "+": 
            case "-": 
            case "*": 
            case "/": 
            case "**":
            case "&":
            case "or": 
            case "eq": 
            case "ne": 
            case "ls": 
            case "le": 
            case "gr": 
            case "ge":              
            case "aug":
                return new Bop(node.getNodeData());
            // gamma
            case "gamma":
                return new Gamma();
            // tau
            case "tau":
                return new Tau(node.childNodes.size());
            // ystar
            case "<Y*>": 
                return new Ystar();
            // operands <ID:>, <INT:>, <STR:>, <nil>, <true>, <false>, <dummy>
            default:
                if (node.getNodeData().startsWith("<ID:")) { 
                    return new Id(node.getNodeData().substring(4, node.getNodeData().length()-1));
                } else if (node.getNodeData().startsWith("<INT:")) {                    
                    return new Int(node.getNodeData().substring(5, node.getNodeData().length()-1));
                } else if (node.getNodeData().startsWith("<STR:")) {                    
                    return new Str(node.getNodeData().substring(6, node.getNodeData().length()-2));
                } else if (node.getNodeData().startsWith("<nil")) {                    
                    return new Tup();
                } else if (node.getNodeData().startsWith("<true>")) {                    
                    return new Bool("true");
                } else if (node.getNodeData().startsWith("<false>")) {                    
                    return new Bool("false");
                } else if (node.getNodeData().startsWith("<dummy>")) {                    
                    return new Dummy();
                } else {
                    System.out.println("Err node: "+node.getNodeData());
                    return new Err();
                }          
        }
    }
    
    public B getB(Node node) {
        B b = new B();
        b.symbols = this.getPreOrderTraverse(node);
        return b;
    }
    
    public Lambda getLambda(Node node) {
        Lambda lambda = new Lambda(this.i++);
        lambda.setDelta(this.getDelta(node.childNodes.get(1)));
        if (",".equals(node.childNodes.get(0).getNodeData())) {
            for (Node identifier: node.childNodes.get(0).childNodes) {
                lambda.identifiers.add(new Id(identifier.getNodeData().substring(4, node.getNodeData().length()-1)));
            }
        } else {
            lambda.identifiers.add(new Id(node.childNodes.get(0).getNodeData().substring(4, node.childNodes.get(0).getNodeData().length()-1)));
        }
        return lambda;
    }
    
    private ArrayList<Symbol> getPreOrderTraverse(Node node) {
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();
        if ("lambda".equals(node.getNodeData())) {
            symbols.add(this.getLambda(node));
        } else if ("->".equals(node.getNodeData())) {
            symbols.add(this.getDelta(node.childNodes.get(1)));
            symbols.add(this.getDelta(node.childNodes.get(2)));
            symbols.add(new Beta());
            symbols.add(this.getB(node.childNodes.get(0)));
        } else {
            symbols.add(this.getSymbol(node));
            for (Node child: node.childNodes) {
                symbols.addAll(this.getPreOrderTraverse(child));
            }
        }
        return symbols;
    }
    
    public Delta getDelta(Node node) {
        Delta delta = new Delta(this.j++);
        delta.symbols = this.getPreOrderTraverse(node);
        return delta;        
    }
    
    public ArrayList<Symbol> getControl(AST ast) {
        ArrayList<Symbol> control = new ArrayList<Symbol>();
        control.add(this.e0);
        control.add(this.getDelta(ast.getRootNode()));
        return control;
    }
    
    public ArrayList<Symbol> getStack() {
        ArrayList<Symbol> stack = new ArrayList<Symbol>();
        stack.add(this.e0);
        return stack;
    }
    
    public ArrayList<E> getEnvironment() {
        ArrayList<E> environment = new ArrayList<E>();
        environment.add(this.e0);
        return environment;
    }
    
    public CSEMachine getCSEMachine(AST ast) {        
        return new CSEMachine(this.getControl(ast), this.getStack(), this.getEnvironment());
    }
}
