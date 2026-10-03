// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class Statement_do extends Statement {

    private DoNonterm DoNonterm;
    private AllStatement AllStatement;
    private WhileNonterm WhileNonterm;
    private Condition Condition;

    public Statement_do (DoNonterm DoNonterm, AllStatement AllStatement, WhileNonterm WhileNonterm, Condition Condition) {
        this.DoNonterm=DoNonterm;
        if(DoNonterm!=null) DoNonterm.setParent(this);
        this.AllStatement=AllStatement;
        if(AllStatement!=null) AllStatement.setParent(this);
        this.WhileNonterm=WhileNonterm;
        if(WhileNonterm!=null) WhileNonterm.setParent(this);
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
    }

    public DoNonterm getDoNonterm() {
        return DoNonterm;
    }

    public void setDoNonterm(DoNonterm DoNonterm) {
        this.DoNonterm=DoNonterm;
    }

    public AllStatement getAllStatement() {
        return AllStatement;
    }

    public void setAllStatement(AllStatement AllStatement) {
        this.AllStatement=AllStatement;
    }

    public WhileNonterm getWhileNonterm() {
        return WhileNonterm;
    }

    public void setWhileNonterm(WhileNonterm WhileNonterm) {
        this.WhileNonterm=WhileNonterm;
    }

    public Condition getCondition() {
        return Condition;
    }

    public void setCondition(Condition Condition) {
        this.Condition=Condition;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DoNonterm!=null) DoNonterm.accept(visitor);
        if(AllStatement!=null) AllStatement.accept(visitor);
        if(WhileNonterm!=null) WhileNonterm.accept(visitor);
        if(Condition!=null) Condition.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DoNonterm!=null) DoNonterm.traverseTopDown(visitor);
        if(AllStatement!=null) AllStatement.traverseTopDown(visitor);
        if(WhileNonterm!=null) WhileNonterm.traverseTopDown(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DoNonterm!=null) DoNonterm.traverseBottomUp(visitor);
        if(AllStatement!=null) AllStatement.traverseBottomUp(visitor);
        if(WhileNonterm!=null) WhileNonterm.traverseBottomUp(visitor);
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_do(\n");

        if(DoNonterm!=null)
            buffer.append(DoNonterm.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AllStatement!=null)
            buffer.append(AllStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(WhileNonterm!=null)
            buffer.append(WhileNonterm.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Condition!=null)
            buffer.append(Condition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_do]");
        return buffer.toString();
    }
}
