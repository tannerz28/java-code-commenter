public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}

public class Animal {
    private int legs;

    public Animal() {
        this.legs = 4;
    }

    private getLegs () {
        return legs;
    }

    public void move() {
        System.out.println("The animal moved.");
    }

}

public class Dog extends Animal {
    public Dog() {
        super();
    }
    public void move() {
        System.out.println("The dog walked.");
    }
}