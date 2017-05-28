package edu.mscd.thesis.util;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.mscd.thesis.model.city.CityData;
import edu.mscd.thesis.model.city.CityDataWeightVector;
import edu.mscd.thesis.model.city.CityProperty;

public class TestWeightVector {
	private static WeightVector<CityProperty> weights;
	private static CityData data;

	@BeforeClass
	public static void runOnceBeforeClass() {
		data = new CityData();
		weights = new CityDataWeightVector();
		for(CityProperty prop: CityProperty.values()){
			data.setProperty(prop, 0.5);
			weights.setWeightFor(prop, 0.5);
		}

	}

	@AfterClass
	public static void runOnceAfterClass() {

	}

	@Before
	public void runBeforeTestMethod() {

	}

	@After
	public void runAfterTestMethod() {
		//
	}



	@Test
	public void testWeightsAndScoreHalf() {
		for(CityProperty prop: CityProperty.values()){
			data.setProperty(prop, 0.5);
			weights.setWeightFor(prop, 0.5);
		}
		data.setProperty(CityProperty.POP, Rules.MAX_POPULATION/2.0);
		double score = Rules.scoring(data, weights);
		assertTrue(score==0.5);
	}
	
	@Test
	public void testWeightsAndScoreFull() {
		for(CityProperty prop: CityProperty.values()){
			data.setProperty(prop, 1);
			if(prop.needsInversion()){
				data.setProperty(prop, 0);
			}
			weights.setWeightFor(prop, 1);
		}
		data.setProperty(CityProperty.POP, Rules.MAX_POPULATION);
		weights.setWeightFor(CityProperty.POP, 1.0);
		double score = Rules.scoring(data, weights);
		assertTrue(score==1.0);
	}
	
	
	@Test
	public void testWeightsAndScoreEmpty() {
		for(CityProperty prop: CityProperty.values()){
			data.setProperty(prop, 0.5);
			weights.setWeightFor(prop, 0.0);
		}
		data.setProperty(CityProperty.POP, 0);
		weights.setWeightFor(CityProperty.POP, 0);
		double score = Rules.scoring(data, weights);
		assertTrue(score>=0);
	}
	
	@Test
	public void testWeightsAndScoreRandom() {
		for(CityProperty prop: CityProperty.values()){
			data.setProperty(prop, 0.5);
			weights.setWeightFor(prop, 0.5);
		}
		data.setProperty(CityProperty.POP, 0);
		weights.setWeightFor(CityProperty.POP, 0);
		double score = Rules.scoring(data, weights);
		assertTrue(score>=0);
		assertTrue(score<=1);
	}

}
