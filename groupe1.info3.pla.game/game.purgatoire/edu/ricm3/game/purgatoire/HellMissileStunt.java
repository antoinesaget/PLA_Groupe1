package edu.ricm3.game.purgatoire;

import java.awt.Color;

import ricm3.interpreter.IAutomaton;
import ricm3.interpreter.IDirection;
import ricm3.interpreter.IEntityType;

public class HellMissileStunt extends Stunt {

	HellMissileStunt(IAutomaton automaton, Color c) {
		super(automaton, c);
		// TODO Auto-generated constructor stub
	}
	
	HellMissileStunt(){
		super(Singleton.getNewMissileHellAut(), null, Color.DARK_GRAY);
	}
	
	@Override
	void pop(IDirection d) {
		m_entity.die();
	}

	@Override
	void wizz(IDirection d) {
		System.out.println("wizz hell soul");
	}

	@Override
	void hit(IDirection d) {
		System.out.println("hit hell soul");
	}

	@Override
	void egg() {
		System.out.println("egg hell soul");
	}
	
	void goingOut(IDirection d){
		m_entity.die();
	}
	
	@Override
	public void step(long now) {
		Entity entity = m_entity.m_level.m_collisionGrid.testCollisionWithType(m_entity, IEntityType.ADVERSARY);
		if (entity != null) {
			entity.die();
		}
		m_automaton.step(m_entity);
	}

}
