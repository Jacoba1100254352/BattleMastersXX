// ============== Horse.java ==============

public class Horse {
    private String name;
    private double speed;
    private int price;
    
    public Horse(String name, double speed, int price) {
        this.name = name;
        this.speed = speed;
        this.price = price;
    }
    
    // Copy constructor
    public Horse(Horse other) {
        this.name = other.name;
        this.speed = other.speed;
        this.price = other.price;
    }
    
    // Getters
    public String getName() { return name; }
    public double getSpeed() { return speed; }
    public int getPrice() { return price; }
}