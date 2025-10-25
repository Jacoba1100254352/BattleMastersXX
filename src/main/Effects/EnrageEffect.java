package Effects;


/**
 * Enrage effect for increased damage
 */
class EnrageEffect extends StatusEffect
{
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
