package edu.mscd.thesis.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.bldgs.Factory;
import edu.mscd.thesis.model.bldgs.House;
import edu.mscd.thesis.model.people.Citizen;
import edu.mscd.thesis.model.people.Person;
import edu.mscd.thesis.model.tiles.TileType;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;

public class TestPerson {
	private static Building work;
	private static Building home;

	@BeforeClass
	public static void runOnceBeforeClass() {
			

	}

	@AfterClass
	public static void runOnceAfterClass() {
		//
	}

	@Before
	public void runBeforeTestMethod() {
		
		work = new Factory(new Pos2D(0,0), TileType.BARREN, ZoneType.INDUSTRIAL, Density.HIGH);
		home = new House(new Pos2D(0,1), TileType.BARREN, ZoneType.RESIDENTIAL, Density.HIGH);
		//
	}

	@After
	public void runAfterTestMethod() {
		//
	}

	@Test
	public void testInit() {
		Person p = new Citizen(1);
		assertNotNull(p);
	}
	
	@Test
	public void testStates(){
		Person p = new Citizen(1);
		assertFalse(p.employed());
		work.addOccupant(p);
		assertTrue(p.employed());
		assertTrue(p.homeless());
		home.addOccupant(p);
		assertFalse(p.homeless());
		work.addOccupant(p);
		assertTrue(p.employed());
	}
	
	@Test
	public void testStatesWithUpdate() {
		int id = 1;
		Person p = new Citizen(id);
		assertEquals(id, p.getID());
		assertFalse(p.employed());
		assertTrue(p.homeless());
		assertNull(p.getHome());
		assertNull(p.getWork());
		int money = p.getMoney();
		int happiness = p.getHappiness();
		int age = p.getAge();
		assertTrue(money>=0);
		assertTrue(happiness>=0);
		assertTrue(age>=0);
		
		//Turn
		p.update();
		work.update(Rules.MAX);
		home.update(Rules.MAX);
		assertNotNull(p);
		assertEquals(id, p.getID());
		assertFalse(p.employed());
		assertTrue(p.homeless());
		assertNull(p.getHome());
		assertNull(p.getWork());
		assertTrue(money>p.getMoney());
		assertTrue(happiness>p.getHappiness());
		assertTrue(age<p.getAge());
		
		//Change state
		home.addOccupant(p);
		work.addOccupant(p);
		assertNotNull(p);
		assertTrue(p.employed());
		assertFalse(p.homeless());
		assertNotNull(p.getHome());
		assertNotNull(p.getWork());
		
		//Take some number of turns
		for(int i=0; i<5; i++){
			p.update();
			work.update(Rules.MAX);
			home.update(Rules.MAX);
		}
		
		assertNotNull(p);
		assertTrue(p.employed());
		assertFalse(p.homeless());
		assertNotNull(p.getHome());
		assertNotNull(p.getWork());
		assertTrue(money<p.getMoney());
		assertTrue(happiness<p.getHappiness());
		assertTrue(age<p.getAge());
	}

}
