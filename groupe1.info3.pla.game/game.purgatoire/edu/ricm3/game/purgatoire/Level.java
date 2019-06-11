package edu.ricm3.game.purgatoire;

import java.awt.Color;
import java.security.cert.PKIXRevocationChecker.Option;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.ricm3.game.purgatoire.entities.Entity;
import edu.ricm3.game.purgatoire.entities.Missile;
import edu.ricm3.game.purgatoire.entities.Nest;
import edu.ricm3.game.purgatoire.entities.Obstacle;
import edu.ricm3.game.purgatoire.entities.Player;
import edu.ricm3.game.purgatoire.entities.Soul;
import edu.ricm3.game.purgatoire.entities.Special;
import ricm3.interpreter.IDirection;

public class Level {
	Color m_c;

	public Model m_model;

	List<Entity> m_souls;
	List<Entity> m_obstacles;
	List<Entity> m_nest;
	List<Entity> m_missiles;

	Entity m_special;
	public Entity m_player;

	List<Entity> m_entities;
	List<Entity> m_toRemove;

	public CollisionGrid m_collisionGrid;

	private long lastUpdatePlayer;
	// public long nest_spawn_period = Options.NEST_SPAWN_DELAY;
	private long lastUpdateSouls;
	private long lastUpdateObstacles;
	private long lastUpdateNests;

	Level(Model model, Color c) {
		m_c = c;
		m_model = model;

		m_souls = new LinkedList<Entity>();
		m_obstacles = new LinkedList<Entity>();
		m_nest = new LinkedList<Entity>();
		m_entities = new LinkedList<Entity>();
		m_missiles = new LinkedList<Entity>();
		m_toRemove = new LinkedList<Entity>();

		m_collisionGrid = new CollisionGrid();
	}

	Level(Model model) {
		this(model, Color.WHITE);
	}

	public void addEntity(Entity e) {
		if (e instanceof Obstacle) {
			if (m_obstacles.contains(e))
				throw new IllegalArgumentException("Cannot have to same entity in the level");
			m_obstacles.add(e);
		}

		if (e instanceof Soul) {
			if (m_souls.contains(e))
				throw new IllegalArgumentException("Cannot have to same entity in the level");
			m_souls.add(e);
		}

		if (e instanceof Nest) {
			if (m_nest.contains(e))
				throw new IllegalArgumentException("Cannot have to same entity in the level");
			m_nest.add(e);
		}

		if (e instanceof Special) {
			m_special = e;
		}

		if (e instanceof Player) {
			m_player = e;
		}

		if (e instanceof Missile) {
			if (m_missiles.contains(e))
				throw new IllegalArgumentException("Cannot have to same entity in the level");
			m_missiles.add(e);
		}

		m_entities.add(e);
		m_collisionGrid.addEntity(e);
	}

	public void removeEntity(Entity e) {
		m_toRemove.add(e);
	}

	// Update the collisionGrid with the future new values translated by x and y.
	public void updateEntity(Entity e, int x, int y) {
		m_collisionGrid.updateEntity(e, x, y);
	}

	public void step(long now) {
		removeEntities();
		Iterator<Entity> iter;
		if (now - lastUpdatePlayer > 1000 / 60) {
			lastUpdatePlayer = now;
			if (m_player != null)
				m_player.step(now);

			iter = m_missiles.iterator();
			while (iter.hasNext()) {
				iter.next().step(now);
			}
		}

		if (now - lastUpdateSouls > 1000 / 60) {
			iter = m_souls.iterator();
			while (iter.hasNext()) {
				iter.next().step(now);
			}
			if (m_special != null)
				m_special.step(now);
			lastUpdateSouls = now;
		}
		if (now - lastUpdateObstacles > 500) {
			iter = m_obstacles.iterator();
			while (iter.hasNext()) {
				iter.next().step(now);
			}
			if (m_special != null)
				m_special.step(now);
			lastUpdateObstacles = now;
		}

		iter = m_nest.iterator();
		if (now - lastUpdateNests > Options.NEST_SPAWN_DELAY) {
			while (iter.hasNext()) {
				iter.next().step(now);
			}
			lastUpdateNests = now;
		}
	}

	private void removeEntities() {
		Iterator<Entity> iter = m_toRemove.iterator();
		while (iter.hasNext()) {
			Entity e = iter.next();
			if (e instanceof Obstacle) {
				m_obstacles.remove(e);
			}

			if (e instanceof Soul) {
				m_souls.remove(e);
			}

			if (e instanceof Nest) {
				m_nest.remove(e);
			}

			if (e instanceof Special) {
				m_special = null;
			}

			if (e instanceof Player) {
				m_player = null;
			}

			if (e instanceof Missile) {
				m_missiles.remove(e);
			}

			m_entities.remove(e);
			m_collisionGrid.removeEntity(e);
			iter.remove();
		}
	}

	public boolean wontCollide(Entity entity, IDirection d) {
		return m_collisionGrid.wontCollide(entity, d);
	}

	void transform() {
		Iterator<Entity> iter = m_entities.iterator();
		while (iter.hasNext())
			iter.next().transform();
	}

	public WorldType getWorldType() {
		return m_model.getWorldType();
	}

	void levelGenerator(String level) {
		if (level.length() != Options.LVL_HEIGHT * Options.LVL_WIDTH / 2)
			throw new IllegalArgumentException("Wrong lvl length in your file");
		// 1st quarter (NW)
		for (int i = 0; i < Options.LVL_HEIGHT / 2; i++) {
			for (int j = 0; j < Options.LVL_WIDTH / 2; j++) {
				if()
				entityInterpret(level.charAt(i * j), i, j);
			}
		}

	}

	public void entityInterpret(char c, int x, int y) {
		switch (c) {
		case 'O':
			new Obstacle(this, x, y, Options.OBSTACLE_WIDTH, Options.OBSTACLE_HEIGHT);
		case 'S':
			new Soul(this, x, y, Options.SOUL_WIDTH, Options.SOUL_HEIGHT);
		case 'N':
			new Nest(this, x, y, Options.NEST_WIDTH, Options.NEST_HEIGHT);
		case '*':
			new Special(this, x, y, Options.SPCL_WIDTH, Options.SPCL_HEIGHT);
		case '_':
		}
		throw new IllegalArgumentException("Wrong letter in the level file");
	}
}
