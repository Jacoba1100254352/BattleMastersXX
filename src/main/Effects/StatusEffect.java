package Effects;

/**
 * Base class for all status effects.
 */
public abstract class StatusEffect {
    protected final String name;
    protected int duration;
    protected final int strength;
    protected final boolean beneficial;
    
    public StatusEffect(String name, int duration, int strength, boolean beneficial) {
        this.name = name;
        this.duration = duration;
        this.strength = strength;
        this.beneficial = beneficial;
    }
    
    public abstract void apply(Object target);
    
    public void decrementDuration() {
        duration--;
    }
    
    public boolean isExpired() {
        return duration <= 0;
    }
    
    // Getters
    public String getName() { return name; }
    public int getDuration() { return duration; }
    public int getStrength() { return strength; }
    public boolean isBeneficial() { return beneficial; }
}
