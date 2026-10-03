// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class ActParList_rec extends ActParList {

    private ActParBegin ActParBegin;
    private ActPar ActPar;
    private ActParMore ActParMore;

    public ActParList_rec (ActParBegin ActParBegin, ActPar ActPar, ActParMore ActParMore) {
        this.ActParBegin=ActParBegin;
        if(ActParBegin!=null) ActParBegin.setParent(this);
        this.ActPar=ActPar;
        if(ActPar!=null) ActPar.setParent(this);
        this.ActParMore=ActParMore;
        if(ActParMore!=null) ActParMore.setParent(this);
    }

    public ActParBegin getActParBegin() {
        return ActParBegin;
    }

    public void setActParBegin(ActParBegin ActParBegin) {
        this.ActParBegin=ActParBegin;
    }

    public ActPar getActPar() {
        return ActPar;
    }

    public void setActPar(ActPar ActPar) {
        this.ActPar=ActPar;
    }

    public ActParMore getActParMore() {
        return ActParMore;
    }

    public void setActParMore(ActParMore ActParMore) {
        this.ActParMore=ActParMore;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActParBegin!=null) ActParBegin.accept(visitor);
        if(ActPar!=null) ActPar.accept(visitor);
        if(ActParMore!=null) ActParMore.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParBegin!=null) ActParBegin.traverseTopDown(visitor);
        if(ActPar!=null) ActPar.traverseTopDown(visitor);
        if(ActParMore!=null) ActParMore.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParBegin!=null) ActParBegin.traverseBottomUp(visitor);
        if(ActPar!=null) ActPar.traverseBottomUp(visitor);
        if(ActParMore!=null) ActParMore.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ActParList_rec(\n");

        if(ActParBegin!=null)
            buffer.append(ActParBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ActPar!=null)
            buffer.append(ActPar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ActParMore!=null)
            buffer.append(ActParMore.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ActParList_rec]");
        return buffer.toString();
    }
}
