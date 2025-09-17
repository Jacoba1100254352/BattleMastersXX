 // ============== StatusEffect.java ==============

public class StatusEffect {
    private int damage;
    private int turns;
    
    public StatusEffect(int damage, int turns) {
        this.damage = damage;
        this.turns = turns;
    }
    
    public StatusEffect(int turns) {
        this.turns = turns;
        this.damage = 0;
    }
    
    // Copy constructor
    public StatusEffect(StatusEffect other) {
        this.damage = other.damage;
        this.turns = other.turns;
    }
    
    public void decreaseTurns() { this.turns--; }
    
    // Getters and setters
    public int getDamage() { return damage; }
    public int getTurns() { return turns; }
    public void setTurns(int turns) { this.turns = turns; }
}