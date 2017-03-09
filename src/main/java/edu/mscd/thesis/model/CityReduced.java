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

	public CityReduced(City original) {
		cDemand = original.commercialDemand();
		rDemand = original.residentialDemand();
		iDemand = original.industrialDemand();
		homelessnessRate = original.percentageHomeless();
		unemploymentRate = original.percentageUnemployed();

		this.populationSize = original.totalPopulation();

	}

	@Override
	public Collection<Person> getPopulation() {
		return null;
	}

	@Override
	public int residentialDemand() {
		// TODO Auto-generated method stub
		return rDemand;
	}

	@Override
	public int commercialDemand() {
		// TODO Auto-generated method stub
		return cDemand;
	}

	@Override
	public int industrialDemand() {
		// TODO Auto-generated method stub
		return iDemand;
	}

	@Override
	public int zoneCount(ZoneType zType) {
		// TODO Auto-generated method stub
		return 0;
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
	public Collection<Person> getUnemployed() {
		return null;
	}

	@Override
	public Collection<Person> getHomeless() {
		return null;
	}


	@Override
	public void update() {

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
		sb.append(" population:");
		
		sb.append("}");
		return sb.toString();
	}
}
