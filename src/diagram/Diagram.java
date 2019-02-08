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
    }

    public void program() {
        next(TokenType.CLASS, "Ожидался класс");
        next(TokenType.ID, "Ожидался идентификатор");
        semAddClass();
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
        tree = thisTree = new ProgramTree(token.getText());
        thisTree.setRight(Node.createEmptyNode());
        thisTree = thisTree.right;
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
        if (thisTree.findUpFunction(token.getText()) != null)
            semPrintError("Функция '" + token.getText() + "' уже была объявлена");
        thisTree.setLeft(Node.createFunction(token.getText()));
        thisTree = thisTree.left;
    }



    private boolean isOperatorsAndDate() {
        return nextToken.getType() == TokenType.OPEN_CURLY_BRACE;
    }
    private void operatorsAndDate() {
        next(TokenType.OPEN_CURLY_BRACE, "Ожидался символ {");

        semInLevel();
        boolean find = false;
        if (nextToken.getType() != TokenType.CLOSE_CURLY_BRACE) {
            while (nextToken.getType() != TokenType.CLOSE_CURLY_BRACE && nextToken.getType() != TokenType.EOF) {
                if (isOperator()) {
                    find = true;
                    operator();
                } else if (isDate()) {
                    find = true;
                    date();
                } else
                    printError("Неизвестный символ");
            }
            if (!find) {
                printError("Недостижимый код");
            }
        }
        semOutLevel();
        next(TokenType.CLOSE_CURLY_BRACE, "Ожидался символ }");
    }
    private void semInLevel() {
        stack.push(thisTree);
        thisTree.setRight(Node.createEmptyNode());
        thisTree = thisTree.right;
    }
    private void semOutLevel() {
        thisTree = stack.pop();
        if (thisTree.node.getTypeObject() == TypeObject.EMPTY) {
            thisTree.setLeft(Node.createEmptyNode());
            thisTree = thisTree.left;
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
        next(TokenType.ID, "Ожидался идентификатрор");
        Token varName = token;
        semHasVarOrArray(varName);


        if (nextToken.getType() == TokenType.ASSIGN) {
            next(TokenType.ASSIGN, "Ожидался символ =");
            if (isExpression())
                expression1();
            else
                printError("Ожидалось выражение");
            semAddVar(typeData, true, varName);
        } else if (nextToken.getType() == TokenType.OPEN_SQUARE) {
            next(TokenType.OPEN_SQUARE, "Ожидался символ [");
            next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");

            if (nextToken.getType() == TokenType.ASSIGN) {
                next(TokenType.ASSIGN, "Ожидался символ =");
                next(TokenType.NEW, "Ожидался символ new");
                next();

                TypeData typeDataMass;
                if (token.getType() != TokenType.CHAR && token.getType() != TokenType.DOUBLE)
                    printError("Ожидался тип");
                if (token.getType() == TokenType.DOUBLE)
                    typeDataMass = TypeData.DOUBLE;
                else
                    typeDataMass = TypeData.CHAR;

                next(TokenType.OPEN_SQUARE, "Ожидался символ [");
                next(TokenType.TYPE_INT, "Ожидалось целое");
                next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");
                Token tokenN = token;
                semHasArray(varName, tokenN);
                if (typeData == typeDataMass)
                    semAddArray(typeData, true, varName, Integer.parseInt(tokenN.getText()));
                else
                    semPrintError("Не верный тип массива");
            } else {
                semAddArray(typeData, false, varName, 0);
            }
            return;
        }
    }
    private void semHasArray(Token varName, Token tokenN) {
        if (thisTree.findUpArray(varName.getText()) != null) {
            Node mass = thisTree.findUpArray(varName.getText()).node;
            mass.n = Integer.parseInt(tokenN.getText());
            mass.isInit = true;
        }
    }
    private void semHasVarOrArray(Token varName) {
        if (thisTree.findUpVarOrArray(varName.getText()) != null)
            semPrintError("Идентификатор " + varName.getText() + " уже использовался");
    }
    private void semAddArray(TypeData typeData, boolean init, Token name, int n) {
        if (thisTree.findUpArray(name.getText()) != null) {
            semPrintError("Массив " + name.getText() + " уже существует");
        } else {
            thisTree.setLeft(Node.createArray(name.getText(), typeData, n));
            thisTree.left.node.isInit = init;
            thisTree = thisTree.left;
        }
    }
    private void semAddVar(TypeData typeData, boolean init, Token name) {
        if (thisTree.findUpVar(name.getText()) != null) {
            semPrintError("Переменная " + name.getText() + " уже существует");
        } else {
            thisTree.setLeft(Node.createVar(name.getText(), typeData));
            thisTree.left.node.isInit = init;
            thisTree = thisTree.left;
        }
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
            Token tokenName = token;
            if (isAssignment()) {
                assignment(tokenName);
            }
            else if (nextToken.getType() == TokenType.OPEN_PARENTHESIS) {
                next(TokenType.OPEN_PARENTHESIS, "Ожидался символ (");
                if (thisTree.findUpFunction(tokenName.getText()) == null)
                    semPrintError("Функция '" + tokenName.getText() + "()' не определена");
                next(TokenType.CLOSE_PARENTHESIS, "Ожидался символ )");
                next(TokenType.SEMICOLON, "Ожидался символ ;");
            } else if (nextToken.getType() == TokenType.OPEN_SQUARE) {
                next(TokenType.OPEN_SQUARE, "Ожидался символ [");

                next(TokenType.TYPE_INT, "Ожидалось целое");
                Token indexArray = token;
                if (thisTree.findUpArray(tokenName.getText()) != null) {
                    Node mass = thisTree.findUpArray(tokenName.getText()).node;
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



    private boolean isAssignment() {
        return nextToken.getType() == TokenType.ASSIGN;
    }
    private void assignment(Token tokenName) {
        next(TokenType.ASSIGN, "Ожидался символ =");
        if (isExpression()) {
            Node node = expression1();
            if(thisTree.findUpVarOrArray(tokenName.getText()) == null)
                semPrintError("Переменная или массив '" + tokenName.getText() + "' не найдена");
            else {
                if (inType(thisTree.findUpVarOrArray(tokenName.getText()).node.typeData, node.typeData)) {
                    Node mass = thisTree.findUpVarOrArray(tokenName.getText()).node;
                    mass.isInit = true;
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

            if(thisTree.findUpArray(tokenName.getText()) == null)
                semPrintError("Массив '" + tokenName.getText() + "' не найден");
            else {
                TypeData typeDataMass;
                if (typeMass == TokenType.CHAR)
                    typeDataMass = TypeData.CHAR;
                else
                    typeDataMass = TypeData.DOUBLE;
                if (thisTree.findUpArray(tokenName.getText()).node.typeData  == typeDataMass) {
                    Node mass = thisTree.findUpVarOrArray(tokenName.getText()).node;
                    mass.isInit = true;
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
        if (typeData1 == TypeData.CHAR && typeData2 == TypeData.CHAR)
            return true;
        return false;
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
                return Node.createUnknow();
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
                next(TokenType.TYPE_INT, "Ожидалось целое");
                next(TokenType.CLOSE_SQUARE, "Ожидался символ ]");
                if (thisTree.findUpVarOrArray(tokenName.getText()) != null)
                    return Node.createConst(thisTree.findUpVarOrArray(tokenName.getText()).node.typeData);
                else {
                    semPrintError("Неизвестная переменная");
                    return Node.createUnknow();
                }
            }
            else {
                if (thisTree.findUpVarOrArray(tokenName.getText()) != null)
                    return Node.createConst(thisTree.findUpVarOrArray(tokenName.getText()).node.typeData);
                else {
                    semPrintError("Неизвестная переменная");
                    return Node.createUnknow();
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
            return Node.createConst(TypeData.INTEGER);
        } else if (token.getType() == TokenType.TYPE_DOUBLE) {
            return Node.createConst(TypeData.DOUBLE);
        } else if (token.getType() == TokenType.TYPE_STRING) {
            return Node.createConst(TypeData.STRING);
        } else {
            return Node.createConst(TypeData.CHAR);
        }
    }
}
