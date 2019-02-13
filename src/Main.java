import diagram.Diagram;
import io.Reader;
import parser.Scanner;
import parser.Token;
import parser.TokenType;

public class Main {
    public static void main(String[] args) {
        String text;
        try {
            text = Reader.getDate("discriminant.jaton");
        } catch (Exception e) {
            System.out.println("Неверны тип файла");
            return;
        }

        Scanner scanner = new Scanner(text);
        Diagram diagram = new Diagram(scanner);

        diagram.setFlag(false);
        diagram.program();
        diagram.setFlag(true);
        diagram.program();


        Token token = scanner.nextScanner();
        if (token.getType() != TokenType.EOF)
            System.out.println("--Error--");
    }
}
