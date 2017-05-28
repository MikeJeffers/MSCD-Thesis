package edu.mscd.thesis.model.people;

import java.util.Objects;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public class Citizen implements Person {
	private Building home;
	private Building work;
	private Pos2D currentLocation;
	private int id;
	private int happiness;
	private int money;
	private int age;

	public Citizen(int _id) {
		this.id = _id;
		this.age = Util.getRandomBetween(1, (int) (Rules.LIFE_SPAN*0.9));
		this.money = (int) Util.mapValue(age, new double[]{0,  Rules.LIFE_SPAN}, new double[]{1, Rules.MAX*0.75});
		this.happiness = (int) Util.mapValue(age, new double[]{0,  Rules.LIFE_SPAN}, new double[]{1, Rules.MAX*0.75});
	}

	@Override
	public Pos2D getCurrentPos() {
		return currentLocation;
	}

	@Override
	public Building getHome() {
		return home;
	}

	@Override
	public Building getWork() {
		return work;
	}

	@Override
	public void employAt(Building b) {
		work = b;
	}

	@Override
	public void liveAt(Building b) {
		home = b;
	}

	@Override
	public int getHappiness() {
		return this.happiness;
	}

	@Override
	public int getMoney() {
		return this.money;
	}

	@Override
	public boolean homeless() {
		return this.home == null;
	}

	@Override
	public boolean employed() {
		return this.work != null;
	}

	@Override
	public void fire() {
		this.work = null;
	}

	@Override
	public void evict() {
		this.home = null;
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Person) {
			Person o = (Person) other;
			return this.id == o.getID();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public void update() {
		age++;

		happiness -= Rules.HAPPINESS_DECAY;
		money -= Rules.WEALTH_DECAY;

		happiness = (int) Util.boundValue(happiness, -Rules.MAX, Rules.MAX);
		money = (int) Util.boundValue(money, -Rules.MAX, Rules.MAX);
	}
	
	@Override
	public void please(int amount) {
		happiness+=amount;
	}

	@Override
	public void pay(int amount) {
		money+=amount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Person:{");
		sb.append("ID=");
		sb.append(this.id);
		sb.append(" employed=");
		sb.append(this.employed());
		sb.append(" homeless=");
		sb.append(this.homeless());
		sb.append(" happy=");
		sb.append(this.getHappiness());
		sb.append(" money=");
		sb.append(this.getMoney());
		sb.append("}");
		return sb.toString();

	}

	@Override
	public int getAge() {
		return this.age;
	}

	

}
