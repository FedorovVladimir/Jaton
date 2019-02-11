package diagram;

import objects.ProgramTree;
import parser.Scanner;
import parser.Token;
import parser.TokenType;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class ClassicDiagram {
    protected Deque<Boolean> fInits = new LinkedList<>();

    protected Scanner scanner;

    Token token;
    Token nextToken;

    ProgramTree tree;

    Deque<ProgramTree> callStack = new LinkedList<>();

    public ProgramTree getTree() {
        return tree;
    }

    ClassicDiagram(Scanner scanner) {
        this.scanner = scanner;
    }

    void next() {
        token = scanner.nextScanner();
        nextToken = nextTokenRead();
    }

    void next(TokenType type, String text) {
        next();
        if (token.getType() != type)
            printError(text);
    }

    private Token nextTokenRead() {
        scanner.save();
        Token token = scanner.nextScanner();
        scanner.ret();
        return token;
    }

    public void printError(String text) {
        System.out.println(text + " строка " + scanner.getNumberRow() + ", столбец " + scanner.getNumberCol());
        System.exit(1);
    }

    void semPrintError(String text) {
        if (fInits.peek()) {
            System.out.println(text + " строка " + scanner.getNumberRow() + ", столбец " + scanner.getNumberCol());
        }
    }
}
