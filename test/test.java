public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}

public class Animal {
    public void move() {
        System.out.println("The animal moved.");
    }

}

public class Dog extends Animal {
    public void move() {
        System.out.println("The dog walked.");
    }
}