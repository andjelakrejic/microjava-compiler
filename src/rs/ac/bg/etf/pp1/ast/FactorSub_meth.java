// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class FactorSub_meth extends FactorSub {

    private Designator Designator;
    private ActParBegin ActParBegin;
    private ActParList ActParList;

    public FactorSub_meth (Designator Designator, ActParBegin ActParBegin, ActParList ActParList) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.ActParBegin=ActParBegin;
        if(ActParBegin!=null) ActParBegin.setParent(this);
        this.ActParList=ActParList;
        if(ActParList!=null) ActParList.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public ActParBegin getActParBegin() {
        return ActParBegin;
    }

    public void setActParBegin(ActParBegin ActParBegin) {
        this.ActParBegin=ActParBegin;
    }

    public ActParList getActParList() {
        return ActParList;
    }

    public void setActParList(ActParList ActParList) {
        this.ActParList=ActParList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(ActParBegin!=null) ActParBegin.accept(visitor);
        if(ActParList!=null) ActParList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(ActParBegin!=null) ActParBegin.traverseTopDown(visitor);
        if(ActParList!=null) ActParList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(ActParBegin!=null) ActParBegin.traverseBottomUp(visitor);
        if(ActParList!=null) ActParList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorSub_meth(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ActParBegin!=null)
            buffer.append(ActParBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ActParList!=null)
            buffer.append(ActParList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorSub_meth]");
        return buffer.toString();
    }
}
