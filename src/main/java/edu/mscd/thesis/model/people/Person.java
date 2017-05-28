package edu.mscd.thesis.model.people;


import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.bldgs.Building;

public interface Person{
	public Pos2D getCurrentPos();

	public Building getHome();
	public boolean homeless();

	public Building getWork();
	public boolean employed();

	public void fire();
	public void evict();
	public void employAt(Building b);

	public void liveAt(Building b);

	public int getHappiness();
	public void please(int amount);
	public int getMoney();
	public void pay(int amount);
	public int getID();
	public int getAge();
	void update();
	
}
