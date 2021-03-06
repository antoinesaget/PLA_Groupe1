package ricm3.parser;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import ricm3.interpreter.IAction;
import ricm3.interpreter.IAction.IEgg;
import ricm3.interpreter.IAction.IHit;
import ricm3.interpreter.IAction.IMove;
import ricm3.interpreter.IAction.IPop;
import ricm3.interpreter.IAction.IWait;
import ricm3.interpreter.IAction.IWizz;
import ricm3.interpreter.IAutomaton;
import ricm3.interpreter.IBehaviour;
import ricm3.interpreter.ICondition;
import ricm3.interpreter.ICondition.IAnd;
import ricm3.interpreter.ICondition.IBinaryOp;
import ricm3.interpreter.ICondition.ICell;
import ricm3.interpreter.ICondition.IClosest;
import ricm3.interpreter.ICondition.IGotPower;
import ricm3.interpreter.ICondition.IKey;
import ricm3.interpreter.ICondition.IMyDir;
import ricm3.interpreter.ICondition.INot;
import ricm3.interpreter.ICondition.IOr;
import ricm3.interpreter.ICondition.ITrue;
import ricm3.interpreter.ICondition.IUnaryOp;
import ricm3.interpreter.IDirection;
import ricm3.interpreter.IEntityType;
import ricm3.interpreter.IState;
import ricm3.interpreter.ITransition;

/* Michael PÉRIN, Verimag / Univ. Grenoble Alpes, june 2018
 *
 * Constructors of the Abstract Syntax Tree of Game Automata
 */

public class Ast {

	// All this is only for the graphical .dot output of the Abstract Syntax Tree

	public String kind; // the name of the non-terminal node

	public int id = Id.fresh(); // a unique id used as a graph node

	// AST as tree

	public String dot_id() {
		return Dot.node_id(this.id);
	}

	public String as_tree_son_of(Ast father) {
		return Dot.edge(father.dot_id(), this.dot_id()) + this.as_dot_tree();
	}

	public String as_dot_tree() {
		return this.as_tree_node() + this.tree_edges();
	}

	public String as_tree_node() {
		return Dot.declare_node(this.dot_id(), this.kind, "");
	}

	public String tree_edges() {
		return "undefined: " + this.kind + ".tree_edges";
	}

	// AST as automata in .dot format

	public String as_dot_aut() {
		return "undefined " + this.kind + ".as_dot_aut";
	}

	// AST as active automata (interpreter of transitions)

	public Object make() {
		return null;
	}

	public static class Terminal extends Ast {
		String value;

		Terminal(String string) {
			this.kind = "Terminal";
			this.value = string;
		}

		public String toString() {
			return value;
		}

		public String tree_edges() {
			String value_id = Dot.node_id(-this.id);
			return Dot.declare_node(value_id, value, "shape=none, fontsize=10, fontcolor=blue")
					+ Dot.edge(this.dot_id(), value_id);
		}

		public IUnaryOp make_unaryOp() {
			switch (value) {
			case "!":
				return new INot();
			default:
				return null;
			}
		}

		public IBinaryOp make_binaryOp() {
			switch (value) {
			case "&":
				return new IAnd();
			case "/":
				return new IOr();
			default:
				return null;
			}
		}
	}

	// Value = Constant U Variable

	public static class Value extends Ast {
	}

	public static class Constant extends Value {

		Terminal value;

		Constant(String string) {
			this.kind = "Constant";
			this.value = new Terminal(string);
		}

		public String tree_edges() {
			return value.as_tree_son_of(this);
		}

		public String toString() {
			return value.toString();
		}
	}

	public static class Variable extends Value {

		Terminal name;

		Variable(String string) {
			this.kind = "Variable";
			this.name = new Terminal(string);
		}

		public String tree_edges() {
			return name.as_tree_son_of(this);
		}

		public String toString() {
			return name.toString();
		}
	}

	// Parameter = Underscore U Key U Direction U Entity
	// Parameter are not Expression (no recursion)

	public static abstract class Parameter extends Ast {
	}

	public static class Underscore extends Parameter {
		Underscore() {
			this.kind = "Underscore";
		}

		@Override
		public Integer make() {
			return KeyEvent.VK_UNDERSCORE;
		}

		public String tree_edges() {
			return "";
		}

		public String toString() {
			return "_";
		}
	}

	public static class Number_as_String extends Parameter {

		Constant value;

		Number_as_String(String string) {
			this.kind = "Number";
			this.value = new Constant(string);
		}

		public String tree_edges() {
			return value.as_tree_son_of(this);
		}

		public String toString() {
			return value.toString();
		}
	}

	public static class Key extends Parameter {

		Constant value;

		Key(String string) {
			this.kind = "Key";
			this.value = new Constant(string);
		}

		@Override
		public Integer make() {
			String keyString = value.toString().toUpperCase();
			if(keyString.length() == 1) {
				return (int) keyString.charAt(0);
			} else {
				switch (keyString) {
				case "SPACE" : return KeyEvent.VK_SPACE;
				case "ENTER" : return KeyEvent.VK_ENTER;
				case "FU" : return KeyEvent.VK_UP;
				case "FD" : return KeyEvent.VK_DOWN;
				case "FR" : return KeyEvent.VK_RIGHT;
				case "FL" : return KeyEvent.VK_LEFT;
				}
			}
			throw new IllegalStateException();
		}

		public String tree_edges() {
			return value.as_tree_son_of(this);
		}

		public String toString() {
			return value.toString();
		}
	}

	public static class Direction extends Parameter {

		Value value;

		Direction(Value value) {
			this.kind = "Direction";
			this.value = value;
		}

		@Override
		public IDirection make() {
			switch(value.toString()) {
			case "N": return IDirection.NORTH;
			case "S": return IDirection.SOUTH;
			case "E": return IDirection.EAST;
			case "O": return IDirection.WEST;
			case "F": return IDirection.FRONT;
			case "B": return IDirection.BACK;
			case "L": return IDirection.LEFT;
			case "R": return IDirection.RIGHT;
			}
			throw new IllegalStateException();
		}

		public String tree_edges() {
			return value.as_tree_son_of(this);
		}

		public String toString() {
			return value.toString();
		}
	}

	public static class Entity extends Parameter {

		Value value;

		Entity(Value expression) {
			this.kind = "Entity";
			this.value = expression;
		}

		@Override
		public IEntityType make() {
			switch(value.toString()) {
			case "A": return IEntityType.ADVERSARY;
			case "D": return IEntityType.DANGER;
			case "G": return IEntityType.GATE;
			case "J": return IEntityType.JUMP;
			case "M": return IEntityType.MISSILE;
			case "O": return IEntityType.OBSTACLE;
			case "P": return IEntityType.PICK;
			case "T": return IEntityType.TEAM;
			case "V": return IEntityType.VOID;
			case "@": return IEntityType.PLAYER;
			case "_": return IEntityType.ANYTHING;
			}
			throw new IllegalStateException();
		}
		
		public String tree_edges() {
			return value.as_tree_son_of(this);
		}

		public String toString() {
			return value.toString();
		}
	}

	// Expression = UnaryOp Expression U Expression BinaryOp Expression U
	// FunCall(Parameters)

	public static abstract class Expression extends Ast {
		public abstract String toString();

		public abstract ICondition make_condition();

		public abstract IAction make_action();
	}

	public static class UnaryOp extends Expression {

		Terminal operator;
		Expression operand;

		UnaryOp(String operator, Expression operand) {
			this.kind = "UnaryOp";
			this.operator = new Terminal(operator);
			this.operand = operand;
		}

		public String tree_edges() {
			return operator.as_tree_son_of(this) + operand.as_tree_son_of(this);
		}

		public String toString() {
			return operator + "(" + operand + ")";
		}

		@Override
		public ICondition make_condition() {
			IUnaryOp op = operator.make_unaryOp();
			op.setCondition(operand.make_condition());
			return op;
		}

		@Override
		public IAction make_action() {
			return null;
		}
	}

	public static class BinaryOp extends Expression {

		Terminal operator;
		Expression left_operand;
		Expression right_operand;

		BinaryOp(Expression l, String operator, Expression r) {
			this.kind = "BinaryOp";
			this.operator = new Terminal(operator);
			this.left_operand = l;
			this.right_operand = r;
		}

		public String tree_edges() {
			return left_operand.as_tree_son_of(this) + operator.as_tree_son_of(this)
					+ right_operand.as_tree_son_of(this);
		}

		public String toString() {
			return "(" + left_operand + " " + operator + " " + right_operand + ")";
		}

		@Override
		public ICondition make_condition() {
			IBinaryOp op = operator.make_binaryOp();
			op.setConditions(left_operand.make_condition(), right_operand.make_condition());
			return op;
		}

		@Override
		public IAction make_action() {
			return null;
		}

	}

	public static class FunCall extends Expression {

		Terminal name;
		List<Parameter> parameters;

		FunCall(String name, List<Parameter> parameters) {
			this.kind = "FunCall";
			this.name = new Terminal(name);
			this.parameters = parameters;
		}

		public String tree_edges() {
			String output = new String();
			output += name.as_tree_son_of(this);
			ListIterator<Parameter> Iter = this.parameters.listIterator();
			while (Iter.hasNext()) {
				Parameter parameter = Iter.next();
				output += parameter.as_tree_son_of(this);
			}
			return output;
		}

		public String toString() {
			String string = new String();
			ListIterator<Parameter> Iter = this.parameters.listIterator();
			while (Iter.hasNext()) {
				Parameter parameter = Iter.next();
				string += parameter.toString();
				if (Iter.hasNext()) {
					string += ",";
				}
			}
			return name + "(" + string + ")";
		}

		@Override
		public ICondition make_condition() {
			switch(name.toString()) {
			case "True" : return new ITrue();
			case "Key" : return new IKey(parameters);
			case "MyDir" : return new IMyDir(parameters);
			case "Cell" : return new ICell(parameters);
			case "Closest" : return new IClosest(parameters);
			case "GotPower" : return new IGotPower(parameters); //isInrange
//			case "GotStuff" : return new GotStuff();
			}
			throw new IllegalStateException();
		}

		@Override
		public IAction make_action() {
			switch(name.toString()) {
			case "Wait" : return new IWait(); 
			case "Wizz" : return new IWizz(parameters);
			case "Pop" : return new IPop(parameters);
			case "Move" : return new IMove(parameters);
//			case "Jump" :d
//			case "Turn" :d
			case "Hit" : return new IHit(parameters);
//			case "Protect" :d
//			case "Pick" :d
//			case "Throw" :d
//			case "Store" :
//			case "Get" :
//			case "Power" :
//			case "Kamikaze" :
			case "Egg" : return new IEgg();
			}
			throw new IllegalStateException();
		}
	}

	public static class Condition extends Ast {

		Expression expression;

		Condition(Expression expression) {
			this.kind = "Condition";
			this.expression = expression;
		}

		@Override
		public ICondition make() {
			return expression.make_condition();
		}

		public String tree_edges() {
			return expression.as_tree_son_of(this);
		}

		public String toString() {
			return expression.toString();
		}
	}

	public static class Action extends Ast {

		Expression expression;

		Action(Expression expression) {
			this.kind = "Action";
			this.expression = expression;
		}

		@Override
		public IAction make() {
			return expression.make_action();
		}

		public String tree_edges() {
			return expression.as_tree_son_of(this);
		}

		public String toString() {
			return expression.toString();
		}
	}

	public static class State extends Ast {

		Terminal name;

		State(String string) {
			this.kind = "State";
			this.name = new Terminal(string);
		}

		@Override
		public IState make() {
			return new IState(name.toString(), id);
		}

		public String tree_edges() {
			return name.as_tree_son_of(this);
		}

		public String dot_id_of_state_of(Automaton automaton) {
			return Dot.name(automaton.id + "." + name.toString());
		}

		public String as_state_of(Automaton automaton) {
			return Dot.declare_node(this.dot_id_of_state_of(automaton), name.toString(), "shape=circle, fontsize=4");
		}
	}

	public static class AI_Definitions extends Ast {

		List<Automaton> automata;

		AI_Definitions(List<Automaton> list) {
			this.kind = "AI_Definitions";
			this.automata = list;
		}

		@Override
		public List<IAutomaton> make() {
			List<IAutomaton> automatons = new ArrayList<IAutomaton>();
			Iterator<Automaton> iter = automata.iterator();
			while(iter.hasNext()) {
				automatons.add(iter.next().make());
			}
			
			return automatons;
		}

		public String tree_edges() {
			String output = new String();
			ListIterator<Automaton> Iter = this.automata.listIterator();
			while (Iter.hasNext()) {
				Automaton automaton = Iter.next();
				output += automaton.as_tree_son_of(this);
			}
			return output;
		}

		public String as_dot_tree() {
			return Dot.graph("AST", this.as_tree_node() + this.tree_edges());
		}

		public String as_dot_aut() {
			String string = new String();
			ListIterator<Automaton> Iter = this.automata.listIterator();
			while (Iter.hasNext()) {
				Automaton automaton = Iter.next();
				string += automaton.as_dot_aut();
			}
			return Dot.graph("Automata", string);
		}

	}

	public static class Automaton extends Ast {

		Terminal name;
		State entry;
		List<Behaviour> behaviours;

		Automaton(String name, State entry, List<Behaviour> behaviours) {
			this.kind = "Automaton";
			this.name = new Terminal(name);
			this.entry = entry;
			this.behaviours = behaviours;
		}

		public IAutomaton make() {
			List<IBehaviour> iBehaviours = new LinkedList<IBehaviour>();
			Iterator<Behaviour> iter = behaviours.iterator();
			while (iter.hasNext()) {
				iBehaviours.add(iter.next().make());
			}

			IState istate_initial = entry.make();
			return new IAutomaton(istate_initial, iBehaviours, name.toString());
		}

		public String tree_edges() {
			String output = new String();
			output += name.as_tree_son_of(this);
			output += entry.as_tree_son_of(this);
			ListIterator<Behaviour> Iter = this.behaviours.listIterator();
			while (Iter.hasNext()) {
				Behaviour behaviour = Iter.next();
				output += behaviour.as_tree_son_of(this);
			}
			return output;
		}

		public String as_dot_aut() {
			String string = new String();
			string += Dot.declare_node(this.dot_id(), name.toString(), "shape=box, fontcolor=red");
			string += Dot.edge(this.dot_id(), entry.dot_id_of_state_of(this));
			ListIterator<Behaviour> Iter = this.behaviours.listIterator();
			while (Iter.hasNext()) {
				Behaviour behaviour = Iter.next();
				string += behaviour.as_transition_of(this);
			}
			return Dot.subgraph(this.id, string);
		}

	}

	public static class Behaviour extends Ast {

		State source;
		List<Transition> transitions;

		Behaviour(State state, List<Transition> transitions) {
			this.kind = "Behaviour";
			this.source = state;
			this.transitions = transitions;
		}

		@Override
		public IBehaviour make() {
			List<ITransition> iTransitions = new LinkedList<ITransition>();
			Iterator<Transition> iter = transitions.iterator();
			while (iter.hasNext()) {
				iTransitions.add(iter.next().make());
			}

			IState istate_initial = source.make();
			return new IBehaviour(istate_initial, iTransitions);
		}

		public String tree_edges() {
			String output = new String();
			output += source.as_tree_son_of(this);
			ListIterator<Transition> Iter = this.transitions.listIterator();
			while (Iter.hasNext()) {
				Transition transition = Iter.next();
				output += transition.as_tree_son_of(this);
			}
			return output;
		}

		public String as_transition_of(Automaton automaton) {
			String string = new String();
			ListIterator<Transition> Iter = this.transitions.listIterator();
			while (Iter.hasNext()) {
				Transition transition = Iter.next();
				string += transition.as_transition_from(automaton, source);
			}
			return source.as_state_of(automaton) + string;
		}
	}

	public static class Transition extends Ast {

		Condition condition;
		Action action;
		State target;

		Transition(Condition condition, Action action, State target) {
			this.kind = "Transition";
			this.condition = condition;
			this.action = action;
			this.target = target;
		}

		public ITransition make() {
			return new ITransition(condition.make(), action.make(), target.make());
		}

		public String tree_edges() {
			return condition.as_tree_son_of(this) + action.as_tree_son_of(this) + target.as_tree_son_of(this);
		}

		public String toString() {
			return condition + "? " + action;
		}

		public String as_transition_from(Automaton automaton, State source) {
			String string = new String();
			string += Dot.declare_node(this.dot_id(), this.toString(), "shape=box, fontcolor=blue, fontsize=6");
			string += Dot.edge(source.dot_id_of_state_of(automaton), this.dot_id());
			string += Dot.edge(this.dot_id(), target.dot_id_of_state_of(automaton));
			return string;
		}
	}
}
