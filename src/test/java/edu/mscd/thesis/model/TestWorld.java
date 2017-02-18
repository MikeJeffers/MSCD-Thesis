package edu.mscd.thesis.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.JavaFXThreadingRule;

public class TestWorld {
	@Rule 
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	@BeforeClass
	public static void runOnceBeforeClass() {

	}

	@AfterClass
	public static void runOnceAfterClass() {
		//
	}

	@Before
	public void runBeforeTestMethod() {
		//
	}

	@After
	public void runAfterTestMethod() {
		//
	}

	@Test
	public void testWorldInit() {
		int x=2;
		int y=3;
		World w = new WorldImpl(x, y);
		assertNotNull(w);
		assertNotNull(w.getCity());
		assertTrue(w.getTiles().length==x*y);

	}

	@Test
	public void testZoningOfWorld() {
		Pos2D target =new Pos2D(2, 2);
		World w = new WorldImpl(5, 5);
		Zone zoneAtTarg = w.getZoneAt(target);
		assertTrue(ZoneType.EMPTY==zoneAtTarg.getZoneType());
		boolean success = w.setZoneAt(target, ZoneType.RESIDENTIAL);
		if(success){//if zoning successful, tile is valid zonable tile
			Zone reZoned = w.getZoneAt(target);
			assertTrue(ZoneType.RESIDENTIAL==reZoned.getZoneType());
			Tile t = w.getTileAt(target);
			assertTrue(t.getType().isZonable());
		}else{ //tile is a non-zonable type
			Tile t = w.getTileAt(target);
			assertNotNull(t);
			assertFalse(t.getType().isZonable());
		}
		

	}
}