# Jaton
![Image of Yaktocat](https://raw.githubusercontent.com/FedorovVladimir/Jaton/master/diagrams/Jaton.png)

1. Пример программы на языке Jaton:
```
class Main {
    # первая программа
    public static void main() {
        print("Hello, world!");
    }
}
```
2. В Jaton 4 типа констант: строковые, символьные, целочисленные, с плавающей точкой.
#### Код:
```
class Main {
    public static void main() {
        print("Hello, world!");
        print('a');
        print(10);
        print(3.1415);
    }
}
```
#### Вывод:
```
Hello, world!
a
10
3.1415
```
3. Переменные в Jaton могут быть 2 типов: char и double
#### Код:
```
class Main {
    public static void main() {
        char ch = 'j';
        double d = 2.7;
        print(ch);
        print(d);
    }
}
```
#### Вывод:
```
j
2.7
```
