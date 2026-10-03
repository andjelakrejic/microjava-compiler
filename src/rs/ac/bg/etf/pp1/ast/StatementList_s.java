// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class StatementList_s extends StatementList {

    private StatementList StatementList;
    private AllStatement AllStatement;

    public StatementList_s (StatementList StatementList, AllStatement AllStatement) {
        this.StatementList=StatementList;
        if(StatementList!=null) StatementList.setParent(this);
        this.AllStatement=AllStatement;
        if(AllStatement!=null) AllStatement.setParent(this);
    }

    public StatementList getStatementList() {
        return StatementList;
    }

    public void setStatementList(StatementList StatementList) {
        this.StatementList=StatementList;
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
        if(StatementList!=null) StatementList.accept(visitor);
        if(AllStatement!=null) AllStatement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(StatementList!=null) StatementList.traverseTopDown(visitor);
        if(AllStatement!=null) AllStatement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(StatementList!=null) StatementList.traverseBottomUp(visitor);
        if(AllStatement!=null) AllStatement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("StatementList_s(\n");

        if(StatementList!=null)
            buffer.append(StatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AllStatement!=null)
            buffer.append(AllStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [StatementList_s]");
        return buffer.toString();
    }
}
