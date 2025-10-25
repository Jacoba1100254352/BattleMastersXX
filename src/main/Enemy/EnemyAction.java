package Enemy;

/**
 * Enemy action class for AI decisions.
 */
public class EnemyAction {
	private final String actionType;
	private final int damage;
	private final String message;
	private final String element;
	private final String statusEffect;
	
	public EnemyAction(String actionType, int damage, String message) {
		this(actionType, damage, message, "physical", null);
	}
	
	public EnemyAction(String actionType, int damage, String message, String element) {
		this(actionType, damage, message, element, null);
	}
	
	public EnemyAction(String actionType, int damage, String message, String element, String statusEffect) {
		this.actionType = actionType;
		this.damage = damage;
		this.message = message;
		this.element = element;
		this.statusEffect = statusEffect;
	}
	
	// Getters
	public String getActionType() { return actionType; }
	public int getDamage() { return damage; }
	public String getMessage() { return message; }
	public String getElement() { return element; }
	public String getStatusEffect() { return statusEffect; }
}
