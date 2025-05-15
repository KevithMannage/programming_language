package Symbols;

public class Symbol {
    protected String data;
    
    public Symbol(String data) {
        this.data = data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public String getNodeData() {
        return this.data;
    }
}
