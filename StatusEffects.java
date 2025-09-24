// ================================================================================
// STATUS EFFECT SYSTEM - Should be in StatusEffect.java
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

/**
 * Regeneration effect for healing over time
 */
class RegenerationEffect extends StatusEffect {
    public RegenerationEffect(int duration, int healPerTurn) {
        super("Regeneration", duration, healPerTurn, true);
    }
    
    @Override
    public void apply(Object target) {
        if (target instanceof Player player) {
            player.heal(strength);
            System.out.println("Regeneration heals you for " + strength + " HP.");
        } else if (target instanceof Enemy enemy) {
            enemy.heal(strength);
        }
    }
}

/**
 * Poison effect for damage over time
 */
class PoisonEffect extends StatusEffect {
    public PoisonEffect(int duration, int damagePerTurn) {
        super("Poison", duration, damagePerTurn, false);
    }
    
    @Override
    public void apply(Object target) {
        if (target instanceof Player player) {
            player.takeDamage(strength, "poison");
            System.out.println("Poison deals " + strength + " damage!");
        } else if (target instanceof Enemy enemy) {
            enemy.takeDamage(strength, "poison");
        }
    }
}

/**
 * Spell power boost effect
 */
class SpellPowerBoostEffect extends StatusEffect {
    public SpellPowerBoostEffect(int duration, int powerBoost) {
        super("Spell Power", duration, powerBoost, true);
    }
    
    @Override
    public void apply(Object target) {
        // This effect is passive and checked during spell casting
        System.out.println("Magical energy courses through you! (+" + strength + "% spell power)");
    }
}

/**
 * Enrage effect for increased damage
 */
class EnrageEffect extends StatusEffect {
    public EnrageEffect(int duration) {
        super("Enraged", duration, 30, true); // 30% damage increase
    }
    
    @Override
    public void apply(Object target) {
        // Passive effect applied during damage calculation
        if (duration == 1) {
            System.out.println("The rage subsides...");
        }
    }
}