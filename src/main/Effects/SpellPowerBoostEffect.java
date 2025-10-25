package Effects;

/**
 * Spell power boost effect.
 */
public class SpellPowerBoostEffect extends StatusEffect {
	public SpellPowerBoostEffect(int duration, int powerBoost) {
		super("Spell Power", duration, powerBoost, true);
	}
	
	@Override
	public void apply(Object target) {
		// This effect is passive and checked during spell casting
		System.out.println("Magical energy courses through you! (+" + strength + "% spell power)");
	}
}
