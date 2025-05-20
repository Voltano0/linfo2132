package compiler.Parser;

import compiler.Semantic.ASTVisitor;

public class FieldAccess extends ASTNode{
    private String Varname;
    private String RecArg;
    private TypeNode recinType;
    private String Rectype;

    public FieldAccess(String fieldName, String type) {
        this.Varname = fieldName;
        this.RecArg = type;
    }

    @Override
    public String prettyPrint(String indent) {
        return "" + indent + "FieldAccess: " + Varname + " : " + RecArg;
    }
    public String getVarname() {
        return Varname;
    }
    public String getRecArg() {
        return RecArg;
    }
    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }
    public TypeNode getRecInType() {
        return recinType;
    }
    public void setRecInType(TypeNode recType) {
        this.recinType = recType;
    }
    public String getRectype() {
        return Rectype;
    }
    public void setRectype(String rectype) {
        Rectype = rectype;
    }
}
