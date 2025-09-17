// ============== Battle.java ==============

import java.util.*;

public class Battle{
    private Scanner scanner;
    
    public Battle(Scanner scanner) {
        this.scanner = scanner;
    }
    
    public String battle(Player player, Enemy enemy) {
        System.out.println("A wild " + enemy.getName() + " appears!");
        
        while (player.isAlive() && enemy.isAlive()) {
            System.out.println("\n" + player.getName() + " HP: " + (int)player.getHp() + 
                             "/" + (int)player.getMaxHp() + " | Mana: " + (int)player.getMana() + 
                             "/" + (int)player.getMaxMana());
            System.out.println(enemy.getName() + " HP: " + enemy.getHp());
            
            if (player.getStatusEffects().containsKey("freeze")) {
                System.out.println("You are frozen and skip this turn!");
                applyStatusEffects(player);
                continue;
            }
            
            System.out.println("1. Attack\n2. Use Magic\n3. Use Item\n4. Run");
            System.out.print("Your action: ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    int dmg = player.getAttackValue();
                    enemy.takeDamage(dmg);
                    System.out.println("You hit " + enemy.getName() + " for " + dmg + " damage!");
                    break;
                    
                case "2":
                    castSpell(player, enemy);
                    break;
                    
                case "3":
                    useItem(player);
                    break;
                    
                case "4":
                    System.out.println("You fled!");
                    return "fled";
                    
                default:
                    System.out.println("Invalid action.");
                    continue;
            }
            
            applyStatusEffects(player);
            applyStatusEffects(enemy);
            
            // Enemy attack
            if (enemy.getStatusEffects().containsKey("freeze")) {
                System.out.println(enemy.getName() + " is frozen and can't move this turn!");
            } else if (enemy.isAlive()) {
                player.defend(enemy.attack());
            }
            
            // Companion actions
            for (Companion companion : player.getCompanions()) {
                if (enemy.isAlive()) {
                    companion.act(player, enemy);
                }
            }
        }
        
        if (!enemy.isAlive()) {
            System.out.println("\nYou defeated " + enemy.getName() + "!");
            player.completeQuest(enemy.getName());
            player.gainExp(enemy.getExpReward());
            
            // Calculate gold reward with faction bonus
            int goldReward = enemy.getGoldReward();
            if ("Shadow Brotherhood".equals(player.getFaction())) {
                int bonusGold = 500;
                goldReward += bonusGold;
                System.out.println("Shadow Brotherhood bonus: +" + bonusGold + " gold!");
            }
            
            player.setGold(player.getGold() + goldReward);
            System.out.println("You earned " + enemy.getExpReward() + " EXP and " + goldReward + " gold.");
            
            if (enemy.isBoss()) {
                System.out.println("*** Boss " + enemy.getName() + " vanquished! You are victorious! ***");
                Item bonusItem = GameData.ARMOR.get(new Random().nextInt(GameData.ARMOR.size()));
                player.getInventory().add(new Item(bonusItem));
                System.out.println("Boss dropped: " + bonusItem.getName() + "!");
            }
            
            return "victory";
        } else if (!player.isAlive()) {
            System.out.println("\n*** You have been defeated. Game Over. ***");
            System.exit(0);
        }
        
        return "defeat";
    }
    
    private void castSpell(Player player, Enemy enemy) {
        System.out.print("Cast (fireball/ice shard/heal/mega heal): ");
        String spell = scanner.nextLine().toLowerCase();
        
        Map<String, Object> spellData = GameData.SPELLS.get(spell);
        if (spellData == null) {
            System.out.println("Unknown spell.");
            return;
        }
        
        int cost = (Integer) spellData.get("cost");
        if (player.getMana() < cost) {
            System.out.println("Not enough mana.");
            return;
        }
        
        player.setMana(player.getMana() - cost);
        
        if (spellData.containsKey("damage")) {
            int baseDamage = (Integer) spellData.get("damage");
            String element = (String) spellData.get("element");
            
            // Check enemy resistances/weaknesses
            if (element != null) {
                if (enemy.getResist().contains(element)) {
                    baseDamage = (int)(baseDamage * 0.5);
                    System.out.println(enemy.getName() + " resists " + element + " magic! Damage halved.");
                } else if (enemy.getWeak().contains(element)) {
                    baseDamage = (int)(baseDamage * 1.5);
                    System.out.println(enemy.getName() + " is weak to " + element + " magic! Damage increased.");
                }
            }
            
            enemy.takeDamage(baseDamage);
            System.out.println(spell + " hits " + enemy.getName() + " for " + baseDamage + " damage!");
        } else if (spellData.containsKey("heal")) {
            int healAmount = (Integer) spellData.get("heal");
            player.setHp(player.getHp() + healAmount);
            System.out.println("Healed " + healAmount + " HP.");
        }
    }
    
    private void useItem(Player player) {
        List<Item> usableItems = new ArrayList<>();
        for (Item item : player.getInventory()) {
            if (item.isUsable()) {
                usableItems.add(item);
            }
        }
        
        if (usableItems.isEmpty()) {
            System.out.println("No usable items in your inventory.");
            return;
        }
        
        System.out.println("Usable items:");
        for (int i = 0; i < usableItems.size(); i++) {
            System.out.println((i + 1) + ". " + usableItems.get(i).getName());
        }
        
        try {
            System.out.print("Use which item?: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < usableItems.size()) {
                Item item = usableItems.get(idx);
                
                // Check for bleeding first
                if (player.getStatusEffects().containsKey("bleed") && "heal".equals(item.getEffect())) {
                    System.out.println("You are bleeding and cannot use healing items!");
                    return;
                }
                
                if ("heal".equals(item.getEffect())) {
                    int amount = item.getValue();
                    if (player.getStatusEffects().containsKey("poison")) {
                        amount = amount / 2;
                        System.out.println("Poison reduces your healing!");
                    }
                    player.setHp(player.getHp() + amount);
                    System.out.println("You healed " + amount + " HP.");
                } else if ("mana".equals(item.getEffect())) {
                    player.setMana(player.getMana() + item.getValue());
                    System.out.println("You restored " + item.getValue() + " mana.");
                } else if ("full_heal".equals(item.getEffect())) {
                    player.setHp(player.getMaxHp());
                    player.setMana(player.getMaxMana());
                    System.out.println("You fully restored your HP and mana!");
                }
                
                player.getInventory().remove(item);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private void applyStatusEffects(Object entity) {
        Map<String, StatusEffect> effects;
        String name;
        
        if (entity instanceof Player) {
            Player player = (Player) entity;
            effects = player.getStatusEffects();
            name = player.getName();
        } else if (entity instanceof Enemy) {
            Enemy enemy = (Enemy) entity;
            effects = enemy.getStatusEffects();
            name = enemy.getName();
        } else {
            return;
        }
        
        List<String> toRemove = new ArrayList<>();
        
        for (Map.Entry<String, StatusEffect> entry : effects.entrySet()) {
            String effectName = entry.getKey();
            StatusEffect effect = entry.getValue();
            
            if ("burn".equals(effectName)) {
                int dmg = effect.getDamage();
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    player.setHp(player.getHp() - dmg);
                } else if (entity instanceof Enemy) {
                    Enemy enemy = (Enemy) entity;
                    enemy.takeDamage(dmg);
                }
                System.out.println(name + " takes " + dmg + " burn damage!");
            }
            
            effect.decreaseTurns();
            if (effect.getTurns() <= 0) {
                toRemove.add(effectName);
            }
        }
        
        for (String effectName : toRemove) {
            effects.remove(effectName);
        }
    }
}