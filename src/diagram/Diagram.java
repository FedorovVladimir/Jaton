package diagram;

import objects.Node;
import objects.ProgramTree;
import objects.TypeData;
import objects.TypeObject;
import parser.Scanner;
import parser.Token;
import parser.TokenType;

public class Diagram extends ClassicDiagram {
    private int number = -1;

    public Diagram(Scanner scanner) {
        super(scanner);
        tree = new ProgramTree(Node.createFunction("print"));
        tree.setLeft(Node.createFunction("scan"));
        tree = tree.left;
        tree.setLeft(Node.createFunction("println"));
        tree = tree.left;
    }

    public void program() {
        number++;
        scanner.reset();
        next(TokenType.CLASS, "Ожидался class");
        next(TokenType.ID, "Ожидался идентификатор класса");
        interAddClass();
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
    private void interAddClass() {
        if (fInits.peek())
            semAddClass();
    }
    private void semAddClass() {
        tree.setLeft(Node.createClass(token.getText()));
        tree = tree.left;
        tree.setRight(Node.createEmptyNode());
        tree = tree.right;
    }


    private boolean isFunction() {
        return nextToken.getType() == TokenType.VOID ||
                nextToken.getType() == TokenType.PUBLIC;
    }
    private void function() {
        next();

        Token name;
        if (token.getType() == TokenType.VOID) {
            next(TokenType.ID, "Ожидался идентификатор");
            name = token;
            setFlag(false);
        } else {
            next(TokenType.STATIC, "Ожидался static");
            next(TokenType.VOID, "Ожидался void");
            next(TokenType.MAIN, "Ожидался main");
            name = token;
        }
        next(TokenType.OPEN_PARENTHESIS, "Ожидался символ (");
        next(TokenType.CLOSE_PARENTHESIS, "Ожидался символ )");
        semAddFunction(scanner.getNumberSymbol(), name);

        if (isOperatorsAndDate()) {
            operatorsAndDate();
        }
        else {
            printError("Ожидался символ {");
        }
        setFlag(true);
    }
    private void semAddFunction(int numberSymbol,  Token name) {
        if (tree.findUpFunction(name.getText()) != null)
            if (number == 0)
                semPrintError("Функция '" + name.getText() + "' уже была объявлена");
        if (number == 0) {
            Node f = Node.createFunction(name.getText());
            f.setPtr(numberSymbol);
            tree.setLeft(f);
        } else {
            tree.setLeft(Node.createEmptyNode());
        }
        tree = tree.left;
    }



    private boolean isOperatorsAndDate() {
        return nextToken.getType() == TokenType.OPEN_CURLY_BRACE;
    }
    private void operatorsAndDate() {
        next(TokenType.OPEN_CURLY_BRACE, "Ожидался символ {");
        semInLevel();

        while (nextToken.getType() != TokenType.CLOSE_CURLY_BRACE && nextToken.getType() != TokenType.EOF) {
            if (isOperator())
                operator();
            else if (isDate())
                date();
            else
                printError("Неизвестный символ");
        }

        semOutLevel();
        next(TokenType.CLOSE_CURLY_BRACE, "Ожидался символ }");
    }
    private void semInLevel() {
        if (fInits.peek()) {
            callStack.push(tree);
            tree.setRight(Node.createEmptyNode());
            tree = tree.right;
        }
    }
    private void semOutLevel() {
        if (fInits.peek()) {
            tree = callStack.pop();
            if (tree.node.getTypeObject() == TypeObject.EMPTY) {
                tree.setLeft(Node.createEmptyNode());
                tree = tree.left;
            }
        }
    }



    private boolean isDate() {
        return nextToken.getType() == TokenType.DOUBLE ||
                nextToken.getType() == TokenType.CHAR;
    }
    private void date() {
        TypeData typeData;
        if (nextToken.getType() == TokenType.CHAR) {
            typeData = TypeData.CHAR;
        } else  {
            typeData = TypeData.DOUBLE;
        }

        do {
            next();
            if (isVariable())
                variable(typeData);
            else
                printError("Ожидался идентификатор");
        } while (nextToken.getType() == TokenType.COMMA);

        next(TokenType.SEMICOLON, "Ожидался символ ;");
    }



    private boolean isVariable() {
        return nextToken.getType() == TokenType.ID;
    }
    private void variable(TypeData typeData) {
        next(TokenType.ID, "Ожидался идентификатор");
        Token id = token;
        semHasVarOrArray(id);

        if (nextToken.getType() == TokenType.OPEN_SQUARE) {
            next(TokenType.OPEN_SQUARE, "Ожидался символ [");
            next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");

            interAddArray(typeData, id);

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

                Node node = expression1();
                semTypeNumber(node.typeData);

                next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");
                if (typeData == typeDataMass) {
                    interInitArray(id, node.getInteger());
                }
                else
                    semPrintError("Не верный тип массива");
            }
        } else {
            interAddVar(typeData, id);
            if (nextToken.getType() == TokenType.ASSIGN) {
                next(TokenType.ASSIGN, "Ожидался символ =");
                if (isExpression()) {
                    Node expression = expression1();
                    interInitVar(id, expression);
                } else {
                    printError("Ожидалось выражение");
                }
            }
        }
    }
    private void semHasVarOrArray(Token varName) {
        if (tree.findUpVarOrArray(varName.getText()) != null)
            semPrintError("Идентификатор " + varName.getText() + " уже использовался");
    }
    private void interAddVar(TypeData typeData, Token name) {
        if (fInits.peek())
            semAddVar(typeData, name);
    }
    private void semAddVar(TypeData typeData, Token name) {
        if (tree.findUpVar(name.getText()) != null) {
            semPrintError("Переменная " + name.getText() + " уже существует");
        } else {
            tree.setLeft(Node.createVar(name.getText(), typeData));
            tree = tree.left;
        }
    }
    private void interInitVar(Token varName, Node expression) {
        if (fInits.peek())
            semInitVar(varName, expression);
    }
    private void semInitVar(Token varName, Node expression) {
        Node node = tree.findUpVar(varName.getText()).node;
        node.value = expression.value;
    }
    private void interAddArray(TypeData typeData, Token name) {
        if (fInits.peek()) {
            semAddArray(typeData, name);
        }
    }
    private void semAddArray(TypeData typeData, Token name) {
        if (tree.findUpArray(name.getText()) != null) {
            semPrintError("Массив " + name.getText() + " уже существует");
        } else {
            tree.setLeft(Node.createArray(name.getText(), typeData, 0));
            tree = tree.left;
        }
    }
    private void interInitArray(Token varName, int parseInt) {
        if (fInits.peek())
            semInitArray(varName, parseInt);
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
    private void semTypeNumber(TypeData typeData) {
        if (typeData != TypeData.INTEGER && typeData != TypeData.CHAR)
            printError("Ожидалось целое");
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
            Token id = token;

            if (isAssignment()) {
                Node node = assignment();
                if (fInits.peek()) {
                    tree.findUpVar(id.getText()).node.value = node.value;
                }
                next(TokenType.SEMICOLON, "Ожидался символ ;");
            } else if (nextToken.getType() == TokenType.OPEN_PARENTHESIS) {
                next(TokenType.OPEN_PARENTHESIS, "Ожидался символ (");

                if (isExpression() && id.getText().equals("print"))
                    print(expression1());
                if (isExpression() && id.getText().equals("println"))
                    println(expression1());
                if (id.getText().equals("scan")) {
                    next();
                    scan(token);
                }

                semFindFunction(id);
                next(TokenType.CLOSE_PARENTHESIS, "Ожидался символ )");
                next(TokenType.SEMICOLON, "Ожидался символ ;");
            } else if (nextToken.getType() == TokenType.OPEN_SQUARE) {
                next(TokenType.OPEN_SQUARE, "Ожидался символ [");

                Node node = expression1();
                semTypeNumber(node.typeData);

                if (tree.findUpArray(id.getText()) != null) {
                    Node mass = tree.findUpArray(id.getText()).node;
                    if (mass.n <= node.getInteger())
                        semPrintError("Выход за границу массива");
                }
                next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");

                if (isAssignment())
                    assignment();
                next(TokenType.SEMICOLON, "Ожидался символ ;");
            } else {
                printError("Неизвестная команда");
            }
        } else
            printError("Неизвестный оператор");
    }
    private void semFindFunction(Token id) {
        if (tree.findUpFunction(id.getText()) == null)
            semPrintError("Функция '" + id.getText() + "()' не определена");
    }
    private void scan(Token id) {
        if (fInits.peek()) {
            java.util.Scanner in = new java.util.Scanner(System.in);
            tree.findUpVar(id.getText()).node.value = in.next();
        }
    }
    private void print(Node expression1) {
        if (fInits.peek()) {
            TypeObject typeObject = expression1.getTypeObject();
            TypeData typeData = expression1.typeData;
            Object value = expression1.value;
            if (typeObject == TypeObject.CONST) {
                if (typeData == TypeData.DOUBLE) {
                    System.out.print(Double.parseDouble(String.valueOf(value)));
                } else if (typeData == TypeData.INTEGER) {
                    System.out.print(Integer.parseInt(String.valueOf(value)));
                } else if (typeData == TypeData.STRING) {
                    System.out.print(value);
                } else {
                    System.out.println(String.format("%s", value));
                }
            } else if (typeObject == TypeObject.VAR) {
                if (typeData == TypeData.DOUBLE) {
                    System.out.print(Double.parseDouble(String.valueOf(value)));
                } else {
                    System.out.print(String.format("%s", value));
                }
            }
        }
    }
    private void println(Node expression1) {
        if (fInits.peek()) {
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
                } else {
                    System.out.println(String.format("%s", value));
                }
            } else if (typeObject == TypeObject.VAR) {
                if (typeData == TypeData.DOUBLE) {
                    System.out.println(Double.parseDouble(String.valueOf(value)));
                } else {
                    System.out.println(String.format("%s", value));
                }
            }
        }
    }



    private boolean isAssignment() {
        return nextToken.getType() == TokenType.ASSIGN;
    }
    private Node assignment() {
        next(TokenType.ASSIGN, "Ожидался символ =");
        if (isExpression()) {
            return expression1();
        } else  {
            next(TokenType.NEW, "Ожидался new");
            next();
            TokenType typeMass =  token.getType();
            if (typeMass != TokenType.DOUBLE && typeMass != TokenType.CHAR)
                printError("Ожидался тип");
            next(TokenType.OPEN_SQUARE, "Ожидался символ [");

            Node node = expression1();
            semTypeNumber(node.typeData);

            next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");
            return Node.createArray("", typeMass == TokenType.DOUBLE?TypeData.DOUBLE:TypeData.CHAR, node.getInteger());
        }
    }



    private boolean isLoopWhile() {
        return nextToken.getType() == TokenType.WHILE;
    }
    private void loopWhile() {
        next(TokenType.WHILE, "Ожидался while");
        next(TokenType.OPEN_PARENTHESIS, "Ожидался символ (");

        if (fInits.peek()) {
            boolean fInit = false;
            do {
                int col = scanner.getNumberCol();
                int row = scanner.getNumberRow();
                int symbol = scanner.getNumberSymbol();
                if (isExpression() || fInit) {
                    Node node = expression1();
                    fInit = (node.getInteger() > 0);
                } else {
                    printError("Ожидалось выражение");
                }
                next(TokenType.CLOSE_PARENTHESIS, "Ожидался символ )");
                if (fInit) {
                    fInits.push(true);
                } else {
                    fInits.push(false);
                }

                if (isOperator()) {
                    operator();
                }
                else {
                    printError("Ожидался оператор");
                }

                fInits.pop();
                if (fInit) {
                    scanner.setNumberCol(col);
                    scanner.setNumberRow(row);
                    scanner.setNumberSymbol(symbol);

                }
            } while (fInit);
        } else {
            if (isExpression()) {
                expression1();
            } else
                printError("Ожидалось выражение");
            next(TokenType.CLOSE_PARENTHESIS, "Ожидался символ )");
            if (isOperator())
                operator();
            else
                printError("Ожидался оператор");
        }
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
        Node res = Node.createVar("sum", node.typeData);
        res.value = node.value;

        while (nextToken.getType() == TokenType.PLUS || nextToken.getType() == TokenType.MINUS) {
            Token znak = nextToken;
            next();
            Node node2 = expression4();

            TypeData typeData = toTypeDataPlusMinus(res.typeData, node2.typeData);
            if (typeData == TypeData.UNKNOW)
                semPrintError("Неопределенный тип");
            if (znak.getType() == TokenType.PLUS) {
                if (typeData == TypeData.STRING) {
                    res.value = res.getString() + node2.getString();
                } else if (typeData == TypeData.DOUBLE) {
                    res.value = res.getDouble() + node2.getDouble();
                } else {
                    res.value = res.getInteger() + node2.getInteger();
                }
            } else {
                if (typeData == TypeData.STRING) {
                    printError("Не возможно вычесть");
                } else if (typeData == TypeData.DOUBLE) {
                    res.value = res.getDouble() - node2.getDouble();
                } else {
                    res.value = res.getInteger() - node2.getInteger();
                }
            }
            res.typeData = typeData;
        }
        return res;
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
                    return tree.findUpVarOrArray(tokenName.getText()).node;
                else {
                    semPrintError("Неизвестная переменная");
                    return Node.createUnknown();
                }
            }
            else {
                if (tree.findUpVarOrArray(tokenName.getText()) != null)
                    return tree.findUpVarOrArray(tokenName.getText()).node;
                else {
                    if (fInits.peek()) {
                        semPrintError("Неизвестная переменная");
                    }
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

    public void setFlag(boolean b) {
        fInits.poll();
        fInits.add(b);
    }
}
