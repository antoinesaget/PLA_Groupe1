package edu.ricm3.game.purgatoire.stunts;

import java.util.LinkedList;

import edu.ricm3.game.purgatoire.Animation.AnimType;
import edu.ricm3.game.purgatoire.AnimationPlayer;
import edu.ricm3.game.purgatoire.Options;
import edu.ricm3.game.purgatoire.Singleton;
import edu.ricm3.game.purgatoire.Timer;
import edu.ricm3.game.purgatoire.entities.Missile;
import edu.ricm3.game.purgatoire.entities.Player;
import edu.ricm3.game.purgatoire.entities.Special;
import ricm3.interpreter.IDirection;
import ricm3.interpreter.IEntityType;

public class HeavenPlayerStunt extends Stunt implements PlayerStunt {

	LinkedList<Missile> m_missiles;
	Timer m_hitTimer;
	Timer m_hitCoolDown;
	boolean m_isFiring;
	Timer m_karmaTimer;

	public HeavenPlayerStunt() {
		super(Singleton.getNewPlayerHeavenAut(), new AnimationPlayer(Singleton.getPlayerHeavenAnim(), AnimType.IDLE, 2),
				Options.HEAVEN_PLAYER_HP_MAX, Options.HEAVEN_PLAYER_DMG);

		m_popCooldown = Options.DASH_CD;
		m_popTimer = new Timer(m_popCooldown);
		m_missiles = new LinkedList<Missile>();
		m_hitTimer = new Timer(Options.HEAVEN_HIT_TIMER);
		m_hitCoolDown = new Timer(Options.HEAVEN_HIT_CD);
		m_isFiring = false;

		m_karmaTimer = new Timer(Options.PLAYER_KARMA_TIME_DURATION);
		m_karmaTimer.start();
	}

	@Override
	public void pop(IDirection d) {
		if (m_popTimer.isFinished()) {
			dash(m_entity.m_direction);
			System.out.println("dash");
			m_popTimer.start();
		}
		System.out.println("pop heaven player");
	}

	@Override
	public void wizz(IDirection d) {
		Special special = (Special) m_entity.superposedWith(IEntityType.TEAM);
		if (special != null) {
			special.pop(null);
		}
		System.out.println("wizz heaven player");
	}

	@Override
	public void hit(IDirection d) {
		if (m_hitCoolDown.isFinished()) {
			if (m_isFiring == false) {
				m_hitTimer.start();
				m_isFiring = true;
			}
			if (!m_hitTimer.isFinished()) {
				Missile missile;
				switch (d) {
				case NORTH:
					for (int x = 0; x <= 2; x++) {
						missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
								m_entity.m_bounds.x + x, m_entity.m_bounds.y - 1, 1, 1, IDirection.NORTH, m_entity);
						m_missiles.add(missile);
					}
					break;
				case SOUTH:
					for (int x = 0; x <= 2; x++) {
						missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
								m_entity.m_bounds.x + x, m_entity.m_bounds.y + 3, 1, 1, IDirection.SOUTH, m_entity);
						m_missiles.add(missile);
					}
					break;
				case EAST:
					for (int y = 0; y <= 2; y++) {
						missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
								m_entity.m_bounds.x + 3, m_entity.m_bounds.y + y, 1, 1, IDirection.EAST, m_entity);
						m_missiles.add(missile);
					}
					break;
				case WEST:
					for (int y = 0; y <= 2; y++) {
						missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
								m_entity.m_bounds.x - 1, m_entity.m_bounds.y + y, 1, 1, IDirection.WEST, m_entity);
						m_missiles.add(missile);
					}
					break;
				}
			} else {
				m_hitCoolDown.start();
				m_isFiring = false;
			}
		}
	}

	@Override
	public void egg() {
		System.out.println("egg heaven player");
	}

	@Override
	void goingOut(IDirection d) {
		if (d == IDirection.NORTH) {
			m_entity.m_level.m_model.nextLevel();
		}
	}

	public String getRankName() {
		return Options.PLAYER_RANKS_HEAVEN[((Player) m_entity).getRank()];
	}

	@Override
	public void step(long now) {
		super.step(now);
		m_popTimer.step(now);
		m_hitTimer.step(now);
		m_hitCoolDown.step(now);
		m_karmaTimer.step(now);
		changeKarmaOverTime();
	}

	@Override
	public void changeKarmaOverTime() {
		if (m_karmaTimer.isFinished()) {
			((Player) m_entity).addKarma(-Options.PLAYER_KARMA_TIME_AMOUNT);
			m_karmaTimer.start();
		}
	}

}
