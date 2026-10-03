// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class Statement_while extends Statement {

    private WhileCond WhileCond;
    private Condition Condition;
    private WhileBegin WhileBegin;
    private AllStatement AllStatement;

    public Statement_while (WhileCond WhileCond, Condition Condition, WhileBegin WhileBegin, AllStatement AllStatement) {
        this.WhileCond=WhileCond;
        if(WhileCond!=null) WhileCond.setParent(this);
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
        this.WhileBegin=WhileBegin;
        if(WhileBegin!=null) WhileBegin.setParent(this);
        this.AllStatement=AllStatement;
        if(AllStatement!=null) AllStatement.setParent(this);
    }

    public WhileCond getWhileCond() {
        return WhileCond;
    }

    public void setWhileCond(WhileCond WhileCond) {
        this.WhileCond=WhileCond;
    }

    public Condition getCondition() {
        return Condition;
    }

    public void setCondition(Condition Condition) {
        this.Condition=Condition;
    }

    public WhileBegin getWhileBegin() {
        return WhileBegin;
    }

    public void setWhileBegin(WhileBegin WhileBegin) {
        this.WhileBegin=WhileBegin;
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
        if(WhileCond!=null) WhileCond.accept(visitor);
        if(Condition!=null) Condition.accept(visitor);
        if(WhileBegin!=null) WhileBegin.accept(visitor);
        if(AllStatement!=null) AllStatement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(WhileCond!=null) WhileCond.traverseTopDown(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
        if(WhileBegin!=null) WhileBegin.traverseTopDown(visitor);
        if(AllStatement!=null) AllStatement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(WhileCond!=null) WhileCond.traverseBottomUp(visitor);
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        if(WhileBegin!=null) WhileBegin.traverseBottomUp(visitor);
        if(AllStatement!=null) AllStatement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_while(\n");

        if(WhileCond!=null)
            buffer.append(WhileCond.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Condition!=null)
            buffer.append(Condition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(WhileBegin!=null)
            buffer.append(WhileBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AllStatement!=null)
            buffer.append(AllStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_while]");
        return buffer.toString();
    }
}
