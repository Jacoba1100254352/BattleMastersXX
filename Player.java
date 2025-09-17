// ============== Player.java ==============

import java.util.*;

public class Player {
    private String name;
    private double hp = 100.0;
    private double maxHp = 100.0;
    private double mana = 50.0;
    private double maxMana = 50.0;
    private int gold = 0;
    private int exp = 0;
    private int level = 1;
    private int prestige = 0;
    private String location = "Town Square";
    private String currentDomain = "Central Domain";
    private List<Item> inventory;
    private Item weapon;
    private Item armor;
    private List<Companion> companions;
    private int maxCompanions = 3;
    private Horse horse;
    private boolean mythicMode = false;
    private String faction;
    private Map<String, StatusEffect> statusEffects;
    private Quest currentQuest;
    private List<Quest> npcQuests;
    
    public Player(String name) {
        this.name = name;
        this.inventory = new ArrayList<>();
        this.companions = new ArrayList<>();
        this.statusEffects = new HashMap<>();
        this.npcQuests = new ArrayList<>();
    }
    
    public int getAttackValue() {
        int base = 10 + level * 2;
        int weaponPower = (weapon != null) ? weapon.getPower() : 0;
        int prestigeBonus = prestige * 10;
        return base + weaponPower + prestigeBonus;
    }
    
    public void defend(int dmg) {
        int defense = (armor != null) ? armor.getDefense() : 0;
        int prestigeDefense = prestige * 5;
        
        // Faction bonus
        if ("Knights of the Phoenix".equals(faction)) {
            defense += 80; // passive defense boost
        }
        
        int realDmg = Math.max(dmg - defense - prestigeDefense, 0);
        hp -= realDmg;
        System.out.println("You received " + realDmg + " damage (after armor, prestige, and faction bonuses).");
    }
    
    public void gainExp(int amount) {
        exp += amount;
        int needed = level * 100;
        if (exp >= needed) {
            level++;
            exp -= needed;
            maxHp += 20;
            hp = maxHp;
            maxMana += 10;
            mana = maxMana;
            System.out.println("*** You leveled up! You are now level " + level + "! ***");
            
            if (level >= 50) {
                System.out.println("*** PRESTIGE AVAILABLE! You can now prestige at level 50! ***");
                System.out.println("Visit the Prestige Hall to prestige and gain exclusive rewards!");
            }
        }
    }
    
    public boolean canPrestige() {
        return level >= 50;
    }
    
    public boolean doPrestige() {
        if (!canPrestige()) {
            System.out.println("You must reach level 50 to prestige!");
            return false;
        }
        
        prestige++;
        level = 1;
        exp = 0;
        hp = 100 + (prestige * 50);
        maxHp = 100 + (prestige * 50);
        mana = 50 + (prestige * 25);
        maxMana = 50 + (prestige * 25);
        
        System.out.println("*** PRESTIGE " + prestige + " ACHIEVED! ***");
        System.out.println("Level reset to 1, but you gained permanent bonuses!");
        System.out.println("New HP: " + (int)maxHp + ", New Mana: " + (int)maxMana);
        System.out.println("Attack bonus: +" + (prestige * 10) + ", Defense bonus: +" + (prestige * 5));
        
        if (prestige >= 10 && !mythicMode) {
            mythicMode = true;
            System.out.println("*** MYTHIC PRESTIGE UNLOCKED! ***");
            System.out.println("You now have access to the Mythic Gate and Mythic Challenges!");
        }
        
        return true;
    }
    
    public boolean isAlive() {
        return hp > 0;
    }
    
    public boolean addCompanion(Companion companion) {
        if (companions.size() >= maxCompanions) {
            System.out.println("You can only have " + maxCompanions + " companions maximum!");
            return false;
        }
        companions.add(companion);
        return true;
    }
    
    public void addStatusEffect(String effect, StatusEffect statusEffect) {
        statusEffects.put(effect, statusEffect);
    }
    
    // Quest methods
    public void startQuest(Enemy questEnemy) {
        if (currentQuest != null) {
            System.out.println("You already have an active quest. Complete it before taking a new one.");
            return;
        }
        
        Map<String, Object> questData = new HashMap<>();
        questData.put("name", questEnemy.getName());
        questData.put("hp", questEnemy.getHp());
        questData.put("attack", questEnemy.getAttackPower());
        questData.put("exp", questEnemy.getExpReward());
        questData.put("gold", questEnemy.getGoldReward());
        
        System.out.println("A quest has been assigned: Defeat a " + questEnemy.getName() + "!");
    }
    
    public void completeQuest(String defeatedEnemyName) {
        if (currentQuest != null) {
            System.out.println("*** Quest Completed: You defeated the " + defeatedEnemyName + "! ***");
            // Add quest completion logic here
            currentQuest = null;
        }
    }
    
    public boolean acceptNpcQuest(String npcName, Map<String, Object> questData) {
        for (Quest quest : npcQuests) {
            if (quest.getNpc().equals(npcName)) {
                System.out.println("You already have a quest from " + npcName + "!");
                return false;
            }
        }
        
        String type = (String) questData.get("type");
        String target = (String) questData.get("target");
        int quantity = questData.containsKey("quantity") ? (Integer) questData.get("quantity") : 1;
        @SuppressWarnings("unchecked")
        Map<String, Object> reward = (Map<String, Object>) questData.get("reward");
        String description = (String) questData.get("description");
        
        Quest newQuest = new Quest(npcName, type, target, quantity, reward, description);
        npcQuests.add(newQuest);
        System.out.println("Quest accepted from " + npcName + ": " + description);
        return true;
    }
    
    public void updateNpcQuestProgress(String questType, String target) {
        for (Quest quest : npcQuests) {
            if (quest.getType().equals(questType)) {
                if ("find_item".equals(questType) && target.equals(quest.getTarget())) {
                    quest.incrementProgress();
                } else if ("defeat_enemy".equals(questType) && target.equals(quest.getTarget())) {
                    quest.incrementProgress();
                } else if ("deliver_item".equals(questType) && target.equals(quest.getTarget())) {
                    quest.incrementProgress();
                }
                
                if (quest.isComplete()) {
                    completeNpcQuest(quest);
                }
            }
        }
    }
    
    public void completeNpcQuest(Quest quest) {
        System.out.println("*** NPC Quest Completed for " + quest.getNpc() + "! ***");
        
        Map<String, Object> reward = quest.getReward();
        if (reward.containsKey("gold")) {
            gold += (Integer) reward.get("gold");
            System.out.println("You received " + reward.get("gold") + " gold!");
        }
        if (reward.containsKey("exp")) {
            gainExp((Integer) reward.get("exp"));
            System.out.println("You received " + reward.get("exp") + " experience!");
        }
        if (reward.containsKey("item")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> itemData = (Map<String, Object>) reward.get("item");
            String itemName = (String) itemData.get("name");
            if (itemData.containsKey("power")) {
                inventory.add(new Item(itemName, (Integer) itemData.get("power")));
            } else {
                inventory.add(new Item(itemName, "none", 0));
            }
            System.out.println("You received " + itemName + "!");
        }
        
        npcQuests.remove(quest);
    }
    
    public void checkNpcQuestItems() {
        for (Quest quest : new ArrayList<>(npcQuests)) {
            if ("find_item".equals(quest.getType())) {
                long itemCount = inventory.stream()
                    .filter(item -> item.getName().equals(quest.getTarget()))
                    .count();
                if (itemCount >= quest.getQuantity() && quest.getProgress() < quest.getQuantity()) {
                    quest.setProgress(quest.getQuantity());
                    completeNpcQuest(quest);
                    break;
                }
            }
        }
    }
    
    // Getters and setters
    public String getName() { return name; }
    public double getHp() { return hp; }
    public void setHp(double hp) { this.hp = Math.max(0, Math.min(maxHp, hp)); }
    public double getMaxHp() { return maxHp; }
    public double getMana() { return mana; }
    public void setMana(double mana) { this.mana = Math.max(0, Math.min(maxMana, mana)); }
    public double getMaxMana() { return maxMana; }
    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }
    public int getExp() { return exp; }
    public int getLevel() { return level; }
    public int getPrestige() { return prestige; }
    public void setPrestige(int prestige) { this.prestige = prestige; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCurrentDomain() { return currentDomain; }
    public void setCurrentDomain(String currentDomain) { this.currentDomain = currentDomain; }
    public List<Item> getInventory() { return inventory; }
    public Item getWeapon() { return weapon; }
    public void setWeapon(Item weapon) { this.weapon = weapon; }
    public Item getArmor() { return armor; }
    public void setArmor(Item armor) { this.armor = armor; }
    public List<Companion> getCompanions() { return companions; }
    public int getMaxCompanions() { return maxCompanions; }
    public Horse getHorse() { return horse; }
    public void setHorse(Horse horse) { this.horse = horse; }
    public boolean isMythicMode() { return mythicMode; }
    public String getFaction() { return faction; }
    public void setFaction(String faction) { this.faction = faction; }
    public Map<String, StatusEffect> getStatusEffects() { return statusEffects; }
    public Quest getCurrentQuest() { return currentQuest; }
    public List<Quest> getNpcQuests() { return npcQuests; }
}