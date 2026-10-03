// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class Statement_foreach extends Statement {

    private String I1;
    private Expr Expr;
    private ForeachStoreStart ForeachStoreStart;
    private Expr Expr1;
    private ForeachStoreEnd ForeachStoreEnd;
    private ForeachBegin ForeachBegin;
    private AllStatement AllStatement;

    public Statement_foreach (String I1, Expr Expr, ForeachStoreStart ForeachStoreStart, Expr Expr1, ForeachStoreEnd ForeachStoreEnd, ForeachBegin ForeachBegin, AllStatement AllStatement) {
        this.I1=I1;
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.ForeachStoreStart=ForeachStoreStart;
        if(ForeachStoreStart!=null) ForeachStoreStart.setParent(this);
        this.Expr1=Expr1;
        if(Expr1!=null) Expr1.setParent(this);
        this.ForeachStoreEnd=ForeachStoreEnd;
        if(ForeachStoreEnd!=null) ForeachStoreEnd.setParent(this);
        this.ForeachBegin=ForeachBegin;
        if(ForeachBegin!=null) ForeachBegin.setParent(this);
        this.AllStatement=AllStatement;
        if(AllStatement!=null) AllStatement.setParent(this);
    }

    public String getI1() {
        return I1;
    }

    public void setI1(String I1) {
        this.I1=I1;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public ForeachStoreStart getForeachStoreStart() {
        return ForeachStoreStart;
    }

    public void setForeachStoreStart(ForeachStoreStart ForeachStoreStart) {
        this.ForeachStoreStart=ForeachStoreStart;
    }

    public Expr getExpr1() {
        return Expr1;
    }

    public void setExpr1(Expr Expr1) {
        this.Expr1=Expr1;
    }

    public ForeachStoreEnd getForeachStoreEnd() {
        return ForeachStoreEnd;
    }

    public void setForeachStoreEnd(ForeachStoreEnd ForeachStoreEnd) {
        this.ForeachStoreEnd=ForeachStoreEnd;
    }

    public ForeachBegin getForeachBegin() {
        return ForeachBegin;
    }

    public void setForeachBegin(ForeachBegin ForeachBegin) {
        this.ForeachBegin=ForeachBegin;
    }

    public AllStatement getAllStatement() {
        return AllStatement;
    }

    public void setAllStatement(AllStatement AllStatement) {
        this.AllStatement=AllStatement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Expr!=null) Expr.accept(visitor);
        if(ForeachStoreStart!=null) ForeachStoreStart.accept(visitor);
        if(Expr1!=null) Expr1.accept(visitor);
        if(ForeachStoreEnd!=null) ForeachStoreEnd.accept(visitor);
        if(ForeachBegin!=null) ForeachBegin.accept(visitor);
        if(AllStatement!=null) AllStatement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
        if(ForeachStoreStart!=null) ForeachStoreStart.traverseTopDown(visitor);
        if(Expr1!=null) Expr1.traverseTopDown(visitor);
        if(ForeachStoreEnd!=null) ForeachStoreEnd.traverseTopDown(visitor);
        if(ForeachBegin!=null) ForeachBegin.traverseTopDown(visitor);
        if(AllStatement!=null) AllStatement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        if(ForeachStoreStart!=null) ForeachStoreStart.traverseBottomUp(visitor);
        if(Expr1!=null) Expr1.traverseBottomUp(visitor);
        if(ForeachStoreEnd!=null) ForeachStoreEnd.traverseBottomUp(visitor);
        if(ForeachBegin!=null) ForeachBegin.traverseBottomUp(visitor);
        if(AllStatement!=null) AllStatement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_foreach(\n");

        buffer.append(" "+tab+I1);
        buffer.append("\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForeachStoreStart!=null)
            buffer.append(ForeachStoreStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr1!=null)
            buffer.append(Expr1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForeachStoreEnd!=null)
            buffer.append(ForeachStoreEnd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForeachBegin!=null)
            buffer.append(ForeachBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AllStatement!=null)
            buffer.append(AllStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_foreach]");
        return buffer.toString();
    }
}
