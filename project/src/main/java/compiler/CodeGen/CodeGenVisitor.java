package compiler.CodeGen;

import compiler.Parser.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import compiler.Semantic.ASTVisitor;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import static compiler.CodeGen.CodeGen.desc;

/**
 * Walks an AST subtree (a function body or main body) and emits JVM bytecode.
 */
public class CodeGenVisitor implements ASTVisitor {
    private final MethodVisitor mv;
    private final Map<String,Integer> locals;
    private String className;
    public CodeGenVisitor(MethodVisitor mv, Map<String,Integer> locals, String className) {
        this.mv     = mv;
        this.locals = locals;
        this.className = className;
    }

    @Override
    public void visit(Block node) {
        for (ASTNode stmt : node.getStatements()) {
            stmt.accept(this);
        }
    }

    @Override
    public void visit(ExpressionStatement node) {
        node.getExpression().accept(this);
        if(node.getType() != null && !node.getType().equals("void")) {
            mv.visitInsn(Opcodes.POP);
        }

    }

    @Override
    public void visit(ReturnStatement node) {
        node.getExpression().accept(this);
        String t = node.getReturnType().getTypeName();
        switch (t) {
            case "int", "boolean"   -> mv.visitInsn(Opcodes.IRETURN);
            case "float"       -> mv.visitInsn(Opcodes.FRETURN);
            case "long"        -> mv.visitInsn(Opcodes.LRETURN);
            case "double"      -> mv.visitInsn(Opcodes.DRETURN);
            case "string", "rec" -> mv.visitInsn(Opcodes.ARETURN);
            case "void"        -> mv.visitInsn(Opcodes.RETURN);
            default -> throw new UnsupportedOperationException("Bad return type " + t);
        }

    }

    @Override
    public void visit(LiteralExpression node) {
        switch (node.getType()) {
            case "string":
                // push the actual String value onto the stack
                mv.visitLdcInsn(node.getValue());
                break;
            case "int":
                int v = Integer.parseInt(node.getValue());
                if (v >= -1 && v <= 5)      mv.visitInsn(Opcodes.ICONST_0 + v);
                else if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) mv.visitIntInsn(Opcodes.BIPUSH, v);
                else mv.visitLdcInsn(v);
                break;
            case "float":
                // push the actual float value onto the stack
                mv.visitLdcInsn(Float.parseFloat(node.getValue()));
                break;
            case "boolean":
                // push the actual boolean value onto the stack
                if (node.toBool()) {
                    mv.visitInsn(Opcodes.ICONST_1); // true
                } else {
                    mv.visitInsn(Opcodes.ICONST_0); // false
                }
                break;
            case "null":
                // push null onto the stack
                mv.visitInsn(Opcodes.ACONST_NULL);
                break;
            default:
                throw new UnsupportedOperationException("Literal type not supported: " + node.getType());
        }
    }


    @Override
    public void visit(VariableExpression node) {
        String name = node.getVariableName();
        if (locals.containsKey(name)) {
            TypeNode t = node.getType();
            int slot = locals.get(name);
            mv.visitVarInsn(t.isIntOrBool() ? Opcodes.ILOAD : Opcodes.LLOAD, slot);
        } else {
            // static field on Main
            mv.visitFieldInsn(Opcodes.GETSTATIC,
                    this.className,
                    name,
                    desc(node.getType().getTypeName()));
        }

    }

    @Override
    public void visit(BinaryExpression node) {
        // 1) Visit the left operand
        node.getLeft().accept(this);

        // 2) Visit the right operand
        node.getRight().accept(this);

        // 3) Emit the appropriate bytecode for the operator
        String op = node.getOperator();
        switch (op) {
            case "+" -> mv.visitInsn(Opcodes.IADD);
            case "-" -> mv.visitInsn(Opcodes.ISUB);
            case "*" -> mv.visitInsn(Opcodes.IMUL);
            case "/" -> mv.visitInsn(Opcodes.IDIV);
            case "%" -> mv.visitInsn(Opcodes.IREM);
            case "&&" -> handleLogicalAnd(node);
            case "||" -> handleLogicalOr(node);
            default -> handleComparison(op);
        }

    }
    private void handleLogicalAnd(BinaryExpression node) {
        Label falseLabel = new Label();
        Label endLabel = new Label();

        node.getLeft().accept(this);
        mv.visitJumpInsn(Opcodes.IFEQ, falseLabel);


        node.getRight().accept(this);
        mv.visitJumpInsn(Opcodes.IFEQ, falseLabel);

        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitJumpInsn(Opcodes.GOTO, endLabel);
        mv.visitLabel(falseLabel);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitLabel(endLabel);
    }
    private void handleLogicalOr(BinaryExpression node) {
        Label trueLabel = new Label();
        Label endLabel  = new Label();

        node.getLeft().accept(this);
        mv.visitJumpInsn(Opcodes.IFNE, trueLabel);

        node.getRight().accept(this);
        mv.visitJumpInsn(Opcodes.IFNE, trueLabel);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitJumpInsn(Opcodes.GOTO, endLabel);
        mv.visitLabel(trueLabel);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitLabel(endLabel);
    }

    private void handleComparison(String op) {
        Label Ltrue = new Label(), Lend = new Label();
        int jump;
        switch (op) {
            case "==" ->         mv.visitJumpInsn(Opcodes.IF_ICMPEQ, Ltrue);
            case "!=" ->         mv.visitJumpInsn(Opcodes.IF_ICMPNE, Ltrue);
            case "<"  ->         mv.visitJumpInsn(Opcodes.IF_ICMPLT, Ltrue);
            case "<=" ->         mv.visitJumpInsn(Opcodes.IF_ICMPLE, Ltrue);
            case ">"  ->         mv.visitJumpInsn(Opcodes.IF_ICMPGT, Ltrue);
            case ">=" ->         mv.visitJumpInsn(Opcodes.IF_ICMPGE, Ltrue);
            default   -> throw new IllegalArgumentException("Bad comp op " + op);
        }

        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitJumpInsn(Opcodes.GOTO, Lend);
        mv.visitLabel(Ltrue);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitLabel(Lend);
    }

    @Override
    public void visit(FunctionCallExpression node) {
        String name = node.getFunctionName();
        switch (name) {
            // --- READS ---
            case "readInt" -> {
                mv.visitFieldInsn(Opcodes.GETSTATIC,
                        this.className,
                        "__scanner",
                        "Ljava/util/Scanner;");
                // call nextInt(): ()I
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        "java/util/Scanner",
                        "nextInt",
                        "()I",
                        false);
            }
            case "readFloat" -> {
                mv.visitFieldInsn(Opcodes.GETSTATIC, this.className, "__scanner", "Ljava/util/Scanner;");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        "java/util/Scanner",
                        "nextFloat",
                        "()F",
                        false);
            }
            case "readString" -> {
                mv.visitFieldInsn(Opcodes.GETSTATIC, this.className, "__scanner", "Ljava/util/Scanner;");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        "java/util/Scanner",
                        "nextLine",
                        "()Ljava/lang/String;",
                        false);
            }

            // --- WRITES ---
            case "writeInt", "writeFloat", "write", "writeln" -> {
                // get System.out
                mv.visitFieldInsn(Opcodes.GETSTATIC,
                        "java/lang/System",
                        "out",
                        "Ljava/io/PrintStream;");

                // 2) Push every argument in order
                for (ASTNode arg : node.getArguments()) {
                    arg.accept(this);
                }

                // 3) Choose print vs println

                String method = name.equals("writeln") ? "println" : "print";

                String sig;
                switch (name) {
                    case "writeInt" -> sig = "(I)V";
                    case "writeFloat" -> sig = "(F)V";
                    default -> sig = "(Ljava/lang/Object;)V";
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        "java/io/PrintStream",
                        method,
                        sig,
                        false);

                // if you need a return value (say boolean), you could push ICONST_1 here
                // mv.visitInsn(ICONST_1);
            }


            // --- USER FUNCTIONS ---
            default -> {
                for (ASTNode arg : node.getArguments()) {
                    arg.accept(this);
                }
                // build descriptor "(... )R"
                StringBuilder sig = new StringBuilder("(");
                for (String arg : node.getParameterTypes()) {
                    sig.append(desc(arg));
                }
                sig.append(")");
                String retDesc = node.getReturnType() == null ? "V" : desc(node.getReturnType().getTypeName());
                sig.append(retDesc);

                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        this.className,
                        name,
                        sig.toString(),
                        false);
            }
        }
    }

    @Override public void visit(RecordDeclaration node) {
        // 1) Allocate a slot for `p`
        String varName = node.getVariableName();  // e.g. "p"
        int slot = locals.size();
        locals.put(varName, slot);

        // 2) Emit `new Point(...)`
        String recName = node.getRecordName();    // e.g. "Point"
        mv.visitTypeInsn(Opcodes.NEW, recName);
        mv.visitInsn(Opcodes.DUP);

        // 3) Push each constructor argument
        //    (these are your ASTNode expressions)
        for (ASTNode arg : node.getArguments()) {
            (arg).accept(this);
        }

        // 4) Build the ctor descriptor "(II)V" etc.
        StringBuilder ctorSig = new StringBuilder("(");
        for (TypeNode arg : node.getTypeArguments()) {
            ctorSig.append(desc(arg.getTypeName()));
        }
        ctorSig.append(")V");

        // 5) Invoke <init>
        mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                recName,
                "<init>",
                ctorSig.toString(),
                false
        );

        // 6) Store the new record into the local `p`
        mv.visitVarInsn(Opcodes.ASTORE, slot);
    }



    @Override public void visit(IfStatement n){
        Label Lelse = new Label(), Lend = new Label();
        n.getCondition().accept(this);
        mv.visitJumpInsn(Opcodes.IFEQ, Lelse);
        n.getThenBlock().accept(this);
        mv.visitJumpInsn(Opcodes.GOTO, Lend);
        mv.visitLabel(Lelse);
        if(n.getElseBlock() != null) {
            n.getElseBlock().accept(this);
        }
        mv.visitLabel(Lend);
    }
    @Override public void visit(WhileStatement n){
        Label Lbegin = new Label(), Lend = new Label();
        mv.visitLabel(Lbegin);
        n.getCondition().accept(this);
        mv.visitJumpInsn(Opcodes.IFEQ, Lend);
        n.getBlock().accept(this);
        mv.visitJumpInsn(Opcodes.GOTO, Lbegin);
        mv.visitLabel(Lend);
    }
    @Override public void visit(ForStatement n){
        n.getStartExpr().accept(this);
        int slot = locals.size();
        locals.put(n.getIterator(), slot);
        mv.visitVarInsn(Opcodes.ISTORE, slot);

        Label Lbegin = new Label(), Lend = new Label();
        mv.visitLabel(Lbegin);

        mv.visitVarInsn(Opcodes.ILOAD, slot);
        n.getEndExpr().accept(this);
        mv.visitJumpInsn(Opcodes.IF_ICMPGT, Lend);
        n.getBlock().accept(this);

        mv.visitVarInsn(Opcodes.ILOAD, slot);
        n.getStepExpr().accept(this);
        mv.visitInsn(Opcodes.IADD);
        mv.visitVarInsn(Opcodes.ISTORE, slot);

        mv.visitJumpInsn(Opcodes.GOTO, Lbegin);
        mv.visitLabel(Lend);
    }
    @Override public void visit(VariableDeclaration n){
        int slot = locals.size();
        locals.put(n.getVariableName(), slot);

        if(n.getInitializer() != null) {
            n.getInitializer().accept(this);
            TypeNode t = n.getType();
            int storeOp;
            switch (t.getTypeName()) {
                case "int", "boolean" -> storeOp = Opcodes.ISTORE;
                case "long"           -> storeOp = Opcodes.LSTORE;
                case "float"          -> storeOp = Opcodes.FSTORE;
                case "double"         -> storeOp = Opcodes.DSTORE;
                default               -> storeOp = Opcodes.ASTORE;
            }

            mv.visitVarInsn(storeOp, slot);
        }
    }
    @Override public void visit(AssignmentStatement node){
        // 1) Compute the RHS and leave its value on the stack
        node.getRight().accept(this);

        // 2) Figure out where to store it
        //    We assume the LHS is always a simple VariableExpression
        String name = ((VariableExpression)node.getLeft()).getVariableName();
        TypeNode t = ((VariableExpression)node.getLeft()).getType();

        if (locals.containsKey(name)) {
            // a local variable
            int slot = locals.get(name);
            switch (t.getTypeName()) {
                case "int", "boolean" -> mv.visitVarInsn(Opcodes.ISTORE, slot);
                case "long"           -> mv.visitVarInsn(Opcodes.LSTORE, slot);
                case "float"          -> mv.visitVarInsn(Opcodes.FSTORE, slot);
                case "double"         -> mv.visitVarInsn(Opcodes.DSTORE, slot);
                default                -> mv.visitVarInsn(Opcodes.ASTORE, slot);
            }
        } else {
            // a top‐level/static field on Main
            mv.visitFieldInsn(
                    Opcodes.PUTSTATIC,
                    this.className,
                    name,
                    desc(t.getTypeName())
            );
        }
    }


    @Override public void visit(IndexExpression node) {}

    @Override
    public void visit(FieldAccess node) {
        String varName = node.getVarname();
        String fieldName = node.getRecArg();
        String recordType = node.getRectype();
        String fieldDesc = desc(node.getRecInType().getTypeName());
        if (locals.containsKey(varName)) {
            mv.visitVarInsn(Opcodes.ALOAD, locals.get(varName));
        } else {
            // top‐level static record
            mv.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    className,
                    varName,
                    "L" + recordType + ";"      // descriptor for the record class
            );
        }

        // 2) GETFIELD recordType.fieldName:fieldDesc
        mv.visitFieldInsn(
                Opcodes.GETFIELD,
                recordType,
                fieldName,
                fieldDesc
        );
    }

    @Override public void visit(FreeStatement node) {}
    @Override public void visit(FunctionDeclaration node) {}
    @Override public void visit(RecordDefinition node) {}
    @Override public void visit(FieldDeclaration n){}
    @Override public void visit(ConstantDeclaration n){}
    @Override public void visit(Parameter n){}
    @Override public void visit(TypeNode n){}
    @Override public void visit(UnaryExpression node) {}
    @Override public void visit(Program node) {}
}
