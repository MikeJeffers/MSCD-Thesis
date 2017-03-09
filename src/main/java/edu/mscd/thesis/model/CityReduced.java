package edu.mscd.thesis.model;

import java.util.Collection;
import edu.mscd.thesis.model.zones.ZoneType;

public class CityReduced implements City{
	private int populationSize;
	private int rDemand;
	private int cDemand;
	private int iDemand;
	private double homelessnessRate;
	private double unemploymentRate;
	private double averageHappy;
	private double averageMoney;

	public CityReduced(City original) {
		cDemand = original.commercialDemand();
		rDemand = original.residentialDemand();
		iDemand = original.industrialDemand();
		homelessnessRate = original.percentageHomeless();
		unemploymentRate = original.percentageUnemployed();
		averageMoney = original.averageWealth();
		averageHappy = original.averageHappiness();
		this.populationSize = original.totalPopulation();

	}

	
	@Override
	public double averageHappiness() {
		return this.averageHappy;
	}

	@Override
	public double averageWealth() {
		return this.averageMoney;
	}

	@Override
	public int residentialDemand() {
		return rDemand;
	}

	@Override
	public int commercialDemand() {
		return cDemand;
	}

	@Override
	public int industrialDemand() {
		return iDemand;
	}

	@Override
	public int totalPopulation() {
		return populationSize;
	}

	@Override
	public double percentageHomeless() {
		return this.homelessnessRate;
	}

	@Override
	public double percentageUnemployed() {
		return this.unemploymentRate;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("REDUCEDCITY:{");
		sb.append("popCount=");
		sb.append(this.totalPopulation());
		sb.append("unEmployRate=");
		sb.append(this.percentageUnemployed());
		sb.append("homelessRate=");
		sb.append(this.percentageHomeless());
		sb.append(" populationCount:");
		sb.append(this.totalPopulation());
		sb.append("}");
		return sb.toString();
	}
	
	@Override
	public Collection<Person> getPopulation() {
		//TODO should fail; does nothing
		return null;
	}
	
	@Override
	public void update() {
		//TODO should fail; does nothing
	}
	
	@Override
	public Collection<Person> getUnemployed() {
		//TODO should fail; does nothing
		return null;
	}

	@Override
	public Collection<Person> getHomeless() {
		//TODO should fail; does nothing
		return null;
	}
	
	@Override
	public int zoneCount(ZoneType zType) {
		//TODO should fail; does nothing
		return 0;
	}


}
