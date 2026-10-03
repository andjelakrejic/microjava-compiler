// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class CondFact_rel extends CondFact {

    private ExprBasic ExprBasic;
    private Relop Relop;
    private ExprBasic ExprBasic1;

    public CondFact_rel (ExprBasic ExprBasic, Relop Relop, ExprBasic ExprBasic1) {
        this.ExprBasic=ExprBasic;
        if(ExprBasic!=null) ExprBasic.setParent(this);
        this.Relop=Relop;
        if(Relop!=null) Relop.setParent(this);
        this.ExprBasic1=ExprBasic1;
        if(ExprBasic1!=null) ExprBasic1.setParent(this);
    }

    public ExprBasic getExprBasic() {
        return ExprBasic;
    }

    public void setExprBasic(ExprBasic ExprBasic) {
        this.ExprBasic=ExprBasic;
    }

    public Relop getRelop() {
        return Relop;
    }

    public void setRelop(Relop Relop) {
        this.Relop=Relop;
    }

    public ExprBasic getExprBasic1() {
        return ExprBasic1;
    }

    public void setExprBasic1(ExprBasic ExprBasic1) {
        this.ExprBasic1=ExprBasic1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprBasic!=null) ExprBasic.accept(visitor);
        if(Relop!=null) Relop.accept(visitor);
        if(ExprBasic1!=null) ExprBasic1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprBasic!=null) ExprBasic.traverseTopDown(visitor);
        if(Relop!=null) Relop.traverseTopDown(visitor);
        if(ExprBasic1!=null) ExprBasic1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprBasic!=null) ExprBasic.traverseBottomUp(visitor);
        if(Relop!=null) Relop.traverseBottomUp(visitor);
        if(ExprBasic1!=null) ExprBasic1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CondFact_rel(\n");

        if(ExprBasic!=null)
            buffer.append(ExprBasic.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Relop!=null)
            buffer.append(Relop.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExprBasic1!=null)
            buffer.append(ExprBasic1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CondFact_rel]");
        return buffer.toString();
    }
}
