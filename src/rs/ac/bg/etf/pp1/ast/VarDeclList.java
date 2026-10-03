// generated with ast extension for cup
// version 0.8
// 3/8/2026 20:10:39


package rs.ac.bg.etf.pp1.ast;

public class VarDeclList implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private VarDeclPrefix VarDeclPrefix;
    private Type Type;
    private VarDecl VarDecl;
    private VarDeclMore VarDeclMore;

    public VarDeclList (VarDeclPrefix VarDeclPrefix, Type Type, VarDecl VarDecl, VarDeclMore VarDeclMore) {
        this.VarDeclPrefix=VarDeclPrefix;
        if(VarDeclPrefix!=null) VarDeclPrefix.setParent(this);
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.VarDecl=VarDecl;
        if(VarDecl!=null) VarDecl.setParent(this);
        this.VarDeclMore=VarDeclMore;
        if(VarDeclMore!=null) VarDeclMore.setParent(this);
    }

    public VarDeclPrefix getVarDeclPrefix() {
        return VarDeclPrefix;
    }

    public void setVarDeclPrefix(VarDeclPrefix VarDeclPrefix) {
        this.VarDeclPrefix=VarDeclPrefix;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public VarDecl getVarDecl() {
        return VarDecl;
    }

    public void setVarDecl(VarDecl VarDecl) {
        this.VarDecl=VarDecl;
    }

    public VarDeclMore getVarDeclMore() {
        return VarDeclMore;
    }

    public void setVarDeclMore(VarDeclMore VarDeclMore) {
        this.VarDeclMore=VarDeclMore;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarDeclPrefix!=null) VarDeclPrefix.accept(visitor);
        if(Type!=null) Type.accept(visitor);
        if(VarDecl!=null) VarDecl.accept(visitor);
        if(VarDeclMore!=null) VarDeclMore.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclPrefix!=null) VarDeclPrefix.traverseTopDown(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(VarDecl!=null) VarDecl.traverseTopDown(visitor);
        if(VarDeclMore!=null) VarDeclMore.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclPrefix!=null) VarDeclPrefix.traverseBottomUp(visitor);
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(VarDecl!=null) VarDecl.traverseBottomUp(visitor);
        if(VarDeclMore!=null) VarDeclMore.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclList(\n");

        if(VarDeclPrefix!=null)
            buffer.append(VarDeclPrefix.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDecl!=null)
            buffer.append(VarDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclMore!=null)
            buffer.append(VarDeclMore.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclList]");
        return buffer.toString();
    }
}
