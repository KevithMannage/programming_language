package Engine;
import Symbols.*;
import java.util.ArrayList;

public class CSEMachine {
    private ArrayList<Symbol> control;
    private ArrayList<Symbol> stack;
    private ArrayList<E> environment;

    public CSEMachine(ArrayList<Symbol> control, ArrayList<Symbol> stack, ArrayList<E> environment) {
        this.setControl(control);
        this.setStack(stack);
        this.setEnvironment(environment);
    }  
    
    public void setControl(ArrayList<Symbol> control) {
        this.control = control;
    }
    
    public void setStack(ArrayList<Symbol> stack) {
        this.stack = stack;
    }
    
    public void setEnvironment(ArrayList<E> environment) {
        this.environment = environment;
    }
    
    public void execute() {
        E currentEnvironment = this.environment.get(0);
        int j = 1;
        while (!control.isEmpty()) {
//        	printControl();
//	        printStack();
//	        printEnvironment();
            // pop last element of the control
            Symbol currentSymbol = control.get(control.size()-1);
            control.remove(control.size()-1);            
            // rule no. 1
            if (currentSymbol instanceof Id) {
                this.stack.add(0, currentEnvironment.lookup((Id) currentSymbol));
            // rule no. 2
            } else if (currentSymbol instanceof Lambda) {
                Lambda lambda = (Lambda) currentSymbol;
                lambda.setEnvironment(currentEnvironment.getIndex());
                this.stack.add(0, lambda);
            // rule no. 3, 4, 10, 11, 12 & 13
            } else if (currentSymbol instanceof Gamma) {
                Symbol nextSymbol = this.stack.get(0); // Symbol at the top of stack
                this.stack.remove(0);
                // lambda (rule no. 4 & 11)
                if (nextSymbol instanceof Lambda) {
                    Lambda lambda = (Lambda) nextSymbol;
                    E e = new E(j++);
                    if (lambda.identifiers.size() == 1) {
                        e.values.put(lambda.identifiers.get(0), this.stack.get(0));
                        this.stack.remove(0);
                    } else {
                        Tup tup = (Tup) this.stack.get(0);
                        this.stack.remove(0);
                        int i = 0;
                        for (Id id: lambda.identifiers) {
                            e.values.put(id, tup.symbols.get(i++));
                        }
                    }
                    for (E environment: this.environment) {
                        if (environment.getIndex() == lambda.getEnvironment()) {
                            e.setParent(environment);
                        }
                    }        
                    currentEnvironment = e;
                    this.control.add(e);
                    this.control.add(lambda.getDelta());
                    this.stack.add(0, e);
                    this.environment.add(e);
                // tup (rule no. 10)
                } else if (nextSymbol instanceof Tup) {
                    Tup tup = (Tup) nextSymbol;
                    int i = Integer.parseInt(this.stack.get(0).getNodeData());
                    this.stack.remove(0);
                    this.stack.add(0, tup.symbols.get(i-1));
                // ystar (rule no. 12)
                } else if (nextSymbol instanceof Ystar) {
                    Lambda lambda = (Lambda) this.stack.get(0);
                    this.stack.remove(0);
                    Eta eta = new Eta();
                    eta.setIndex(lambda.getIndex());
                    eta.setEnvironment(lambda.getEnvironment());
                    eta.setIdentifier(lambda.identifiers.get(0));
                    eta.setLambda(lambda);
                    this.stack.add(0, eta);
                // eta (rule no. 13)
                } else if (nextSymbol instanceof Eta) {
                    Eta eta = (Eta) nextSymbol;
                    Lambda lambda = eta.getLambda();
                    this.control.add(new Gamma());
                    this.control.add(new Gamma());
                    this.stack.add(0, eta);
                    this.stack.add(0, lambda);
                // builtin functions
                } else {
                    if ("Print".equals(nextSymbol.getNodeData())) {
                        // do nothing
                    } else if ("Stem".equals(nextSymbol.getNodeData())) {
                        Symbol s = this.stack.get(0);
                        this.stack.remove(0);
                        s.setData(s.getNodeData().substring(0, 1));
                        this.stack.add(0, s);
                    } else if ("Stern".equals(nextSymbol.getNodeData())) {
                        Symbol s = this.stack.get(0);
                        this.stack.remove(0);
                        s.setData(s.getNodeData().substring(1));
                        this.stack.add(0, s);
                    } else if ("Conc".equals(nextSymbol.getNodeData())) {
                        Symbol s1 = this.stack.get(0);
                        Symbol s2 = this.stack.get(1);
                        this.stack.remove(0);
                        this.stack.remove(0);
                        s1.setData(s1.getNodeData() + s2.getNodeData());
                        this.stack.add(0, s1);                                          
                    } else if ("Order".equals(nextSymbol.getNodeData())) {
                        Tup tup = (Tup) this.stack.get(0);
                        this.stack.remove(0);
                        Int n = new Int(Integer.toString(tup.symbols.size()));
                        this.stack.add(0, n);
                    } else if ("Null".equals(nextSymbol.getNodeData())) {
                        // implement
                    } else if ("Itos".equals(nextSymbol.getNodeData())) {
                        // implement
                    } else if ("Isinteger".equals(nextSymbol.getNodeData())) {
                        if (this.stack.get(0) instanceof Int) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    } else if ("Isstring".equals(nextSymbol.getNodeData())) {
                        if (this.stack.get(0) instanceof Str) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);                        
                    } else if ("Istuple".equals(nextSymbol.getNodeData())) {
                        if (this.stack.get(0) instanceof Tup) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    } else if ("Isdummy".equals(nextSymbol.getNodeData())) {
                        if (this.stack.get(0) instanceof Dummy) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    } else if ("Istruthvalue".equals(nextSymbol.getNodeData())) {
                        if (this.stack.get(0) instanceof Bool) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    } else if ("Isfunction".equals(nextSymbol.getNodeData())) {
                        if (this.stack.get(0) instanceof Lambda) {
                            this.stack.add(0, new Bool("true"));
                        } else {
                            this.stack.add(0, new Bool("false"));
                        }
                        this.stack.remove(1);
                    }
                }
            // rule no. 5
            } else if (currentSymbol instanceof E) {     
//            	System.out.println(this.stack.size());
//            	System.out.println(this.stack.get(0).getNodeData());


                this.stack.remove(1);
                
                this.environment.get(((E) currentSymbol).getIndex()).setIsRemoved(true);
                int y = this.environment.size();
                while (y > 0) {
                    if (!this.environment.get(y-1).getIsRemoved()) {
                        currentEnvironment = this.environment.get(y-1);
                        break;
                    } else {
                        y--;
                    }
                }
            // rule no. 6 & 7
            } else if (currentSymbol instanceof Rator) {
                if (currentSymbol instanceof Uop) {
                    Symbol rator = currentSymbol;
                    Symbol rand = this.stack.get(0);
                    this.stack.remove(0);
                    stack.add(0, this.applyUnaryOperation(rator, rand));
                }
                if (currentSymbol instanceof Bop) {
                    Symbol rator = currentSymbol;
                    Symbol rand1 = this.stack.get(0);
                    Symbol rand2 = this.stack.get(1);
//                    System.out.println(rand2);
                    this.stack.remove(0);
                    this.stack.remove(0);
                    this.stack.add(0, this.applyBinaryOperation(rator, rand1, rand2));
                }
            // rule no. 8
            } else if (currentSymbol instanceof Beta) {
                if (Boolean.parseBoolean(this.stack.get(0).getNodeData())) {
                    this.control.remove(control.size()-1); 
                } else {
                    this.control.remove(control.size()-2); 
                }
                this.stack.remove(0);
            // rule no. 9
            } else if (currentSymbol instanceof Tau) {
                Tau tau = (Tau) currentSymbol;
                Tup tup = new Tup();
                for (int i = 0; i < tau.getN(); i++) {
                    tup.symbols.add(this.stack.get(0));
                    this.stack.remove(0);
                }
                this.stack.add(0, tup);
            } else if (currentSymbol instanceof Delta) {
                this.control.addAll(((Delta) currentSymbol).symbols);
            } else if (currentSymbol instanceof B) {
                this.control.addAll(((B) currentSymbol).symbols);
            } else {
                this.stack.add(0, currentSymbol);
            }
        }   
    }
    
    public void printControl() {
        System.out.print("Control: ");
        for (Symbol symbol: this.control) {
            System.out.print(symbol.getNodeData());
            if (symbol instanceof Lambda) {
                System.out.print(((Lambda) symbol).getIndex());
            } else if (symbol instanceof Delta) {
                System.out.print(((Delta) symbol).getIndex());
            } else if (symbol instanceof E) {
                System.out.print(((E) symbol).getIndex());
            } else if (symbol instanceof Eta) {
                System.out.print(((Eta) symbol).getIndex());
            }
            System.out.print(",");
        }
        System.out.println();
    }
    
    public void printStack() {
        System.out.print("Stack: ");
        for (Symbol symbol: this.stack) {
            System.out.print(symbol.getNodeData());
            if (symbol instanceof Lambda) {
                System.out.print(((Lambda) symbol).getIndex());
            } else if (symbol instanceof Delta) {
                System.out.print(((Delta) symbol).getIndex());
            } else if (symbol instanceof E) {
                System.out.print(((E) symbol).getIndex());
            } else if (symbol instanceof Eta) {
                System.out.print(((Eta) symbol).getIndex());
            }
            System.out.print(",");
        }
        System.out.println();
    }
    //print the environment
    // e0 is the global environment
    public void printEnvironment() {
        for (Symbol symbol: this.environment) {
            System.out.print("e"+((E) symbol).getIndex()+ " --> ");
            if (((E) symbol).getIndex()!=0) {
                System.out.println("e"+((E) symbol).getParent().getIndex());
            } else {
                System.out.println();
            }
        }
    }
    //apply unary and binary operations
    public Symbol applyUnaryOperation(Symbol rator, Symbol rand) {
        if ("neg".equals(rator.getNodeData())) {
            int val = Integer.parseInt(rand.getNodeData());
            return new Int(Integer.toString(-1*val));
        } else if ("not".equals(rator.getNodeData())) {
            boolean val = Boolean.parseBoolean(rand.getNodeData());
            return new Bool(Boolean.toString(!val));
        } else {
            return new Err();
        }
    }
    
    public Symbol applyBinaryOperation(Symbol rator, Symbol rand1, Symbol rand2) {
        if ("+".equals(rator.getNodeData())) {
            int val1 = Integer.parseInt(rand1.getNodeData());
            int val2 = Integer.parseInt(rand2.getNodeData());
            return new Int(Integer.toString(val1+val2));
        } else if ("-".equals(rator.getNodeData())) {
            int val1 = Integer.parseInt(rand1.getNodeData());
            int val2 = Integer.parseInt(rand2.getNodeData());
            return new Int(Integer.toString(val1-val2));
        } else if ("*".equals(rator.getNodeData())) {
            int val1 = Integer.parseInt(rand1.getNodeData());
            int val2 = Integer.parseInt(rand2.getNodeData());
            return new Int(Integer.toString(val1*val2));
        } else if ("/".equals(rator.getNodeData())) {
            int val1 = Integer.parseInt(rand1.getNodeData());
            int val2 = Integer.parseInt(rand2.getNodeData());
            return new Int(Integer.toString(val1/val2));
        } else if ("**".equals(rator.getNodeData())) {
            int val1 = Integer.parseInt(rand1.getNodeData());
            int val2 = Integer.parseInt(rand2.getNodeData());
            return new Int(Integer.toString((int) Math.pow(val1, val2)));
        } else if ("&".equals(rator.getNodeData())) {            
            boolean val1 = Boolean.parseBoolean(rand1.getNodeData());
            boolean val2 = Boolean.parseBoolean(rand2.getNodeData());
            return new Bool(Boolean.toString(val1 && val2));
        } else if ("or".equals(rator.getNodeData())) {            
            boolean val1 = Boolean.parseBoolean(rand1.getNodeData());
            boolean val2 = Boolean.parseBoolean(rand2.getNodeData());
            return new Bool(Boolean.toString(val1 || val2));
        } else if ("eq".equals(rator.getNodeData())) {            
            String val1 = rand1.getNodeData();
            String val2 = rand2.getNodeData();
            return new Bool(Boolean.toString(val1.equals(val2)));
        } else if ("ne".equals(rator.getNodeData())) {            
            String val1 = rand1.getNodeData();
            String val2 = rand2.getNodeData();
            return new Bool(Boolean.toString(!val1.equals(val2)));
        } else if ("ls".equals(rator.getNodeData())) {            
            int val1 = Integer.parseInt(rand1.getNodeData());
            int val2 = Integer.parseInt(rand2.getNodeData());
            return new Bool(Boolean.toString(val1 < val2));
        } else if ("le".equals(rator.getNodeData())) {            
            int val1 = Integer.parseInt(rand1.getNodeData());
            String s1=rand2.getNodeData();
//            System.out.println(s1);
            int val2 = Integer.parseInt(s1);
            return new Bool(Boolean.toString(val1 <= val2));
        } else if ("gr".equals(rator.getNodeData())) {            
            int val1 = Integer.parseInt(rand1.getNodeData());
            int val2 = Integer.parseInt(rand2.getNodeData());
            return new Bool(Boolean.toString(val1 > val2));
        } else if ("ge".equals(rator.getNodeData())) {            
            int val1 = Integer.parseInt(rand1.getNodeData());
            int val2 = Integer.parseInt(rand2.getNodeData());
            return new Bool(Boolean.toString(val1 >= val2));
        } else if ("aug".equals(rator.getNodeData())) {  
            if (rand2 instanceof Tup) {
                ((Tup) rand1).symbols.addAll(((Tup) rand2).symbols);
            } else {
                ((Tup) rand1).symbols.add(rand2);
            }
            return rand1;
        } else {
            return new Err();
        }
    }
    

public String getTupleValue(Tup tup) {
    StringBuilder temp = new StringBuilder("(");
    boolean first = true;
    for (Symbol symbol : tup.symbols) {
        if (symbol instanceof Tup) {
            String inner = this.getTupleValue((Tup) symbol);
            // Remove outer parentheses from inner tuple
            inner = inner.substring(1, inner.length() - 1);
            if (!inner.isEmpty()) {
                if (!first) temp.append(", ");
                temp.append(inner);
                first = false;
            }
        } else if (!symbol.getNodeData().isBlank()) {
            if (!first) temp.append(", ");
            temp.append(symbol.getNodeData());
            first = false;
        }
    }
    temp.append(")");
    return temp.toString();
}
    // get the answer from the stack
    public String getAnswer() {
        this.execute();
        if (stack.get(0) instanceof Tup) {
            return this.getTupleValue((Tup) stack.get(0));
        }
        return stack.get(0).getNodeData();
    }
}
