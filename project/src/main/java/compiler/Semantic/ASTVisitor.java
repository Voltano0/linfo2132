package compiler.Semantic;

import compiler.Parser.*; 

public interface ASTVisitor {
    void visit(AssignmentStatement node);
    void visit(BinaryExpression node);
    void visit(Block node);
    void visit(ConstantDeclaration node);
    void visit(ExpressionStatement node);
    void visit (FieldDeclaration node);
    void visit(ForStatement node);
    void visit(FreeStatement node);
    void visit(FunctionDeclaration node);
    void visit(FunctionCallExpression node);
    void visit(IfStatement node);
    void visit(LiteralExpression node);
    void visit(Parameter node);
    void visit(Program node);
    void visit(RecordConstructorExpression node);
    void visit(RecordDefinition node);
    void visit(ReturnStatement node);
    void visit(TypeNode node);
    void visit(UnaryExpression node);
    void visit(VariableDeclaration node);
    void visit(VariableExpression node);
    void visit(WhileStatement node);

    void visit(RecordDeclaration node);

    void visit(IndexExpression node);
}