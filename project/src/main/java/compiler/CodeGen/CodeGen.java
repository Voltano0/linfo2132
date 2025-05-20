package compiler.CodeGen;

import compiler.Lexer.*;
import compiler.Parser.ASTNode;
import compiler.Parser.*;
import compiler.Semantic.ASTVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class CodeGen {
    private final Program program;
    private  ClassWriter cw;
    private  MethodVisitor mv;
    private final String outputClassPath;
    public CodeGen(Program program, String outputClassPath) throws IOException {
        this.program = program;
        this.outputClassPath = outputClassPath;
    }
    private String classo;
    public void setClass(String className) {
        this.classo = className;
    }


    public void generate() throws IOException {
        // 1. create output path if does not exist
        Path outputPath = Paths.get(outputClassPath);
        Path outputDir = outputPath.getParent();
        if (outputDir ==null){
            outputDir = Paths.get(".");
        }
        Files.createDirectories(outputDir);

        String filenName = outputPath.getFileName().toString();
        String className = filenName.endsWith(".class") ? filenName.substring(0, filenName.length() - 6) : filenName;
        setClass(className);

        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", null);



        for (ASTNode stmt : program.getStatements()) {
            if (stmt instanceof ConstantDeclaration cd) {
                // ACC_PRIVATE | ACC_STATIC | ACC_FINAL
                cw.visitField(
                        Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                        cd.getName(),                   // the field name, e.g. "message"
                        desc(cd.getType().getTypeName()),  // JVM descriptor, e.g. "I" or "Ljava/lang/String;"
                        null,                           // no generic signature
                        null                            // no initial value here
                ).visitEnd();
            }
            else if(stmt instanceof VariableDeclaration vd) {
                // ACC_PRIVATE | ACC_STATIC
                cw.visitField(
                        Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                        vd.getVariableName(),                   // the field name, e.g. "message"
                        desc(vd.getType().getTypeName()),  // JVM descriptor, e.g. "I" or "Ljava/lang/String;"
                        null,                           // no generic signature
                        null                            // no initial value here
                ).visitEnd();
            }
        }
        // 3. create class file for record
        for (ASTNode smth : program.getStatements()) {
            if (smth instanceof RecordDefinition) {
                emitRecord((RecordDefinition) smth, outputDir.toString());
            }
        }
        cw.visitField(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC,
                        "__scanner",
                        "Ljava/util/Scanner;",
                        null, null)
                .visitEnd();

        // 4. Generate the default constructor
        emitDefaultConstructor();
        emitClinit(className);

        for (ASTNode smth : program.getStatements()) {
            if (smth instanceof FunctionDeclaration fn && !fn.getFunctionName().equals("main")) {
                emitFunction(fn, outputDir.toString());
            }
        }
        // 6. Generate the main method
        FunctionDeclaration mainFn = program.getMainFn();
        if (mainFn == null) {
            System.err.println("No main function found");

        }else {
        emitMain(mainFn, className);}

        // 7. Write the class to the output file
        cw.visitEnd();
        byte[] classBytes = cw.toByteArray();
        Files.write(outputPath, classBytes);

    }

    public static String desc(String typeNode) {
        switch(typeNode){
            case "int":
                return "I";
            case "boolean", "bool":
                return "Z";
            case "string":
                return "Ljava/lang/String;";
            case "void":
                return "V";
            case "float":
                return "F";
            default:
                throw new IllegalArgumentException("Unknown type: " + typeNode);
        }
    }

    private void emitRecord(RecordDefinition record, String outputDir) throws IOException {
        String recordName = record.getRecordName();
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, recordName, null, "java/lang/Object", null);

        // Generate fields
        for (ASTNode field : record.getFields()) {
            if (field instanceof FieldDeclaration) {
                FieldDeclaration fieldDecl = (FieldDeclaration) field;
                String fieldName = fieldDecl.getFieldName();
                String fieldType = fieldDecl.getType().getTypeName();
                cw.visitField(Opcodes.ACC_PUBLIC, fieldName, desc(fieldType), null, null).visitEnd();
            }else if (field instanceof VariableDeclaration vd){
                cw.visitField(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC, vd.getVariableName(), desc(vd.getType().getTypeName()), null, null).visitEnd();
            }
        }

        StringBuilder ctorsig = new StringBuilder("(");
        for(ASTNode field : record.getFields()) {
            if (field instanceof FieldDeclaration) {
                FieldDeclaration fieldDecl = (FieldDeclaration) field;
                String fieldType = fieldDecl.getType().getTypeName();
                ctorsig.append(desc(fieldType));
            }
        }
        ctorsig.append(")V");

        // Generate default constructor
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", ctorsig.toString(), null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0); //push this

        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false); // Call super constructor

        // Set fields
        int index = 1; // Start from 1 to skip 'this'

        for(ASTNode field : record.getFields()) {
            if (field instanceof FieldDeclaration) {
                FieldDeclaration fieldDecl = (FieldDeclaration) field;
                String fieldName = fieldDecl.getFieldName();
                String fieldType = fieldDecl.getType().getTypeName();

                // Load the argument onto the stack
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Objects.equals(fieldType, "int") ? org.objectweb.asm.Opcodes.ILOAD : org.objectweb.asm.Opcodes.LLOAD, index); // Load the argument
                mv.visitFieldInsn(Opcodes.PUTFIELD, recordName, fieldName, desc(fieldType));
                index += Objects.equals(fieldType, "int") ? 1 : 2;
            }
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);

        mv.visitEnd();
        cw.visitEnd();

        // Write the class to the output directory
        Path outputPath = Paths.get(outputDir, recordName + ".class");
        byte[] classBytes = cw.toByteArray();
        Files.write(outputPath, classBytes);
    }

    private void emitMain(FunctionDeclaration mainFn, String className) {
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        // no named parameters—but if you want to reference args, slot 0 holds the String[]
        HashMap<String, Integer> localSlots = new HashMap<>();
        int nextSlot = 1;                   // reserve slot 0 for args

        // if you even want to reference "args", you can:
        // localSlots.put("args", 0);

        for (Parameter p : mainFn.getParameters()) {
            // (in your case main has no params, so this loop is empty)
            localSlots.put(p.getParameterName(), nextSlot);
            nextSlot += p.getType().isWide() ? 2 : 1;
        }
        mainFn.getBody().accept(new CodeGenVisitor(mv, localSlots, className));

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    private void emitDefaultConstructor() {
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1); // or 1,1
        mv.visitEnd();
    }

    private void emitClinit(String classname) {// for constant declarations
        mv = cw.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();


        CodeGenVisitor visitor = new CodeGenVisitor(mv, new HashMap<>(), classname);

        for(ASTNode smth : program.getStatements()) {
            if (smth instanceof ConstantDeclaration cd) {
                cd.getExpression().accept(visitor);
                mv.visitFieldInsn(Opcodes.PUTSTATIC, classname, cd.getName(), desc(cd.getType().getTypeName()));
            }else if (smth instanceof VariableDeclaration vd) {
                vd.getInitializer().accept( visitor);
                mv.visitFieldInsn(Opcodes.PUTSTATIC, classname, vd.getVariableName(), desc(vd.getType().getTypeName()));
            }
        }
        // **Always** init the scanner:
        mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
        mv.visitInsn(Opcodes.DUP);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                "java/util/Scanner",
                "<init>",
                "(Ljava/io/InputStream;)V",
                false);
        mv.visitFieldInsn(Opcodes.PUTSTATIC,
                classname,
                "__scanner",
                "Ljava/util/Scanner;");

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    private void emitFunction(FunctionDeclaration function, String outputDir) throws IOException {
        StringBuilder sig = new StringBuilder("(");
        for (Parameter param : function.getParameters()) {
            sig.append(desc(param.getType().getTypeName()));
        }
        sig.append(")");
        sig.append(function.getReturnType() == null ? "V" : desc(function.getReturnType().getTypeName()));
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, function.getFunctionName(), sig.toString(), null, null);
        mv.visitCode();

        // Generate local variable slots
        HashMap<String, Integer> localSlots = new HashMap<>();
        int index = 0;
        for (Parameter param : function.getParameters()) {
            localSlots.put(param.getParameterName(), index);
            index += Objects.equals(param.getType().getTypeName(), "int") ? 1 : 2; // Assuming int is 4 bytes and other types are 8 bytes
        }

        //  ⇨ **Here**: drive your bytecode visitor over the function body
        CodeGenVisitor gen = new CodeGenVisitor(mv, localSlots, classo);
        function.getBody().accept(gen);

        //  If the last statement wasn’t a return, emit a default
        if (function.getReturnType() != null) {
            // non-void: push 0/false/null and return
            if (function.getReturnType().isIntOrBool()) mv.visitInsn(Opcodes.ICONST_0);
            else mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitInsn(function.getReturnType().isIntOrBool() ? Opcodes.IRETURN : Opcodes.ARETURN);
        } else {
            mv.visitInsn(Opcodes.RETURN);
        }

        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }





}