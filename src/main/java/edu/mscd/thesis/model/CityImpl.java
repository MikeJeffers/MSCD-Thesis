package edu.mscd.thesis.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.mscd.thesis.controller.CityData;
import edu.mscd.thesis.controller.CityProperty;
import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public class CityImpl implements City {

	private Set<Person> population;
	private static int ID_COUNTER = 0;
	private World world;
	private int[] zoneCounts = new int[ZoneType.values().length];

	public CityImpl(World w) {
		this.world = w;
		countZones();

		this.population = new HashSet<Person>();
		for (int i = 0; i < Rules.STARTING_POPULATION; i++) {

			this.population.add(new Citizen(ID_COUNTER));
			ID_COUNTER++;
		}

	}

	@Override
	public Collection<Person> getPopulation() {
		return this.population;
	}


	@Override
	public int totalPopulation() {
		return this.population.size();
	}
	
	@Override
	public double averageHappiness() {
		double total = totalPopulation();
		double happiness = 0.0;
		for (Person p : population) {
			happiness +=((double)p.getHappiness())/((double)Rules.MAX);
		}
		if (total < 1) {
			return 0;
		}
		return happiness/total;
	}

	@Override
	public double averageWealth() {
		double total = totalPopulation();
		double money = 0.0;
		for (Person p : population) {
			money +=((double)p.getMoney())/((double)Rules.MAX);
		}
		if (total < 1) {
			return 0;
		}
		return money/total;
	}
	

	@Override
	public double percentageHomeless() {
		double total = totalPopulation();
		double homelessCount = 0.0;
		for (Person p : population) {
			if (p.homeless()) {
				homelessCount++;
			}
		}
		if (total < 1) {
			return 0;
		}
		return ((double) homelessCount) / ((double) total);
	}

	@Override
	public double percentageUnemployed() {
		double total = totalPopulation();
		double unemployed = 0.0;
		for (Person p : population) {
			if (!p.employed()) {
				unemployed++;
			}
		}
		if (total < 1) {
			return 0;
		}
		return ((double) unemployed) / ((double) total);
	}

	@Override
	public Collection<Person> getUnemployed() {
		Collection<Person> people = new HashSet<Person>();
		for (Person p : population) {
			if (!p.employed()) {
				people.add(p);
			}
		}

		return people;
	}

	@Override
	public Collection<Person> getHomeless() {
		Collection<Person> people = new HashSet<Person>();
		for (Person p : population) {
			if (p.homeless()) {
				people.add(p);
			}
		}
		return people;
	}

	private void addPerson() {
		Person p = new Citizen(ID_COUNTER);
		ID_COUNTER++;
		this.population.add(p);

	}

	private void removePerson(Person p) {
		clearReferencesFrom(p.getHome(), p);
		clearReferencesFrom(p.getWork(), p);
		p.employAt(null);
		p.liveAt(null);
		this.population.remove(p);
	}

	private void clearReferencesFrom(Building b, Person p) {
		if (b != null) {
			b.removeOccupant(p);
		}
	}

	@Override
	public void update() {
		Collection<Person> toRemove = new HashSet<Person>();
		int toAddCounter = 0;
		for (Person p : population) {
			p.update();
			if (p.getHappiness() < 0 && p.getMoney() < 0 && !p.employed() && p.homeless()) {
				toRemove.add(p);
			}else if(p.getAge()>Rules.LIFE_SPAN){
				toRemove.add(p);
			} else if (p.employed() && !p.homeless()) {
				int rand = Util.getRandomBetween(0, 100);
				if (rand < Rules.BIRTH_RATE) {
					toAddCounter++;
				}

			}
		}
		for (Person p : toRemove) {
			this.removePerson(p);
		}
		for (int i = 0; i < toAddCounter; i++) {
			addPerson();
		}
		if(this.population.isEmpty()){
			addPerson();
		}
		countZones();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("City:{");
		sb.append("popCount=");
		sb.append(this.totalPopulation());
		sb.append("unEmployRate=");
		sb.append(this.percentageUnemployed());
		sb.append("homelessRate=");
		sb.append(this.percentageHomeless());
		sb.append(" population:{");
		for (Person p : population) {
			sb.append(p);
			sb.append("\n");
		}
		sb.append("}}");
		return sb.toString();
	}



	private void countZones(){
		for(int i=0; i<zoneCounts.length; i++){
			zoneCounts[i]=0;
		}
		for(Tile t: this.world.getTiles()){
			zoneCounts[t.getZoneType().ordinal()]++;
		}
	}

	@Override
	public double residentialDemand() {
		return Rules.getDemandForZoneType(ZoneType.RESIDENTIAL, this.world);
	}

	@Override
	public double commercialDemand() {
		return Rules.getDemandForZoneType(ZoneType.COMMERICAL, this.world);
	}

	@Override
	public double industrialDemand() {
		return Rules.getDemandForZoneType(ZoneType.INDUSTRIAL, this.world);
	}

	@Override
	public int getZoneCount(ZoneType zt) {
		return this.zoneCounts[zt.ordinal()];
	}

	@Override
	public CityData getData() {
		CityData data = new CityData();
		data.setProperty(CityProperty.R_DEMAND, this.residentialDemand());
		data.setProperty(CityProperty.C_DEMAND, this.commercialDemand());
		data.setProperty(CityProperty.WEALTH, this.averageWealth());
		data.setProperty(CityProperty.HAPPY, this.averageHappiness());
		data.setProperty(CityProperty.UNEMPLOY, this.percentageUnemployed());
		data.setProperty(CityProperty.HOMELESS, this.percentageHomeless());
		return data;
	}


}
