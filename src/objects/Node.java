package objects;

import org.omg.CORBA.UNKNOWN;

public class Node {
    String name;
    TypeObject typeObject;
    public TypeData typeData;

    public int n;
    public Object value = 0;
    public int ptr = 0;

    public void setPtr(int ptr) {
        this.ptr = ptr;
    }

    public String getString() {
        return String.valueOf(value);
    }

    public double getDouble() {
        if (typeData == TypeData.CHAR)
            return (double)(String.valueOf(value).charAt(0));
        else
            return Double.parseDouble(String.valueOf(value));
    }

    public int getInteger() {
        if (typeData == TypeData.INTEGER || typeData == TypeData.DOUBLE || typeData == TypeData.UNKNOW)
            return (int) Double.parseDouble(String.valueOf(value));
        else
            return (int) String.valueOf(value).charAt(0);
    }

    private void printError(String text) {
        System.out.println("Error: " + text);
        System.exit(1);
    }

    public TypeObject getTypeObject() {
        return typeObject;
    }

    public static Node createVar(String name, TypeData typeData) {
        Node node = new Node();
        node.typeObject = TypeObject.VAR;
        node.name = name;
        node.typeData = typeData;
        return node;
    }

    public static Node createArray(String name, TypeData typeData, int n) {
        Node node = new Node();
        node.typeObject = TypeObject.ARRAY;
        node.name = name;
        node.typeData = typeData;
        node.n = n;
        node.value = null;
        return node;
    }

    public static Node createFunction(String name) {
        Node node = new Node();
        node.typeObject = TypeObject.FUNCTION;
        node.name = name;
        node.typeData = TypeData.VOID;
        return node;
    }

    public static Node createClass(String name) {
        Node node = new Node();
        node.typeObject = TypeObject.CLASS;
        node.name = name;
        node.typeData = TypeData.VOID;
        return node;
    }

    public static Node createEmptyNode() {
        Node node = new Node();
        node.typeObject = TypeObject.EMPTY;
        return node;
    }

    public static Node createConst(TypeData typeData, Object value) {
        Node node = new Node();
        node.typeObject = TypeObject.CONST;
        node.typeData = typeData;
        node.value = value;
        return node;
    }

    public static Node createUnknown() {
        Node node = new Node();
        node.typeObject = TypeObject.CONST;
        node.typeData = TypeData.UNKNOW;
        return node;
    }

    @Override
    public String toString() {
        if (typeObject == TypeObject.EMPTY)
            return typeObject.toString();

        String str = typeObject.toString();
        if (typeObject != TypeObject.CLASS)
            str += " " + typeData;
        str += " " + name;
        if (typeObject == TypeObject.ARRAY) {
            str += " n=" + n;
            if (value == null) {
                str += " Value=null";
            }
        }
        if (typeObject == TypeObject.VAR) {
            if (typeData == TypeData.DOUBLE)
                str += " Value=" + Double.parseDouble(String.valueOf(value));
            else
                str += String.format(" Value='%s'", value);
        } else if (typeObject == TypeObject.FUNCTION) {
            str += " ptr=" + ptr;
        }

        return str;
    }
}
