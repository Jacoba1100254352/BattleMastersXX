// ============== Enemy.java ==============

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Enemy {
    private String name;
    private int hp;
    private int attackPower;
    private int expReward;
    private int goldReward;
    private boolean isBoss;
    private Map<String, StatusEffect> statusEffects;
    private List<String> resist;
    private List<String> weak;
    
    public Enemy(String name, int hp, int attack, int expReward, int goldReward, boolean isBoss) {
        this.name = name;
        this.hp = hp;
        this.attackPower = attack;
        this.expReward = expReward;
        this.goldReward = goldReward;
        this.isBoss = isBoss;
        this.statusEffects = new HashMap<>();
        this.resist = new ArrayList<>();
        this.weak = new ArrayList<>();
    }
    
    public Enemy(String name, int hp, int attack, int expReward, int goldReward, 
                boolean isBoss, List<String> resist, List<String> weak) {
        this(name, hp, attack, expReward, goldReward, isBoss);
        if (resist != null) this.resist = new ArrayList<>(resist);
        if (weak != null) this.weak = new ArrayList<>(weak);
    }
    
    public int attack() {
        return attackPower;
    }
    
    public void takeDamage(int damage) {
        this.hp -= damage;
    }
    
    public boolean isAlive() {
        return hp > 0;
    }
    
    public void addStatusEffect(String effect, StatusEffect statusEffect) {
        statusEffects.put(effect, statusEffect);
    }
    
    // Getters and setters
    public String getName() { return name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getAttackPower() { return attackPower; }
    public int getExpReward() { return expReward; }
    public int getGoldReward() { return goldReward; }
    public boolean isBoss() { return isBoss; }
    public Map<String, StatusEffect> getStatusEffects() { return statusEffects; }
    public List<String> getResist() { return resist; }
    public List<String> getWeak() { return weak; }
}