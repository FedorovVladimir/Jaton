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
4. Консольный ввод и вывод в Jaton осуществляются через команды scan, print и println
#### Код:
```
class Main {
    public static void main() {
        double a;
        print("Введите число: ");
        scan(a);
        println("Вы ввели: " + a);
    }
}
```
#### Вывод:
```
Введите число: 1
Вы ввели: 1
```
5. Jaton поддерживает только один вид циклов: while
#### Код:
```
class Main {
    public static void main() {
        double i;
        double j;
        double k;

        i = 2;
        while(i) {
            j = 2;
            while(j) {
                k = 2;
                while(k) {
                    println(i + " " + j + " " + k);
                    k = k - 1;
                }
                j = j - 1;
            }
            i = i - 1;
        }
    }
}
```
#### Вывод:
```
2 2 2
2 2 1.0
2 2 0.0
2 1.0 2
2 1.0 1.0
2 1.0 0.0
2 0.0 2
2 0.0 1.0
2 0.0 0.0
1.0 2 2
1.0 2 1.0
1.0 2 0.0
1.0 1.0 2
1.0 1.0 1.0
1.0 1.0 0.0
1.0 0.0 2
1.0 0.0 1.0
1.0 0.0 0.0
```
6. Сумма первых k членов арфметической прогрессии на Jaton
#### Код:
```
class Main {
    public static void main() {
        println("Сумма первых k членов арфметической прогрессии");

        double a1;
        print("Введите а1: ");
        scan(a1);

        double d;
        print("Введите d: ");
        scan(d);

        double k;
        print("Введите k: ");
        scan(k);

        double s;
        double i = k;
        while(i) {
            s = s + i;
            i = i - d;
        }

        println("Сумма первых " + k + " членов арифметицеской прогрессии(d = " + d + ") равна " + s);
    }
}

```
#### Вывод:
```
Сумма первых k членов арфметической прогрессии
Введите а1: 0
Введите d: 2
Введите k: 4
Сумма первых 4 членов арифметицеской прогрессии(d = 2) равна 6.0
```
