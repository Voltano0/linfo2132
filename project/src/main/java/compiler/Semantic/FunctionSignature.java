package compiler.Semantic;

import java.util.List;

public class FunctionSignature {
    private final String name;
    private final List<String> parameterTypes;
    private final String returnType;

    public FunctionSignature(String name, List<String> parameterTypes, String returnType) {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return "FunctionSignature{" +
                "name='" + name + '\'' +
                ", parameterTypes=" + parameterTypes +
                ", returnType='" + returnType + '\'' +
                '}';
    }

    public boolean matches(List<String> argumentTypes) {
        if (argumentTypes.size() != parameterTypes.size()) {
            return false;
        }
        for (int i = 0; i < argumentTypes.size(); i++) {
            if (!argumentTypes.get(i).equals(parameterTypes.get(i))) {
                return false;
            }
        }
        return true;
    }
}