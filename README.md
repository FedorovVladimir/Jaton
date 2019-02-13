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
        double a = a1;
        while(i) {
            s = s + a;
            print(s + " ");
            a = a + d;
            println(a);
            i = i - 1;
        }

        println("Сумма первых " + k + " членов арифметицеской прогрессии(d = " + d + ") равна " + s);
    }
}
```
#### Вывод:
```
Сумма первых k членов арфметической прогрессии
Введите а1: 1
Введите d: 2
Введите k: 10
1.0 3.0
4.0 5.0
9.0 7.0
16.0 9.0
25.0 11.0
36.0 13.0
49.0 15.0
64.0 17.0
81.0 19.0
100.0 21.0
Сумма первых 10 членов арифметицеской прогрессии(d = 2) равна 100.0
```
7. Поиск k-ого число Фибоначи на Jaton
#### Код:
```
class Main {

    double n = 5;
    double numbers[] = new double[1000];

    void input() {
        print("Какое число фибоначи найти? Введите номер (для окончания введите 0): ");
        scan(n);
    }

    public static void main() {
        while (n) {
            input();
            while (n > 0) {
                findNumbers();
                display();
                displayResult();
                input();
            }
        }
    }

    void findNumbers() {
        double i = 2;
        double k = n;
        numbers[0] = 1;
        double f = n;
        while(f > 1) {
            numbers[1] = 1;
            f = 0;
        }
        while (k > 2) {
            numbers[i] = numbers[i - 1] + numbers[i - 2];
            k = k - 1;
            i = i + 1;
        }
    }

    void display() {
        double k = n;
        double i = 0;
        while (k > 0) {
            print(numbers[i] + " | ");
            k = k - 1;
            i = i + 1;
        }
        println("");
    }

    void displayResult() {
        println("Число Фибоначи №" + n + " = " + numbers[n - 1]);
    }
}
```
#### Вывод:
```
Какое число фибоначи найти? Введите номер (для окончания введите 0): 10
1.0 | 1.0 | 2.0 | 3.0 | 5.0 | 8.0 | 13.0 | 21.0 | 34.0 | 55.0 | 
Число Фибоначи №10 = 55.0
Какое число фибоначи найти? Введите номер (для окончания введите 0): 6
1.0 | 1.0 | 2.0 | 3.0 | 5.0 | 8.0 | 
Число Фибоначи №6 = 8.0
Какое число фибоначи найти? Введите номер (для окончания введите 0): 3
1.0 | 1.0 | 2.0 | 
Число Фибоначи №3 = 2.0
Какое число фибоначи найти? Введите номер (для окончания введите 0): 2
1.0 | 1.0 | 
Число Фибоначи №2 = 1.0
Какое число фибоначи найти? Введите номер (для окончания введите 0): 1
1.0 | 
Число Фибоначи №1 = 1.0
Какое число фибоначи найти? Введите номер (для окончания введите 0): -1
Какое число фибоначи найти? Введите номер (для окончания введите 0): -2
Какое число фибоначи найти? Введите номер (для окончания введите 0): -122
Какое число фибоначи найти? Введите номер (для окончания введите 0): 0
```
7. Решение квадратного уравнения на Jaton
#### Код:
```
class Main {
    double a = 1;
    double b;
    double c;
    double D;
    double sqrt;

    void input() {
        print("Введите коэфициент a: ");
        scan(a);
        print("Введите коэфициент b: ");
        scan(b);
        print("Введите коэфициент c: ");
        scan(c);
    }

    void display1() {
        print("Ищем " + a + "*x^2");
        double pb = b;
        while (pb >= 0) {
            print("+");
            pb = -1;
        }
        print(b + "*x");

        double pc = c;
        while (pc >= 0) {
            print("+");
            pc = -1;
        }
        println(c + " = 0");
    }

    void calcDiscriminant() {
        D = b*b - 4*a*c;
    }

    void display2() {
        println("D = " + D);
    }

    void root1() {
        double d = D;
        while (d == 0) {
            println("Один корень");
            println("x=" + -b/2/a);
            d = 1;
        }
    }

    void root2() {
        double d = D;
        while (d > 0) {
            println("Два корня");
            sqrt();
            println("x1=" + (-b+sqrt) / (2*a));
            println("x2=" + (-b-sqrt) / (2*a));
            d = 0;
        }
    }

    void root0() {
        double d = D;
        while (d < 0) {
            print("Нет корней");
            d = 0;
        }
    }

    public static void main () {
        while(a) {
            input();
            while(a) {
                display1();
                calcDiscriminant();
                display2();
                root1();
                root2();
                root0();
                input();
            }
        }
    }

    void sqrt() {
        sqrt = 0;
        while(sqrt * sqrt <= D) {
            sqrt = sqrt + 1;
        }
        sqrt = sqrt - 1;

        while(sqrt * sqrt <= D) {
            sqrt = sqrt + 0.1;
        }
        sqrt = sqrt - 0.1;

        while(sqrt * sqrt <= D) {
            sqrt = sqrt + 0.01;
        }
        sqrt = sqrt - 0.01;

        println("sqrt(d)≈" + sqrt);
    }
}
```
#### Вывод:
```
Введите коэфициент a: 1
Введите коэфициент b: 2
Введите коэфициент c: 1
Ищем 1*x^2+2*x+1 = 0
D = 0.0
Один корень
x=-1.0
Введите коэфициент a: 1
Введите коэфициент b: -5
Введите коэфициент c: 6
Ищем 1*x^2-5*x+6 = 0
D = 1.0
Два корня
sqrt(d)≈1.0
x1=3.0
x2=2.0
Введите коэфициент a: 0
Введите коэфициент b: 0
Введите коэфициент c: 0
```
