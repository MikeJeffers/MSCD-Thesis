package edu.mscd.thesis.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.mscd.thesis.model.tiles.Tile;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneType;

public class TestWorld {

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
		for (int i = 1; i < 5; i++) {
			int x = (i + 1) * 2;
			int y = (i + 1) * 3;
			World w = new WorldImpl(x, y, "", false);
			assertNotNull(w);
			assertNotNull(w.getCity());
			assertTrue(w.getTiles().length == x * y);
		}

	}

	@Test
	public void testZoningOfWorld() {
		Pos2D target = new Pos2D(2, 2);
		World w = new WorldImpl(5, 5, "", false);
		Zone zoneAtTarg = w.getZoneAt(target);
		assertTrue(ZoneType.EMPTY == zoneAtTarg.getZoneType());
		boolean success = w.setZoneAt(target, ZoneType.RESIDENTIAL);
		if (success) {// if zoning successful, tile is valid zonable tile
			Zone reZoned = w.getZoneAt(target);
			assertTrue(ZoneType.RESIDENTIAL == reZoned.getZoneType());
			Tile t = w.getTileAt(target);
			assertTrue(t.getType().isZonable());
		} else { // tile is a non-zonable type
			Tile t = w.getTileAt(target);
			assertNotNull(t);
			assertFalse(t.getType().isZonable());
		}

	}
}
