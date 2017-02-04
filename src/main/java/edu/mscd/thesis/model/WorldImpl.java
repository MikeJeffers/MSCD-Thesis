package edu.mscd.thesis.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.bldgs.PlaceOfWork;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneFactory;
import edu.mscd.thesis.model.zones.ZoneFactoryImpl;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WorldImpl implements World {
	private Tile[] tiles;
	private int cols, rows;

	public WorldImpl(int sizeX, int sizeY) {
		int size = sizeX * sizeY;
		tiles = new Tile[size];
		this.rows = sizeY;
		this.cols = sizeX;
		this.worldInit();
	}

	private void worldInit() {
		Random r = new Random();
		TileType[] types = TileType.values();
		ZoneFactory zFact = new ZoneFactoryImpl();
		for (int i = 0; i < tiles.length; i++) {
			Pos2D p = new Pos2D((i % cols), (i / cols));
			int typeSelection = r.nextInt(types.length);
			Tile t = new TileImpl(p, types[typeSelection], zFact);
			tiles[i] = t;
		}
	}

	@Override
	public void update() {
		// TODO
		// iterate through all zones: incr/decr value based on conditions

		for (int i = 0; i < tiles.length; i++) {
			Tile t = tiles[i];
			if (t.getZone().getZoneType() != ZoneType.EMPTY) {
				if (t.getZone().getZoneType() == ZoneType.INDUSTRIAL) {
					// fillJobsAt(t);
					System.out.println(t);
				} else if (t.getZone().getZoneType() == ZoneType.RESIDENTIAL) {
					findJobNearest(t);
					System.out.println(t);
				}
				// getnearest of type, get dist, modify value of zone in tile as
				// per ruleset
			}
			tiles[i].update();
		}
	}

	private void findJobNearest(Tile t) {
		Zone z = t.getZone();
		Building b = z.getBuilding();
		if (b != null) {
			for (Person p : b.getOccupants()) {
				if (!p.employed()) {
					Building work = findClosestUnfilledJob(t);
					p.employAt(work);
				}
			}
		}
	}

	private Building findClosestUnfilledJob(Tile t) {
		double minDist = Double.MAX_VALUE;
		Building bestCandidate = null;
		Pos2D origin = t.getPos();
		for (int i = 0; i < tiles.length; i++) {
			if (!tiles[i].equals(t)) {
				ZoneType type = tiles[i].getZone().getZoneType();
				if (type == ZoneType.INDUSTRIAL || type == ZoneType.COMMERICAL) {
					Pos2D dest = tiles[i].getPos();
					double d = origin.distBetween(dest);
					if (d < minDist) {
						Zone z = tiles[i].getZone();
						Building b = z.getBuilding();
						if (b.getMaxOccupants() > b.getOccupants().size()) {
							minDist = d;
							bestCandidate = b;
						}
					}
				}
			}
		}
		return bestCandidate;
	}

	private void fillJobsAt(Tile t) {
		if (t.getZone().getZoneType() == ZoneType.INDUSTRIAL) {
			Zone z = t.getZone();
			Building b = z.getBuilding();
			if (b != null) {
				boolean searchExhausted = false;
				int maxOccupants = b.getMaxOccupants();
				int currentOccpancy = b.currentOccupancy();
				while (currentOccpancy < maxOccupants && !searchExhausted) {
					Person p = findClosestUnemployed(t);
					if (p != null) {
						b.addOccupant(p);
						currentOccpancy = b.currentOccupancy();
					} else {
						searchExhausted = true;
					}
				}
			}
		}
	}

	private Person findClosestUnemployed(Tile t) {
		double minDist = Double.MAX_VALUE;
		Person bestCandidate = null;
		Pos2D origin = t.getPos();
		for (int i = 0; i < tiles.length; i++) {
			if (!tiles[i].equals(t)) {
				if (tiles[i].getZone().getZoneType() == ZoneType.RESIDENTIAL) {
					Pos2D dest = tiles[i].getPos();
					double d = origin.distBetween(dest);
					if (d < minDist) {
						Zone z = tiles[i].getZone();
						Building b = z.getBuilding();
						Collection<Person> people = b.getOccupants();
						for (Person p : people) {
							if (!p.employed()) {
								minDist = d;
								bestCandidate = p;
							}
						}
					}
				}
			}
		}
		return bestCandidate;
	}

	private Person findUnemployed(Tile t) {
		for (int i = 0; i < tiles.length; i++) {
			if (!tiles[i].equals(t)) {
				if (tiles[i].getZone().getZoneType() == ZoneType.RESIDENTIAL) {
					Zone z = tiles[i].getZone();
					Building b = z.getBuilding();
					Collection<Person> people = b.getOccupants();
					for (Person p : people) {
						if (!p.employed()) {
							return p;
						}
					}
				}

			}
		}
		return null;
	}

	@Override
	public boolean setZoneAt(Pos2D pos, ZoneType zt) {
		Tile t = this.getTileAt(pos);
		if (t == null) {
			return false;
		}
		return t.setZone(zt);
	}

	@Override
	public boolean setAllZonesAround(Pos2D pos, ZoneType zt, int radius, boolean squareSelect) {
		Tile t = this.getTileAt(pos);
		if (t == null) {
			return false;
		}
		List<Tile> tilesInRange;
		if (squareSelect) {
			tilesInRange = getNeighborsManhattanDist(t, radius);
		} else {
			tilesInRange = getNeighborsCircularDist(t, radius);
		}
		for (Tile reZone : tilesInRange) {
			reZone.setZone(zt);
		}
		return true;
	}


	private Tile getNearestOfType(Tile t, ZoneType zt) {
		double distance = 10000;
		Tile found = null;
		for (int i = 0; i < tiles.length; i++) {
			if (!tiles[i].equals(t)) {
				if (tiles[i].getZone().getZoneType() == zt) {
					double dist = tiles[i].getPos().distBetween(t.getPos());
					if (dist < distance) {
						distance = dist;
						found = tiles[i];
					}
				}

			}
		}
		return found;
	}

	private Tile findNearestOfType(Tile origin, ZoneType zt) {
		// TODO
		return null;
	}

	private List<Tile> getNeighborsCircularDist(Tile origin, int radius) {
		int index = getIndexOfTile(origin);
		List<Tile> neighbors = new ArrayList<Tile>();
		if (index == -1) {
			return neighbors;
		}
		Pos2D originPt = origin.getPos();
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i].getPos().distBetween(originPt) <= radius) {
				neighbors.add(tiles[i]);
			}
		}
		return neighbors;
	}

	private List<Tile> getNeighborsManhattanDist(Tile origin, int radius) {
		int index = getIndexOfTile(origin);
		List<Tile> neighbors = new ArrayList<Tile>();
		if (index == -1) {
			return neighbors;
		}

		for (int j = -radius; j <= radius; j++) {
			for (int k = -radius; k <= radius; k++) {
				int indexOfNeighbor = index + (k * cols) + (j);
				if (indexOfNeighbor >= tiles.length || indexOfNeighbor < 0) {
					continue;
				}
				int expectedCol = (index % cols) + j;
				int expectedRow = (int) (index / cols) + k;
				int actualCol = indexOfNeighbor % cols;
				int actualRow = indexOfNeighbor / cols;
				if (actualRow == expectedRow && actualCol == expectedCol) {
					neighbors.add(tiles[indexOfNeighbor]);
				}
			}
		}
		return neighbors;
	}

	private int getIndexOfTile(Tile t) {
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i].equals(t)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void draw(GraphicsContext g) {
		g.setFill(Color.DARKGRAY);
		g.fillRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());
		for (int i = 0; i < tiles.length; i++) {
			tiles[i].draw(g);
		}
	}

	@Override
	public Tile getTileAt(Pos2D pos) {
		if (!Util.isValidPos2D(pos, cols, rows)) {
			return null;
		}
		int x = (int) (Math.floor(pos.getX()));
		int y = (int) (Math.floor(pos.getY()));
		return tiles[cols * y + x];
	}

	@Override
	public Zone getZoneAt(Pos2D pos) {
		return getTileAt(pos).getZone();
	}

	@Override
	public City getCity() {
		// TODO Auto-generated method stub
		return null;
	}

}
