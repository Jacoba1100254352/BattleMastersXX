package Effects;// ================================================================================
// STATUS EFFECT SYSTEM - Should be in Effects.StatusEffect.java
// Complete status effect system with various effects
// ================================================================================


/**
 * Base class for all status effects
 */
abstract class StatusEffect {
    protected String name;
    protected int duration;
    protected int strength;
    protected boolean beneficial;
    
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
