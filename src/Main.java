import diagram.Diagram;
import io.Reader;
import parser.Scanner;
import parser.Token;
import parser.TokenType;

public class Main {
    public static void main(String[] args) {
        String text = null;
        if (args.length < 1) {
            System.out.println("Ожидался файл");
            System.exit(1);
        } else {
            try {
                text = Reader.getDate(args[0]);
            } catch (Exception e) {
                System.out.println("Неверны тип файла");
                System.exit(1);
            }
        }

        Scanner scanner = new Scanner(text);
        Diagram diagram = new Diagram(scanner);

        diagram.setFlag(false);
        diagram.program();
        diagram.setFlag(true);
        diagram.program();

        Token token = scanner.nextScanner();
        if (token.getType() != TokenType.EOF)
            System.out.println("--Error ожидался конец файла--");
    }
}
