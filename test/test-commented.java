// The driver class
public class Main {
    // The program's main method
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}

// Parent class
public class Animal {
    // Constructor of the Animal class, creates an instance of the class
    public Animal() {

    }
    public void move() {
        System.out.println("The animal moved.");
    }

}

// Subclass of Animal
public class Dog extends Animal {
    // Constructor of the Dog class, creates an instance of the class
    public Dog() {
        // Calls the constructor of the parent Animal class
        super();
    }
    public void move() {
        System.out.println("The dog walked.");
    }
}
