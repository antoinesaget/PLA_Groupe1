package edu.ricm3.game.purgatoire.stunts;

import edu.ricm3.game.purgatoire.Animation.AnimType;
import edu.ricm3.game.purgatoire.AnimationPlayer;
import edu.ricm3.game.purgatoire.Options;
import edu.ricm3.game.purgatoire.Singleton;
import edu.ricm3.game.purgatoire.entities.Player;
import ricm3.interpreter.IDirection;
import ricm3.interpreter.IEntityType;

public class HellSpecialStunt extends Stunt {

//	HellSpecialStunt(Entity entity) {
//		super(Singleton.getNewSpecialHellAut(), entity, Color.CYAN);
//		m_maxHP = Options.HELL_SPCL_HP_MAX;
//		setDMG(Options.HELL_SPCL_DMG);
//		m_karmaToGive = Options.HELL_SPCL_KARMA_TOGIVE;
//	}

	public HellSpecialStunt() {
		super(Singleton.getNewSpecialHellAut(), new AnimationPlayer(Singleton.getSpecialHellAnim(), AnimType.IDLE, 2));
		m_karmaToGive = Options.HELL_SPCL_KARMA_TOGIVE;
	}

	@Override
	public void pop(IDirection d) {
		Player player = (Player) m_entity.superposedWith(IEntityType.PLAYER);
		if (player != null) {
			System.out.println("sur flaque");
			player.addKarma(m_entity);
			player.addHP(Options.HELL_SPCL_HP_TOGIVE);
		}
		System.out.println("pop flaque");
	}

	@Override
	public void wizz(IDirection d) {
		System.out.println("wizz flaque");
	}

	@Override
	public void takeDamage(int DMG) {
		System.out.println("takeDMG flaque");
	}

	@Override
	public boolean isEntityAt(IEntityType type, IDirection direction) {
		return m_entity.superposedWith(type) != null;
	}
}