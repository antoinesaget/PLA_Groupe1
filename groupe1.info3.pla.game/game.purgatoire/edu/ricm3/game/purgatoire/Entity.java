package edu.ricm3.game.purgatoire;

import java.awt.Rectangle;

import ricm3.interpreter.IDirection;
import ricm3.interpreter.IEntityType;

public class Entity {
	int m_HP, m_maxHP;
	int m_DMG;
	int m_karmaToGive;
	Stunt m_heavenStunt, m_hellStunt, m_currentStunt;
	Level m_level;
	Rectangle m_bounds;
	IEntityType m_type;
	
	Entity(Level level, Stunt heaven, Stunt hell, int x, int y, int width, int height) {
		m_level = level;
		m_heavenStunt = heaven;
		m_hellStunt = hell;
		m_bounds = new Rectangle(x, y, width, height);
		transform();
	}

	public void transform() {
		if (getWorldType() == WorldType.HEAVEN)
			m_currentStunt = m_heavenStunt;
		else
			m_currentStunt = m_hellStunt;
	}
	
	void step(long now) {
	}
	
	WorldType getWorldType() {
		return m_level.getWorldType();
	}

	void takeDamage() {
		m_currentStunt.getDamage();
	}

	public void move(IDirection d) {
		m_currentStunt.move(d);
	}

	public void pop() {
		m_currentStunt.pop();
	}

	public void wizz() {
		m_currentStunt.wizz();
	}

	public void egg() {
		m_currentStunt.egg();
	}

	public void hit() {
		m_currentStunt.hit();
	}
}
