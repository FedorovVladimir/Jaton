package diagram;

import objects.Node;
import objects.ProgramTree;
import objects.TypeData;
import objects.TypeObject;
import parser.Scanner;
import parser.Token;
import parser.TokenType;

public class Diagram extends ClassicDiagram {

    public Diagram(Scanner scanner) {
        super(scanner);
        tree = new ProgramTree(Node.createFunction("print"));
    }

    public void program() {
        next(TokenType.CLASS, "Ожидался class");
        next(TokenType.ID, "Ожидался идентификатор класса");
        semAddClass(); // TODO: 09.02.2019 add to diagram
        next(TokenType.OPEN_CURLY_BRACE, "Ожидался символ {");
        while (nextToken.getType() != TokenType.CLOSE_CURLY_BRACE && nextToken.getType() != TokenType.EOF) {
            if (isFunction())
                function();
            else if (isDate())
                date();
            else
                printError("Неизвестный символ");
        }
        next(TokenType.CLOSE_CURLY_BRACE, "Ожидался символ }");
    }
    private void semAddClass() {
        tree.setLeft(Node.createClass(token.getText()));
        tree = tree.left;
        tree.setRight(Node.createEmptyNode());
        tree = tree.right;
    }


    private boolean isFunction() {
        return nextToken.getType() == TokenType.VOID || nextToken.getType() == TokenType.PUBLIC;
    }
    private void function() {
        next();

        if (token.getType() == TokenType.VOID) {
            next(TokenType.ID, "Ожидался идентификатор");
        } else {
            next(TokenType.STATIC, "Ожидался static");
            next(TokenType.VOID, "Ожидался void");
            next(TokenType.MAIN, "Ожидался main");
        }
        semAddFunction();
        
        next(TokenType.OPEN_PARENTHESIS, "Ожидался символ (");
        next(TokenType.CLOSE_PARENTHESIS, "Ожидался символ )");

        if (isOperatorsAndDate())
            operatorsAndDate();
        else
            printError("Ожидался символ {");
    }
    private void semAddFunction() {
        if (tree.findUpFunction(token.getText()) != null)
            semPrintError("Функция '" + token.getText() + "' уже была объявлена");
        tree.setLeft(Node.createFunction(token.getText()));
        tree = tree.left;
    }



    private boolean isOperatorsAndDate() {
        return nextToken.getType() == TokenType.OPEN_CURLY_BRACE;
    }
    private void operatorsAndDate() {
        next();
        semInLevel();
        if (nextToken.getType() != TokenType.CLOSE_CURLY_BRACE) {
            while (nextToken.getType() != TokenType.CLOSE_CURLY_BRACE && nextToken.getType() != TokenType.EOF) {
                if (isOperator())
                    operator();
                else if (isDate())
                    date();
                else
                    printError("Неизвестный символ");
            }
        }
        semOutLevel();
        next(TokenType.CLOSE_CURLY_BRACE, "Ожидался символ }");
    }
    private void semInLevel() {
        callStack.push(tree);
        tree.setRight(Node.createEmptyNode());
        tree = tree.right;
    }
    private void semOutLevel() {
        tree = callStack.pop();
        if (tree.node.getTypeObject() == TypeObject.EMPTY) {
            tree.setLeft(Node.createEmptyNode());
            tree = tree.left;
        }
    }



    private boolean isDate() {
        return nextToken.getType() == TokenType.DOUBLE || nextToken.getType() == TokenType.CHAR;
    }
    private void date() {
        next();

        TypeData typeData;
        if (token.getType() == TokenType.CHAR) {
            typeData = TypeData.CHAR;
        } else  {
            typeData = TypeData.DOUBLE;
        }

        if (isVariable())
            variable(typeData);
        else
            printError("Ожидался идентификатор");

        while (nextToken.getType() == TokenType.COMMA) {
            next();
            if (isVariable())
                variable(typeData);
            else
                printError("Ожидался идентификатор");
        }

        next(TokenType.SEMICOLON, "Ожидался символ ;");
    }



    private boolean isVariable() {
        return nextToken.getType() == TokenType.ID;
    }
    private void variable(TypeData typeData) {
        next();
        Token varName = token;
        semHasVarOrArray(varName);

        if (nextToken.getType() == TokenType.ASSIGN) {
            semAddVar(typeData, varName);
            next(TokenType.ASSIGN, "Ожидался символ =");
            if (isExpression()) {
                Node expression = expression1();
                semInitVar(varName, expression);
            }
            else {
                printError("Ожидалось выражение");
            }
        } else if (nextToken.getType() == TokenType.OPEN_SQUARE) {
            next(TokenType.OPEN_SQUARE, "Ожидался символ [");
            next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");

            semAddArray(typeData, varName);

            if (nextToken.getType() == TokenType.ASSIGN) {
                next(TokenType.ASSIGN, "Ожидался символ =");
                next(TokenType.NEW, "Ожидался символ new");

                if (nextToken.getType() != TokenType.CHAR && nextToken.getType() != TokenType.DOUBLE) {
                    printError("Ожидался тип");
                }
                TypeData typeDataMass;
                if (nextToken.getType() == TokenType.DOUBLE)
                    typeDataMass = TypeData.DOUBLE;
                else
                    typeDataMass = TypeData.CHAR;
                next();

                semArrayTypes(typeData, typeDataMass);
                next(TokenType.OPEN_SQUARE, "Ожидался символ [");
<<<<<<< HEAD
                next(TokenType.TYPE_INT, "Ожидалось целое");
                Token tokenN = token;
                next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");
                semHasArray(varName, tokenN);
                if (typeData == typeDataMass)
                    semAddArray(typeData, true, varName, Integer.parseInt(tokenN.getText()));
                else
                    semPrintError("Не верный тип массива");
            } else {
                semAddArray(typeData, false, varName, 0);
=======
                next(TokenType.TYPE_INT, "Ожидалось целое"); // TODO: 09.02.2019 add type int
                Token tokenN = token;
                next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");
                semInitArray(varName, Integer.parseInt(tokenN.getText()));
>>>>>>> 71642b1d0399ef692ea8c7385726b973c520a833
            }
        } else {
            semAddVar(typeData, varName);
        }
    }
    private void semHasVarOrArray(Token varName) {
        if (tree.findUpVarOrArray(varName.getText()) != null)
            semPrintError("Идентификатор " + varName.getText() + " уже использовался");
    }
    private void semAddVar(TypeData typeData, Token name) {
        if (tree.findUpVar(name.getText()) != null) {
            semPrintError("Переменная " + name.getText() + " уже существует");
        } else {
            tree.setLeft(Node.createVar(name.getText(), typeData));
            tree = tree.left;
        }
    }
    private void semInitVar(Token varName, Node expression) {
        Node node = tree.findUpVar(varName.getText()).node;
        node.value = expression.value;
    }
    private void semAddArray(TypeData typeData, Token name) {
        if (tree.findUpArray(name.getText()) != null) {
            semPrintError("Массив " + name.getText() + " уже существует");
        } else {
            tree.setLeft(Node.createArray(name.getText(), typeData, 0));
            tree = tree.left;
        }
    }
    private void semInitArray(Token varName, int parseInt) {
        Node node = tree.findUpArray(varName.getText()).node;
        node.n = parseInt;
        if (node.typeData == TypeData.DOUBLE)
            node.value = new double[parseInt];
        else
            node.value = new char[parseInt];
    }
    private void semArrayTypes(TypeData typeData, TypeData typeDataMass) {
        if (typeData != typeDataMass)
            printError("Не верный тип массива");
    }



    private boolean isOperator() {
        return nextToken.getType() == TokenType.SEMICOLON ||
                isOperatorsAndDate() ||
                isLoopWhile() ||
                nextToken.getType() == TokenType.ID;
    }
    private void operator() {

        if (nextToken.getType() == TokenType.SEMICOLON)
            next(TokenType.SEMICOLON, "Ожидался символ ;");
        else if (isOperatorsAndDate()) {
            operatorsAndDate();
        }
        else if (isLoopWhile())
            loopWhile();
        else if (nextToken.getType() == TokenType.ID) {
            next(TokenType.ID, "Ожидался идентификатор");
            Token nameFunction = token;
            Token tokenName = token;
            if (isAssignment()) {
                assignment(tokenName);
            }
            else if (nextToken.getType() == TokenType.OPEN_PARENTHESIS) {
                next(TokenType.OPEN_PARENTHESIS, "Ожидался символ (");
                if (isExpression() && nameFunction.getText().equals("print"))
                    print(expression1());
                if (tree.findUpFunction(tokenName.getText()) == null)
                    semPrintError("Функция '" + tokenName.getText() + "()' не определена");
                next(TokenType.CLOSE_PARENTHESIS, "Ожидался символ )");
                next(TokenType.SEMICOLON, "Ожидался символ ;");
            } else if (nextToken.getType() == TokenType.OPEN_SQUARE) {
                next(TokenType.OPEN_SQUARE, "Ожидался символ [");

                next(TokenType.TYPE_INT, "Ожидалось целое");
                Token indexArray = token;
                if (tree.findUpArray(tokenName.getText()) != null) {
                    Node mass = tree.findUpArray(tokenName.getText()).node;
                    if (mass.n <= Integer.parseInt(indexArray.getText()))
                        semPrintError("Выход за границу массива");
                }
                next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");

                if (isAssignment())
                    assignment(tokenName);
            } else {
                printError("Неизвестная команда");
            }
        } else
            printError("Неизвестный оператор");

    }

    private void print(Node expression1) {
        TypeObject typeObject = expression1.getTypeObject();
        TypeData typeData = expression1.typeData;
        Object value = expression1.value;
        if (typeObject == TypeObject.CONST) {
            if (typeData == TypeData.DOUBLE) {
                System.out.println(Double.parseDouble(String.valueOf(value)));
            } else if (typeData == TypeData.INTEGER) {
                System.out.println(Integer.parseInt(String.valueOf(value)));
            } else if (typeData == TypeData.STRING) {
                System.out.println(value);
            }

        }

//        if (typeObject == TypeObject.VAR) {
//            if (typeData == TypeData.DOUBLE)
//                System.out.println(Double.parseDouble(String.valueOf(value)));
//            else
//                System.out.println(String.format("%s", value));
//        }
    }


    private boolean isAssignment() {
        return nextToken.getType() == TokenType.ASSIGN;
    }
    private void assignment(Token tokenName) {
        next(TokenType.ASSIGN, "Ожидался символ =");
        if (isExpression()) {
            Node node = expression1();
            if(tree.findUpVarOrArray(tokenName.getText()) == null)
                semPrintError("Переменная или массив '" + tokenName.getText() + "' не найдена");
            else {
                if (inType(tree.findUpVarOrArray(tokenName.getText()).node.typeData, node.typeData)) {
                    Node mass = tree.findUpVarOrArray(tokenName.getText()).node;
                } else {
                    semPrintError("Не верный тип");
                }
            }
        } else  {
            next(TokenType.NEW, "Ожидался new");
            next();
            TokenType typeMass =  token.getType();
            if (typeMass != TokenType.DOUBLE && typeMass != TokenType.CHAR)
                printError("Ожидался тип");
            next(TokenType.OPEN_SQUARE, "Ожидался символ [");
            next(TokenType.TYPE_INT, "Ожидалось целое");
            Token tokenN = token;

            if(tree.findUpArray(tokenName.getText()) == null)
                semPrintError("Массив '" + tokenName.getText() + "' не найден");
            else {
                TypeData typeDataMass;
                if (typeMass == TokenType.CHAR)
                    typeDataMass = TypeData.CHAR;
                else
                    typeDataMass = TypeData.DOUBLE;
                if (tree.findUpArray(tokenName.getText()).node.typeData  == typeDataMass) {
                    Node mass = tree.findUpVarOrArray(tokenName.getText()).node;
                    mass.n = Integer.parseInt(tokenN.getText());
                } else {
                    semPrintError("Не верный тип массива");
                }
            }
            next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");
            Node.createArray("", TypeData.DOUBLE, 3);
        }
    }
    private boolean inType(TypeData typeData1, TypeData typeData2) {
        if (typeData1 == TypeData.DOUBLE && isNumber(typeData2))
            return true;
        if (typeData1 == TypeData.INTEGER && isNumber(typeData2) && typeData2 != TypeData.DOUBLE)
            return true;
        return typeData1 == TypeData.CHAR && typeData2 == TypeData.CHAR;
    }



    private boolean isLoopWhile() {
        return nextToken.getType() == TokenType.WHILE;
    }
    private void loopWhile() {
        next(TokenType.WHILE, "Ожидался while");
        next(TokenType.OPEN_PARENTHESIS, "Ожидался символ (");
        if (isExpression()) {
            Node node = expression1();
            if (node.typeData != TypeData.CHAR)
                semPrintError("Ожидался тип char");
        }
        else
            printError("Ожидалось выражение");
        next(TokenType.CLOSE_PARENTHESIS, "Ожидался символ )");
        if (isOperator())
            operator();
        else
            printError("Ожидался оператор");
    }



    private boolean isExpression() {
        return nextToken.getType() == TokenType.ID ||
                nextToken.getType() == TokenType.OPEN_PARENTHESIS ||
                nextToken.getType() == TokenType.TYPE_INT ||
                nextToken.getType() == TokenType.TYPE_DOUBLE ||
                nextToken.getType() == TokenType.TYPE_CHAR ||
                nextToken.getType() == TokenType.TYPE_STRING ||
                nextToken.getType() == TokenType.PLUS ||
                nextToken.getType() == TokenType.MINUS;
    }
    private Node expression1() {
        Node node = expression2();
        TypeData typeData = node.typeData;

        while (nextToken.getType() == TokenType.EQUALLY || nextToken.getType() == TokenType.NOT_EQUALLY) {
            next();

            Node node2 = expression2();
            TypeData typeData2 = node2.typeData;
            if (isNumber(typeData) && isNumber(typeData2)) {
                node.typeData = TypeData.CHAR;
            } else {
                node.typeData = TypeData.UNKNOW;
                semPrintError("Неопределенный тип");
            }
        }
        return node;
    }


    private Node expression2() {
        Node node = expression3();
        TypeData typeData = node.typeData;

        while (nextToken.getType() == TokenType.GREAT || nextToken.getType() == TokenType.GREAT_EQUALLY || nextToken.getType() == TokenType.LESS || nextToken.getType() == TokenType.LESS_EQUALLY) {
            next();

            Node node2 = expression3();
            TypeData typeData2 = node2.typeData;
            if (isNumber(typeData) && isNumber(typeData2)) {
                node.typeData = TypeData.CHAR;
            } else {
                node.typeData = TypeData.UNKNOW;
                semPrintError("Неопределенный тип");
            }
        }
        return node;
    }


    private Node expression3() {
        Node node = expression4();
        TypeData typeData = node.typeData;

        while (nextToken.getType() == TokenType.PLUS || nextToken.getType() == TokenType.MINUS) {
            next();
            Node node2 = expression4();
            TypeData typeData2 = node2.typeData;

            node.typeData = toTypeDataPlusMinus(typeData, typeData2);
            if (typeData == TypeData.UNKNOW)
                semPrintError("Неопределенный тип");
        }
        return node;
    }
    private TypeData toTypeDataPlusMinus(TypeData typeData1, TypeData typeData2) {
        if (typeData1 == TypeData.STRING || typeData2 == TypeData.STRING) {
            if (typeData1 != TypeData.UNKNOW && typeData2 != TypeData.UNKNOW) {
                return TypeData.STRING;
            } else {
                return TypeData.UNKNOW;
            }
        } else
            return toTypeDataSlashStar(typeData1, typeData2);
    }


    private Node expression4() {
        Node node = expression5();

        while (nextToken.getType() == TokenType.STAR || nextToken.getType() == TokenType.SLASH || nextToken.getType() == TokenType.PERCENT) {
            next();
            Node node2 = expression5();
            TypeData typeData2 = node2.typeData;

            if (token.getType() == TokenType.PERCENT)
                node.typeData = toTypeDataPercent(node.typeData, typeData2);
            else
                node.typeData = toTypeDataSlashStar(node.typeData, typeData2);

            if (node.typeData == TypeData.UNKNOW) {
                semPrintError("Неопределенный тип");
            }
        }
        return node;
    }
    private TypeData toTypeDataSlashStar(TypeData typeData1, TypeData typeData2) {
        if (typeData1 == TypeData.DOUBLE || typeData2 == TypeData.DOUBLE) {
            if (isNumber(typeData1) && isNumber(typeData2)) {
                return TypeData.DOUBLE;
            } else {
                return TypeData.UNKNOW;
            }
        } else if (typeData1 == TypeData.INTEGER || typeData2 == TypeData.INTEGER) {
            if (isNumber(typeData1) && isNumber(typeData2)) {
                return TypeData.INTEGER;
            } else {
                return TypeData.UNKNOW;
            }
        } else if (typeData1 == TypeData.CHAR || typeData2 == TypeData.CHAR) {
            if (isNumber(typeData1) && isNumber(typeData2)) {
                return TypeData.CHAR;
            } else {
                return TypeData.UNKNOW;
            }
        } else
            return TypeData.UNKNOW;
    }
    private TypeData toTypeDataPercent(TypeData typeData1, TypeData typeData2) {
        if (isNumber(typeData1) && typeData2 == TypeData.INTEGER) {
            return TypeData.INTEGER;
        } else
            return TypeData.UNKNOW;
    }
    private Boolean isNumber(TypeData typeData) {
        return typeData == TypeData.DOUBLE ||
                typeData == TypeData.INTEGER ||
                typeData == TypeData.CHAR;
    }


    private Node expression5() {
        boolean isZnak = false;
        while (nextToken.getType() == TokenType.PLUS || nextToken.getType() == TokenType.MINUS) {
            next();
            isZnak = true;
        }

        Node node = expression6();
        if (isZnak) {
            if (node.typeData != TypeData.DOUBLE && node.typeData != TypeData.INTEGER) {
                semPrintError("Неопределенный тип");
                return Node.createUnknown();
            } else
                return node;
        } else {
            return node;
        }
    }


    private Node expression6() {
        next();

        if (token.getType() == TokenType.ID) {
            Token tokenName = token;

            if (nextToken.getType() == TokenType.OPEN_SQUARE) {
                next(TokenType.OPEN_SQUARE, "Ожидался символ [");
                next(TokenType.TYPE_INT, "Ожидалось целое"); // todo int в диаграммы
                Token n = token;
                next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");
                if (tree.findUpVarOrArray(tokenName.getText()) != null) // TODO: 09.02.2019 char[20]
                    return Node.createConst(tree.findUpVarOrArray(tokenName.getText()).node.typeData, new char[20]);
                else {
                    semPrintError("Неизвестная переменная");
                    return Node.createUnknown();
                }
            }
            else {
                if (tree.findUpVarOrArray(tokenName.getText()) != null)
                    return Node.createConst(tree.findUpVarOrArray(tokenName.getText()).node.typeData, new char[20]);
                else {
                    semPrintError("Неизвестная переменная");
                    return Node.createUnknown();
                }
            }
        } else if (token.getType() == TokenType.OPEN_PARENTHESIS) {
            Node node = null;
            if (isExpression())
                node = expression1();
            else
                printError("Ожидалось выражение");
            next(TokenType.CLOSE_PARENTHESIS, "Ожидался символ )");
            return node;
        } else if (token.getType() == TokenType.TYPE_INT) {
            return Node.createConst(TypeData.INTEGER, Integer.parseInt(token.getText()));
        } else if (token.getType() == TokenType.TYPE_DOUBLE) {
            return Node.createConst(TypeData.DOUBLE, Double.parseDouble(token.getText()));
        } else if (token.getType() == TokenType.TYPE_CHAR) {
            return Node.createConst(TypeData.CHAR, token.getText());
        } else { // TODO: 09.02.2019 string int
            return Node.createConst(TypeData.STRING, token.getText());
        }
    }
}
