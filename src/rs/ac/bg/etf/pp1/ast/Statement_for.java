// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class Statement_for extends Statement {

    private Statement_f_des Statement_f_des;
    private ForCond ForCond;
    private Statement_f_con Statement_f_con;
    private ForStep ForStep;
    private Statement_f_des Statement_f_des1;
    private ForBegin ForBegin;
    private AllStatement AllStatement;

    public Statement_for (Statement_f_des Statement_f_des, ForCond ForCond, Statement_f_con Statement_f_con, ForStep ForStep, Statement_f_des Statement_f_des1, ForBegin ForBegin, AllStatement AllStatement) {
        this.Statement_f_des=Statement_f_des;
        if(Statement_f_des!=null) Statement_f_des.setParent(this);
        this.ForCond=ForCond;
        if(ForCond!=null) ForCond.setParent(this);
        this.Statement_f_con=Statement_f_con;
        if(Statement_f_con!=null) Statement_f_con.setParent(this);
        this.ForStep=ForStep;
        if(ForStep!=null) ForStep.setParent(this);
        this.Statement_f_des1=Statement_f_des1;
        if(Statement_f_des1!=null) Statement_f_des1.setParent(this);
        this.ForBegin=ForBegin;
        if(ForBegin!=null) ForBegin.setParent(this);
        this.AllStatement=AllStatement;
        if(AllStatement!=null) AllStatement.setParent(this);
    }

    public Statement_f_des getStatement_f_des() {
        return Statement_f_des;
    }

    public void setStatement_f_des(Statement_f_des Statement_f_des) {
        this.Statement_f_des=Statement_f_des;
    }

    public ForCond getForCond() {
        return ForCond;
    }

    public void setForCond(ForCond ForCond) {
        this.ForCond=ForCond;
    }

    public Statement_f_con getStatement_f_con() {
        return Statement_f_con;
    }

    public void setStatement_f_con(Statement_f_con Statement_f_con) {
        this.Statement_f_con=Statement_f_con;
    }

    public ForStep getForStep() {
        return ForStep;
    }

    public void setForStep(ForStep ForStep) {
        this.ForStep=ForStep;
    }

    public Statement_f_des getStatement_f_des1() {
        return Statement_f_des1;
    }

    public void setStatement_f_des1(Statement_f_des Statement_f_des1) {
        this.Statement_f_des1=Statement_f_des1;
    }

    public ForBegin getForBegin() {
        return ForBegin;
    }

    public void setForBegin(ForBegin ForBegin) {
        this.ForBegin=ForBegin;
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
        if(Statement_f_des!=null) Statement_f_des.accept(visitor);
        if(ForCond!=null) ForCond.accept(visitor);
        if(Statement_f_con!=null) Statement_f_con.accept(visitor);
        if(ForStep!=null) ForStep.accept(visitor);
        if(Statement_f_des1!=null) Statement_f_des1.accept(visitor);
        if(ForBegin!=null) ForBegin.accept(visitor);
        if(AllStatement!=null) AllStatement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Statement_f_des!=null) Statement_f_des.traverseTopDown(visitor);
        if(ForCond!=null) ForCond.traverseTopDown(visitor);
        if(Statement_f_con!=null) Statement_f_con.traverseTopDown(visitor);
        if(ForStep!=null) ForStep.traverseTopDown(visitor);
        if(Statement_f_des1!=null) Statement_f_des1.traverseTopDown(visitor);
        if(ForBegin!=null) ForBegin.traverseTopDown(visitor);
        if(AllStatement!=null) AllStatement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Statement_f_des!=null) Statement_f_des.traverseBottomUp(visitor);
        if(ForCond!=null) ForCond.traverseBottomUp(visitor);
        if(Statement_f_con!=null) Statement_f_con.traverseBottomUp(visitor);
        if(ForStep!=null) ForStep.traverseBottomUp(visitor);
        if(Statement_f_des1!=null) Statement_f_des1.traverseBottomUp(visitor);
        if(ForBegin!=null) ForBegin.traverseBottomUp(visitor);
        if(AllStatement!=null) AllStatement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_for(\n");

        if(Statement_f_des!=null)
            buffer.append(Statement_f_des.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForCond!=null)
            buffer.append(ForCond.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement_f_con!=null)
            buffer.append(Statement_f_con.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForStep!=null)
            buffer.append(ForStep.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement_f_des1!=null)
            buffer.append(Statement_f_des1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForBegin!=null)
            buffer.append(ForBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AllStatement!=null)
            buffer.append(AllStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_for]");
        return buffer.toString();
    }
}
