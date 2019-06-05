package edu.ricm3.game.purgatoire;

import java.awt.Color;
import java.awt.image.BufferedImage;

import ricm3.interpreter.IAutomaton;
import ricm3.interpreter.IDirection;

public class Stunt {

	IAutomaton m_automaton;
	Color m_c;
	BufferedImage m_sprite;
	Entity m_entity;

	Stunt(IAutomaton automaton, Color c) {
		m_automaton = automaton;
		m_c = c;
	}

	Stunt(IAutomaton automaton, Entity entity, Color c) {
		m_automaton = automaton;
		m_entity = entity;
		m_c = c;
	}

	Stunt(IAutomaton automaton, Entity entity, BufferedImage sprite) {
		m_automaton = automaton;
		m_entity = entity;
		m_sprite = sprite;
	}

	void tryMove(IDirection d) {
		switch (d) {
		case NORTH:
			if (m_entity.m_bounds.y == 1) {
				m_entity.m_level.m_model.nextLevel();
			}			
			move(0, -1);
			break;
		case SOUTH:
			if (m_entity.m_bounds.y <=  Options.LVL_HEIGHT - m_entity.m_bounds.height) {
				move(0,1);
			}
			break;
		case EAST:
			if (m_entity.m_bounds.x < Options.LVL_WIDTH - m_entity.m_bounds.height) {
				move(1,0);
			}
			break;
		case WEST:
			if (m_entity.m_bounds.x > 0) {
				move(-1,0);
			}
			break;
		}
	}

	void pop() {
		System.out.println("pop de base");
	}

	void wizz() {
		System.out.println("wizz de base");
	}

	void hit(IDirection d) {
		System.out.println("hit de base");
	}

	void move(int x, int y) {
		m_entity.m_bounds.x += x;
		m_entity.m_bounds.y += y;
	}

	void egg() {
		System.out.println("egg de base");
	}

	void getDamage(int DMG) {
		m_entity.m_HP -= DMG;
	}

	public void setAttachedEntity(Entity entity) {
		m_entity = entity;
	}
}