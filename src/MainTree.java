import diagram.Diagram;
import io.Reader;
import parser.Scanner;
import parser.Token;
import parser.TokenType;

public class MainTree {
    public static void main(String[] args) {
        String text;
        try {
            text = Reader.getDate("Test.jaton");
        } catch (Exception e) {
            System.out.println("Неверны тип файла");
            return;
        }

        Scanner scanner = new Scanner(text);
        Diagram diagram = new Diagram(scanner);

        diagram.setFlag(false);
        diagram.program();
        diagram.getTree().getRoot().print(0);

        System.out.println();
        System.out.println();

        diagram.setFlag(true);
        diagram.program();
        diagram.getTree().getRoot().print(0);


        Token token = scanner.nextScanner();
        if (token.getType() == TokenType.EOF) {
            System.out.println("Синтаксических ошибок не обнаружено!");
        }
        else {
            diagram.printError("Лишний текст в конце программы");
        }
    }
}
