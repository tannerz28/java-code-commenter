// The driver class.
public class Main {
    // The program's main method.
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}

// Parent class
public class Animal {
    public void move() {
        System.out.println("The animal moved.");
    }

}

// Subclass of Animal
public class Dog extends Animal {
    public void move() {
        System.out.println("The dog walked.");
    }
}
