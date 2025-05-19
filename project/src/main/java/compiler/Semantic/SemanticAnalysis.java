package compiler.Semantic;

import java.sql.SQLOutput;
import java.util.*;
import compiler.Parser.*;

public class SemanticAnalysis implements ASTVisitor {
    private String currentType;
    private String currentFunctionType;
    private final Map<String, String> symbolTable = new HashMap<>();
    private final Map<String, List<String>> functionParameterTypes = new HashMap<>();
    private Map<String, List<String>> recordFieldTypes = new HashMap<>();
    private Set<String> validTypes = new HashSet<>(Arrays.asList("int", "boolean", "float", "string", "bool"));
    private List<String> errors = new ArrayList<>();
    private final Map<String, String> constants = new HashMap<>();


    public SemanticAnalysis() {

        // add function inti the symbol table
        symbolTable.put("readInt", "int");
        symbolTable.put("readFloat", "float");
        symbolTable.put("readString", "string");
        symbolTable.put("writeInt", "void");
        symbolTable.put("writeFloat", "void");
        symbolTable.put("write", "void");
        symbolTable.put("writeln", "void");
        symbolTable.put("chr", "string");
        symbolTable.put("len", "int");
        symbolTable.put("floor", "int");
    
        // add function parameter types with entries
        functionParameterTypes.put("writeInt", List.of("int"));
        functionParameterTypes.put("writeFloat", List.of("float"));
        functionParameterTypes.put("write", List.of("string"));
        functionParameterTypes.put("chr", List.of("int"));
        functionParameterTypes.put("len", List.of("string"));
        functionParameterTypes.put("floor", List.of("float"));
        // add function return types
        functionParameterTypes.put("readInt", List.of());
        functionParameterTypes.put("readFloat", List.of());
        functionParameterTypes.put("readString", List.of());
        functionParameterTypes.put("writeln", List.of());
    }



    @Override
    public void visit(AssignmentStatement node) {

        // visit the left side 
        node.getLeft().accept(this);
        String leftType = currentType;
        String leftName = node.getLeft().toString();

        // Check if the left side is a variable
        if (leftType == null) {
            errors.add("ScopeError : Variable '" + leftName + "' is not defined");
            return;
        }

        // visit the right side
        node.getRight().accept(this);
        String rightType = currentType;

        // Check if the right side is a valid type
        if (rightType == null) {
            errors.add("TypeError : Right-hand side of assignment is invalid or undefined");
            return;
        }

        // check for type compatibility
        if (leftType.endsWith("[]") && rightType.startsWith("array<")) {
            String elementType = leftType.substring(0, leftType.length() - 2); 
            String initializerElementType = rightType.substring(6, rightType.length() - 1); 
    
            if (!elementType.equals(initializerElementType)) {
                errors.add("TypeError : Mismatched types in array initialization: variable '" + leftName + "' is of type " + leftType + ", but assigned array contains elements of type " + initializerElementType);
            }
            return;
        }


        if (!leftType.equals(rightType)) {
            errors.add("TypeError :Mismatched types in assignment: variable '" + leftName + "' is of type " + leftType + ", but assigned value is of type " + rightType);
        }
    }

    @Override
    public void visit(BinaryExpression node) {

        // visit the left node
        node.getLeft().accept(this);
        String leftType = currentType;

        // visit the right node
        node.getRight().accept(this);
        String rightType = currentType;

        String operator = node.getOperator();


        if (operator.equals("+")) {
            // handle string concatenation
            if ("string".equals(leftType) && "string".equals(rightType)) {
                currentType = "string"; 
            } 
            // handle addition
            else if (("int".equals(leftType) && "float".equals(rightType)) || ("float".equals(leftType) && "int".equals(rightType))) {
                currentType = "float"; 
            } else if ("int".equals(leftType) && "int".equals(rightType)) {
                currentType = "int";
            } else if ("float".equals(leftType) && "float".equals(rightType)) {
                currentType = "float";
            } else {
                errors.add("OperatorError : Invalid types for '+' operator: " + leftType + " and " + rightType);
                currentType = null;
            }
        } else if (operator.equals("-") || operator.equals("*") || operator.equals("/")) {
            // handle operations
            if (("int".equals(leftType) && "float".equals(rightType)) || ("float".equals(leftType) && "int".equals(rightType))) {
                currentType = "float"; 
            } else if ("int".equals(leftType) && "int".equals(rightType)) {
                currentType = "int";
            } else if ("float".equals(leftType) && "float".equals(rightType)) {
                currentType = "float";
            } else {
                errors.add("OperatorError : Invalid types for binary operator '" + operator + "': " + leftType + " and " + rightType);

                currentType = null;
            }
        } else if (operator.equals("&&") || operator.equals("||")) {
            // handle logical operators
            if (!"boolean".equals(leftType) || !"boolean".equals(rightType)) {
                errors.add("OperatorError : Invalid types for binary operator '" + operator + "': " + leftType + " and " + rightType);
            }
            currentType = "boolean";
        } else if (operator.equals("==") || operator.equals("!=") || operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals(">=")) {
            // handle comparison 
            if (!leftType.equals(rightType)) {
                errors.add("OperatorError : Comparison operator '" + operator + "' requires operands of the same type, found: " + leftType + " and " + rightType);
            }
            currentType = "boolean";
        } else {
            // unsupported operator
            errors.add("OperatorError : Unsupported binary operator: " + operator);
            currentType = null; 
        }
    }


    @Override
    public void visit(Block node) {
        // check if empty
        List<ASTNode> statements = node.getStatements();
        if (statements != null) {
            // for all statements accept if not null
            for (ASTNode statement : statements) {
                if (statement != null) {
                    statement.accept(this);
                } else {
                    errors.add( "Block contains a null statement");
                }
            }
        } else {
            errors.add( "Block contains no statements");
        }
    }

    // File: SemanticAnalysis.java
// Language: java
    @Override
    public void visit(ConstantDeclaration node) {
        String name = node.getName();
        TypeNode type = node.getType();
        ASTNode initializer = node.getExpression();

        // check if the constant is already declared
        if (constants.containsKey(name)) {
            errors.add("ScopeError: Constant " + name + " is already declared");
            return;
        }

        // ensure the constant has an initializer
        if (initializer == null) {
            errors.add("TypeError: Constant " + name + " must be initialized at the time of declaration");
            return;
        }

        // validate the initializer
        initializer.accept(this);
        String initializerType = currentType;
        if (Objects.equals(initializerType, "boolean")) {
            initializerType = "bool";
        }

        // If the initializer is a function call, look up the function's return type
        if (initializer instanceof FunctionCallExpression) {
            String functionName = ((FunctionCallExpression) initializer).getFunctionName();
            initializerType = symbolTable.get(functionName);
            if (initializerType == null) {
                errors.add("ScopeError: Function " + functionName + " is not defined");
                return;
            }
        }

        // Compare initializer type with the declared type
        if (!type.getTypeName().equals(initializerType)) {
            errors.add("TypeError: Type mismatch in constant " + name + ": expected " +
                    type.getTypeName() + ", but found " + initializerType);
            return;
        }

        // Visit the type to set the currentType and save the final constant in global scope
        type.accept(this);
        constants.put(name, currentType);
        symbolTable.put(name, currentType);
    }


    @Override
    public void visit(ExpressionStatement node) {
        // check if the expression is null
        if (node.getExpression() == null) {
            errors.add( "TypeError : Expression statement contains a null expression");
            return;
        }

        node.getExpression().accept(this);

        // check if the expression has a valid type
        if (currentType == null) {
            errors.add( "TypeError : Expression statement has an invalid or undefined type");
        }
    }

    @Override
    public void visit(FieldDeclaration node) {
        String fieldName = node.getFieldName();
        ASTNode fieldType = node.getType();

        // check if the field is already declared
        if (fieldName == null || fieldName.isEmpty()) {
            errors.add( "Field has an invalid or missing name");
            return;
        }

        // check if the field type is null
        if (fieldType == null) {
            errors.add( "TypeError : Field '" + fieldName + "' has an invalid or missing type");
            return;
        }

        // check if the field is already defined
        if (symbolTable.containsKey(fieldName)) {
            errors.add( "ScopeError : Field '" + fieldName + "' is already defined");
            return;
        }

        // visit the field type to determine its type
        fieldType.accept(this);
        symbolTable.put(fieldName, currentType);
    }

    @Override
    public void visit(ForStatement node) {
        String iterator = node.getIterator();

        // check if the iterator is null
        if (iterator == null || iterator.isEmpty()) {
            errors.add( "For statement has an invalid or missing iterator");
            return;
        }


        // check if the start, end, and step expressions are null
        if (node.getStartExpr() != null) {
            node.getStartExpr().accept(this);
            if (!"int".equals(currentType)) {
                errors.add( "Start expression in 'for' statement must be of type int");
            }
        } else {
            errors.add( "For statement is missing a start expression");
        }

        if (node.getEndExpr() != null) {
            node.getEndExpr().accept(this);
            if (!"int".equals(currentType)) {
                errors.add( "End expression in 'for' statement must be of type int");
            }
        } else {
            errors.add( "For statement is missing an end expression");
        }

        if (node.getStepExpr() != null) {
            node.getStepExpr().accept(this);
            if (!"int".equals(currentType)) {
                errors.add( "Step expression in 'for' statement must be of type int");
            }
        }

        // check if the block is null
        if (node.getBlock() != null) {
            node.getBlock().accept(this);
        } else {
            errors.add( "For statement is missing a block");
        }

    }

    @Override
    public void visit(FreeStatement node) {
        node.getExpression().accept(this);
        String operandType = currentType;

        // check if the operand type is null
        if (operandType == null) {
            errors.add("TypeError : Operand of 'free' statement is invalid or undefined");
            return;
        }

        // check if the operand type is an array or record
        if (!operandType.endsWith("[]") && !isRecordType(operandType)) {
            errors.add("TypeError : Operand of 'free' statement must be an array or record type, found: " + operandType);
            return;
        }
    }

    @Override
    public void visit(FunctionDeclaration node) {
        String functionName = node.getFunctionName();

        // check if the function name is null
        if (functionName == null || functionName.isEmpty()) {
            errors.add( "TypeError : Function has an invalid or missing name");
            return;
        }

        // check if the function is already defined
        if (symbolTable.containsKey(functionName)) {
            errors.add( "ScopeError : Function '" + functionName + "' is already defined");
            return;
        }

        symbolTable.put(functionName, "function");

        // check if the function has parameters
        List<String> parameterTypes = new ArrayList<>();
        for (ASTNode parameter : node.getParameters()) {
            parameter.accept(this);
            parameterTypes.add(currentType);
        }
        functionParameterTypes.put(functionName, parameterTypes);

        // check if the function has a return type
        if (node.getReturnType() != null) {
            node.getReturnType().accept(this);
            if (currentType == null) {
                errors.add( "ReturnError : Function '" + functionName + "' has an invalid return type");
                return;
            }
            currentFunctionType = currentType;
        } else {
            currentFunctionType = "void";
        }
        symbolTable.put(functionName, currentFunctionType);
        // check if the function has a body
        if (node.getBody() != null) {
            node.getBody().accept(this);
        } else {
            errors.add( "Function '" + functionName + "' is missing a body");
        }

        currentFunctionType = null;
    }

    @Override
    public void visit(FunctionCallExpression node) {
        String functionName = node.getFunctionName();
        // check if the function name is null
        if (!symbolTable.containsKey(functionName)) {
            errors.add("ScopeError : Function '" + functionName + "' is not defined");
            currentType = null;
            return;
        }
        if ("writeln".equals(functionName) || "write".equals(functionName)) {
            List<ASTNode> arguments = node.getArguments();
            for (ASTNode argument : arguments) {
                if (argument == null) {
                    errors.add("ArgumentError : 'writeln' cannot have null arguments");
                    return;
                }
                argument.accept(this); // Validate the argument type
            }
            currentType = "void";
            return;
        }

        List<ASTNode> arguments = node.getArguments();
        List<String> parameterTypes = functionParameterTypes.get(functionName);
        // check if the function has parameters
        if (arguments.size() != parameterTypes.size()) {
            errors.add( "ArgumentError : Incorrect number of arguments for function '" + functionName + "'");
            currentType = null;
            return;
        }

        // check if the arguments are valid
        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).accept(this);
            if (!currentType.equals(parameterTypes.get(i))) {
                errors.add( "ArgumentError : Argument type mismatch for function '" + functionName + "' at position " + i);
                return;
            }
        }


        currentType = symbolTable.get(functionName);
    }

    @Override
    public void visit(IfStatement node) {
        // check if the condition is null
        if (node.getCondition() == null) {
            errors.add( "MissingConditionError : If statement is missing a condition");
            return;
        }

        // visit the condition to determine its type
        node.getCondition().accept(this);
        if (!"boolean".equals(currentType)) {
            errors.add( "MissingConditionError : Condition in 'if' statement must be of type boolean");
            return;
        }

        // check if the then block is null
        if (node.getThenBlock() != null) {
            node.getThenBlock().accept(this);
        }

        // check if the else block is null
        if (node.getElseBlock() != null) {
            node.getElseBlock().accept(this);
        }
    }

    @Override
    public void visit(LiteralExpression node) {
        String value = node.getValue();
        if(Objects.equals(value, "")) {
            currentType = "string";
        }
        // check if the value is null or empty
        if (value == null) {
            errors.add( "TypeError : LiteralExpression has an invalid or missing value");
            currentType = null;
            return;
        }

        // check if the value is a string
        if (value.matches("-?\\d+")) {
            currentType = "int";
        } else if (value.matches("-?\\d*\\.\\d+")) {
            currentType = "float";
        } else if (value.equals("true") || value.equals("false")) {
            currentType = "boolean";
        } else  {
            currentType = "string";
        }
    }

    @Override
    public void visit(Parameter node) {
        String paramName = node.getParameterName();
        ASTNode typeNode = node.getType();

        // check if the parameter name is null
        if (paramName == null || paramName.isEmpty()) {
            errors.add( "Parameter has an invalid or missing name");
            return;
        }

        // check if the parameter type is null
        if (typeNode == null) {
            errors.add( "TypeError : Parameter '" + paramName + "' has an invalid or missing type");
            return;
        }

        typeNode.accept(this);
        String paramType = currentType;

        // check if the parameter type is valid
        if (symbolTable.containsKey(paramName)) {
            errors.add( "ScopeError : Parameter '" + paramName + "' is already defined in the current scope");
            return;
        }

        symbolTable.put(paramName, paramType);
    }

    @Override
    public void visit(Program node) {
        // check if the program has a name
        List<ASTNode> statements = node.getStatements();
        if (statements == null || statements.isEmpty()) {
            errors.add( "Program contains no statements");
            return;
        }

        // visit all statements in the program
        for (ASTNode statement : statements) {
            if (statement != null) {
                statement.accept(this);
            } else {
                errors.add( "Program contains a null statement");
            }
        }
    }

    @Override
    public void visit(RecordConstructorExpression node) {
        String recordName = node.getRecordName();
        List<ASTNode> arguments = node.getArguments();

        // check if the record name is null
        if (recordName == null || recordName.isEmpty()) {
            errors.add( "RecordError : RecordConstructorExpression has an invalid or missing record name");
            return;
        }

        // check if the record name is already defined
        if (!symbolTable.containsKey(recordName)) {
            errors.add( "RecordError : Record '" + recordName + "' is not defined");
            return;
        }

        // check if the record name is a valid type
        List<String> expectedTypes = recordFieldTypes.get(recordName);
        if (expectedTypes == null) {
            errors.add( "RecordError : Record '" + recordName + "' has no defined fields");
            return;
        }

        // check if the number of arguments matches the expected types
        if (arguments.size() != expectedTypes.size()) {
            errors.add( "RecordError : Incorrect number of arguments for record '" + recordName + "'");
            return;
        }

        // check if the argument types match the expected types
        for (int i = 0; i < arguments.size(); i++) {
            ASTNode argument = arguments.get(i);
            argument.accept(this);
            if (!currentType.equals(expectedTypes.get(i))) {
                errors.add( "RecordError : Argument type mismatch for record '" + recordName + "' at position " + i);
            }
        }

        currentType = recordName;
    }

    @Override
    public void visit(RecordDefinition node) {
        String recordName = node.getRecordName();
        List<ASTNode> fields = node.getFields();

        // check if the record name is null
        if (recordName == null || recordName.isEmpty()) {
            errors.add( "RecordError : Record has an invalid or missing name");
            return;
        }

        // check if the record name is already defined
        if (symbolTable.containsKey(recordName)) {
            errors.add( "RecordError : Record '" + recordName + "' is already defined");
            return;
        }

        symbolTable.put(recordName, "record");

        // check if the record has fields
        Set<String> fieldNames = new HashSet<>();
        List<String> fieldTypes = new ArrayList<>();
        for (ASTNode field : fields) {
            // check if the field is null
            if (field instanceof FieldDeclaration) {
                FieldDeclaration fieldNode = (FieldDeclaration) field;
                String fieldName = fieldNode.getFieldName();
                // if the field name is null add error, else 
                if (fieldNames.contains(fieldName)) {
                    errors.add( "RecordError :Duplicate field name '" + fieldName + "' in record '" + recordName + "'");
                } else {
                    fieldNames.add(fieldName);
                    fieldNode.getType().accept(this);
                    fieldTypes.add(currentType);
                }
            } else {
                errors.add( "RecordError : Invalid field in record '" + recordName + "'");
            }
        }
        recordFieldTypes.put(recordName, fieldTypes);
    }

    @Override
    public void visit(ReturnStatement node) {
        // check if the return statement is inside a function
        if (currentFunctionType == null) {
            errors.add( "ReturnError : Return statement outside of a function");
            return;
        }

        // check if the return statement has an expression
        ASTNode expression = node.getExpression();
        if (expression != null) {
            expression.accept(this);
            if (!currentType.equals(currentFunctionType)) {
                errors.add( "ReturnError : Return type mismatch: expected '" + currentFunctionType + "', found '" + currentType + "'");
            }
        } else if (!"void".equals(currentFunctionType)) {
            errors.add( "ReturnError : Missing return value for non-void function");
        }
    }

    @Override
    public void visit(TypeNode node) {
        String typeName = node.getTypeName();
        if (typeName == null || typeName.isEmpty()) {
            errors.add( "TypeError : TypeNode has an invalid or missing type name");
            currentType = null;
            return;
        }

        if ("bool".equals(typeName)) {
            typeName = "boolean"; 
        }

        if (!validTypes.contains(typeName) && !symbolTable.containsKey(typeName)) {
            errors.add( "TypeError : Unknown type '" + typeName + "'");
            currentType = null;
        } else {
            currentType = typeName;
        }
    }

    @Override
    public void visit(UnaryExpression node) {
        String operator = node.getOperator();
        ASTNode expression = node.getExpression();

        if (expression == null) {
            errors.add( "TypeError : Unary expression contains a null expression");
            return;
        }

        expression.accept(this);

        if ("-".equals(operator) || "+".equals(operator)) {
            if (!"int".equals(currentType) && !"float".equals(currentType)) {
                errors.add( "TypeError : Unary operator '" + operator + "' requires an int or float operand, found '" + currentType + "'");
            }
        } else if ("!".equals(operator)) {
            if (!"boolean".equals(currentType)) {
                errors.add( "TypeError : Unary operator '!' requires a boolean operand, found '" + currentType + "'");
            }
        } else {
            errors.add("TypeError : Unsupported unary operator: " + operator);
        }
    }

    
    @Override
    public void visit(VariableDeclaration node) {
        String variableName = node.getVariableName();
        ASTNode typeNode = node.getType();
        ASTNode initializer = node.getInitializer();

        if (variableName == null || variableName.isEmpty()) {
            errors.add( "TypeError : Variable has an invalid or missing name");
            return;
        }

        if (typeNode == null) {
            errors.add( "TypeError : Variable '" + variableName + "' has an invalid or missing type");
            return;
        }
        

        typeNode.accept(this);
        String variableType = currentType;

        if (variableType == null) {
            errors.add( "TypeError : Variable '" + variableName + "' has an invalid type");
            return;
        }

        if (symbolTable.containsKey(variableName)) {
            errors.add( "TypeError : Variable '" + variableName + "' is already defined in the current scope");
            return;
        }

        if (initializer != null) {
            initializer.accept(this);
            if (currentType == null || !currentType.equals(variableType)) {
                errors.add( "TypeError : Initializer type '" + currentType + "' does not match variable type '" + variableType + "' for '" + variableName + "'");
            }
        }

        symbolTable.put(variableName, variableType);
    }


    @Override
    public void visit(VariableExpression node) {
        String variableName = node.getVariableName();

        if (variableName == null || variableName.isEmpty()) {
            errors.add("ScopeError : Variable expression has an invalid or missing name");
            currentType = null;
        }

        if (!symbolTable.containsKey(variableName)) {
            errors.add( "ScopeError : Variable '" + variableName + "' is not definedo");
            currentType = null;

        }


        currentType = symbolTable.get(variableName);
    }


    @Override
    public void visit(WhileStatement node) {
        ASTNode condition = node.getCondition();
        ASTNode block = node.getBlock();

        if (condition == null) {
            errors.add( "MissingConditionError : While statement is missing a condition");
        }

        condition.accept(this);
        if (!"boolean".equals(currentType)) {
            errors.add( "MissingConditionError : Condition in 'while' statement must be of type boolean");
        }

        if (block != null) {
            block.accept(this);
        } else {
            errors.add( "While statement is missing a block");
        }
    }

    @Override
    public void visit(IndexExpression node) {
        node.getArray().accept(this);
        String arrayType = currentType;

        if (!arrayType.endsWith("[]") && !"string".equals(arrayType)) {
            errors.add("TypeError : Index operator is not valid for type: " + arrayType);
            currentType = null;
            return;
        }

        node.getIndex().accept(this);
        String indexType = currentType;

        if (!"int".equals(indexType)) {
            errors.add("TypeError : Index expression must be of type int, found: " + indexType);
            currentType = null;
            return;
        }

        if (arrayType.endsWith("[]")) {
            currentType = arrayType.substring(0, arrayType.length() - 2); 
        } else if ("string".equals(arrayType)) {
            currentType = "int"; 
        }
    }











    
    // Helper methods //

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void printErrors() {
        for (String error : errors) {
            System.err.println(error);
        }
    }

    private boolean isRecordType(String type) {
        return type.startsWith("record<") && type.endsWith(">");
    }
    
    
}

