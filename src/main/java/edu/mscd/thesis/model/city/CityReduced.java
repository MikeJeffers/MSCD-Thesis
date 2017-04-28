package edu.mscd.thesis.model.city;

import java.util.Collection;

import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.zones.ZoneType;

public class CityReduced implements City{
	private int populationSize;
	private double rDemand;
	private double cDemand;
	private double iDemand;
	private double homelessnessRate;
	private double unemploymentRate;
	private double averageHappy;
	private double averageMoney;
	private int[] zoneCounts;
	private double densityRating;
	private CityData data;

	public CityReduced(City original) {
		this.cDemand = original.commercialDemand();
		this.rDemand = original.residentialDemand();
		this.iDemand = original.industrialDemand();
		this.homelessnessRate = original.percentageHomeless();
		this.unemploymentRate = original.percentageUnemployed();
		this.averageMoney = original.averageWealth();
		this.averageHappy = original.averageHappiness();
		this.populationSize = original.totalPopulation();
		this.data = original.getData();
		this.zoneCounts = new int[ZoneType.values().length];
		this.densityRating = original.densityRating();
		for(int i=0; i<ZoneType.values().length; i++){
			zoneCounts[i]=original.getZoneCount(ZoneType.values()[i]);
		}

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
	public double residentialDemand() {
		return rDemand;
	}

	@Override
	public double commercialDemand() {
		return cDemand;
	}

	@Override
	public double industrialDemand() {
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
	public int getZoneCount(ZoneType zt){
		return this.zoneCounts[zt.ordinal()];
	}
	
	@Override
	public double densityRating() {
		return this.densityRating;
	}


	@Override
	public CityData getData() {
		return this.data;
	}
	

}
