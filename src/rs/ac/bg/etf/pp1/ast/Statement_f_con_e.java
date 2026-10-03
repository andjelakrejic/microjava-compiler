// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class Statement_f_con_e extends Statement_f_con {

    public Statement_f_con_e () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_f_con_e(\n");

        buffer.append(tab);
        buffer.append(") [Statement_f_con_e]");
        return buffer.toString();
    }
}
