package edu.ricm3.game.purgatoire.stunts;

import java.util.LinkedList;

import edu.ricm3.game.purgatoire.Animation.AnimType;
import edu.ricm3.game.purgatoire.AnimationPlayer;
import edu.ricm3.game.purgatoire.Options;
import edu.ricm3.game.purgatoire.Singleton;
import edu.ricm3.game.purgatoire.Sound;
import edu.ricm3.game.purgatoire.Timer;
import edu.ricm3.game.purgatoire.entities.Entity;
import edu.ricm3.game.purgatoire.entities.Missile;
import edu.ricm3.game.purgatoire.entities.Player;
import ricm3.interpreter.IAutomaton;
import ricm3.interpreter.IDirection;

public class HellPlayerStunt extends Stunt implements PlayerStunt {

	private LinkedList<Missile> m_missiles;
	private Timer m_missileTimer;
	private Timer m_karmaTimer;

	private int m_lastPopPeriod = -1;
	private int m_nbPeriod;
	private int m_DMGBuffRatio = Options.BUFF_DMG;
	private int m_weaknessBuffRatio = Options.BUFF_WEAKNESS;
	private int m_durationBuff = Options.BUFF_DURATION;

	private IAutomaton m_automatonMove;

	public HellPlayerStunt() {
		super(Singleton.getNewPlayerHellAut(), new AnimationPlayer(Singleton.getPlayerHellAnim(), AnimType.IDLE, 2),
				Options.PLAYER_HP_MAX_TOTAL_HELL[0], Options.PLAYER_DMG_HELL[0]);

		m_missiles = new LinkedList<Missile>();
		m_missileTimer = new Timer(Options.HIT_TIMER_HELL[0]);
		m_wizzTimer = new Timer(Options.HELL_PLAYER_WIZZ_TIMER);
		m_popTimer = new Timer(m_durationBuff);
		m_karmaTimer = new Timer(Options.PLAYER_KARMA_TIME_DURATION);
		m_karmaTimer.start();
		if (m_automaton.toString().equals("PlayerAction")) {
			m_automatonMove = Singleton.getNewPlayerHellMoveAut();
		}
	}

	@Override
	public void pop(IDirection d) {
		// TODO Peut-être un peu lourd comme calcul ? A voir si on peut pas juste avoir
		// un compteur de période écoulée ?
		m_nbPeriod = (int) m_entity.m_level.m_model.getTotalTime() / Options.TOTAL_PERIOD;
		if (m_nbPeriod != m_lastPopPeriod) {
			m_popTimer.start();
			buff(m_DMGBuffRatio, m_weaknessBuffRatio);
			m_lastPopPeriod = m_nbPeriod;
		}
		if (Options.ECHO_POP_PLAYER)
			System.out.println("Player pop hell");
	}

	@Override
	public void wizz(IDirection d) {
		if (m_wizzTimer.isFinished()) {
			m_wizzTimer.start();
			Missile missile;
			// North Line
			for (int x = 0; x <= 3; x++) {
				missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
						m_entity.m_bounds.x + x, m_entity.m_bounds.y - 1, Options.MISSILE_SIZE, IDirection.NORTH,
						m_entity);
				m_missiles.add(missile);
			}
			// South Line
			for (int x = -1; x <= 2; x++) {
				missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
						m_entity.m_bounds.x + x, m_entity.m_bounds.y + 3, Options.MISSILE_SIZE, IDirection.SOUTH,
						m_entity);
				m_missiles.add(missile);
			}
			// East Line
			for (int y = 0; y <= 3; y++) {
				missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
						m_entity.m_bounds.x + 3, m_entity.m_bounds.y + y, Options.MISSILE_SIZE, IDirection.EAST,
						m_entity);
				m_missiles.add(missile);
			}
			// West Line
			for (int y = -1; y <= 2; y++) {
				missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
						m_entity.m_bounds.x - 1, m_entity.m_bounds.y + y, Options.MISSILE_SIZE, IDirection.WEST,
						m_entity);
				m_missiles.add(missile);
			}
			if (Options.ECHO_CIRCLE_ATTACK)
				System.out.println("Player circle attack");
		}
		if (Options.ECHO_WIZZ_PLAYER)
			System.out.println("Player wizz hell");
	}

	@Override
	public void hit(IDirection d) {
		super.hit(d);

		if (m_missileTimer.isFinished()) {
			m_missileTimer.start();
			Missile missile;
			switch (d) {
			case NORTH:
				missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
						m_entity.m_bounds.x + 1, m_entity.m_bounds.y - 1, Options.MISSILE_SIZE, d, m_entity);
				m_missiles.add(missile);
				if (m_animation != null)
					m_animation.changeTo(AnimType.NORTH);
				break;
			case SOUTH:
				missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
						m_entity.m_bounds.x + 1, m_entity.m_bounds.y + m_entity.m_bounds.height, Options.MISSILE_SIZE,
						d, m_entity);
				m_missiles.add(missile);
				if (m_animation != null)
					m_animation.changeTo(AnimType.SOUTH);
				break;
			case EAST:
				missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
						m_entity.m_bounds.x + m_entity.m_bounds.width, m_entity.m_bounds.y + 1, Options.MISSILE_SIZE, d,
						m_entity);
				m_missiles.add(missile);
				if (m_animation != null)
					m_animation.changeTo(AnimType.EAST);
				break;
			case WEST:
				missile = new Missile(m_entity.m_level, new HeavenMissileStunt(), new HellMissileStunt(),
						m_entity.m_bounds.x - 1, m_entity.m_bounds.y + 1, Options.MISSILE_SIZE, d, m_entity);
				m_missiles.add(missile);
				if (m_animation != null)
					m_animation.changeTo(AnimType.WEST);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void egg() {
		System.out.println("egg hell");
	}

	@Override
	public void takeDamage(int DMG) {
		if (!Options.INVULNERABILITY) {
			(new Sound("sprites/hurt.wav")).start();
			((Player) m_entity).addMaxHP(-m_entity.getMaxHP() / Options.HELL_DIVIDAND_HP_MAX_TOLOSE);
			m_entity.addHP(-(int) (m_weaknessBuff * DMG));
		}
	}

	@Override
	public void takeDamage(Entity e) {
		if (!Options.INVULNERABILITY) {
			(new Sound("sprites/hurt.wav")).start();
			((Player) m_entity).addMaxHP(-getDMGTaken(e) * Options.HELL_DIVIDAND_HP_MAX_TOLOSE / 100);
			m_entity.addHP(-getDMGTaken(e));
		}
	}

	@Override
	public void step(long now) {
		m_entity.m_direction = IDirection.NONE;
		if (m_automatonMove != null) {
			m_automatonMove.step(m_entity);
		}
		super.step(now);
		if (m_popTimer.isFinished()) {
			setDMGBuff(1);
			m_weaknessBuff = 1;
			Singleton.getController().updateBuffsUI();
		}
		m_missileTimer.step(now);
		m_karmaTimer.step(now);
		changeKarmaOverTime();
	}

	@Override
	void goingOut(IDirection d) {
		if (d == IDirection.NORTH) {
			m_entity.m_level.m_model.nextLevel();
		}
	}

	public String getRankName() {
		return Options.PLAYER_RANKS_HELL[((Player) m_entity).getRank()];
	}

	@Override
	public void changeKarmaOverTime() {
		if (m_karmaTimer.isFinished()) {
			((Player) m_entity).addKarma(+Options.PLAYER_KARMA_TIME_AMOUNT);
			m_karmaTimer.start();
		}
	}

	@Override
	public void updateRankStats() {
		((Player) m_entity).setMaxTotalHP(Options.PLAYER_HP_MAX_TOTAL_HELL[((Player) m_entity).getRank()]);
		setDMG(Options.PLAYER_DMG_HELL[((Player) m_entity).getRank()]);
		m_missileTimer.setDuration(Options.HIT_TIMER_HELL[((Player) m_entity).getRank()]);
		if (Options.ECHO_HIT_TIMER_CHANGE)
			System.out.println("Missile timer: " + m_missileTimer.getDuration());
	}

	@Override
	protected void move(int x, int y) {
		super.move(x, y);
		m_entity.m_level.m_model.m_totalDistance -= y;
	}

}
