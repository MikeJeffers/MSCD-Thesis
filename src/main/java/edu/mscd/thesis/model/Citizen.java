package edu.mscd.thesis.model;

import java.util.Objects;

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
		this.money = Util.getRandomBetween(1, 55);
		this.happiness = Util.getRandomBetween(1, 55);
		this.age = Util.getRandomBetween(1, 80);
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
		work=b;
	}

	@Override
	public void liveAt(Building b) {
		home=b;
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
	public boolean equals(Object other){
		if(other instanceof Person){
			Person o = (Person) other;
			return this.id==o.getID();
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}

	@Override
	public void update() {
		age++;
		if(this.employed()){
			this.money++;
		}else{
			money--;
		}
		if(!this.homeless()){
			this.happiness++;
		}else{
			happiness--;
		}
		if(this.homeless() && !this.employed()){
			this.happiness--;
		}
		happiness = Math.min(Rules.MAX, happiness);
		money = Math.min(Rules.MAX, money);
		happiness = Math.max(-Rules.MAX, happiness);
		money = Math.max(-Rules.MAX, money);
	}
	
	@Override
	public String toString(){
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
