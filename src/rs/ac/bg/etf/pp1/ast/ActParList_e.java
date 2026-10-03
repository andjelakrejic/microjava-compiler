// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class ActParList_e extends ActParList {

    private ActParBegin ActParBegin;

    public ActParList_e (ActParBegin ActParBegin) {
        this.ActParBegin=ActParBegin;
        if(ActParBegin!=null) ActParBegin.setParent(this);
    }

    public ActParBegin getActParBegin() {
        return ActParBegin;
    }

    public void setActParBegin(ActParBegin ActParBegin) {
        this.ActParBegin=ActParBegin;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActParBegin!=null) ActParBegin.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParBegin!=null) ActParBegin.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParBegin!=null) ActParBegin.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ActParList_e(\n");

        if(ActParBegin!=null)
            buffer.append(ActParBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ActParList_e]");
        return buffer.toString();
    }
}
