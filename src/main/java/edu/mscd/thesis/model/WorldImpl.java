package edu.mscd.thesis.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.mscd.thesis.controller.UserData;
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
	private City city;

	public WorldImpl(int sizeX, int sizeY) {
		int size = sizeX * sizeY;
		tiles = new Tile[size];
		this.rows = sizeY;
		this.cols = sizeX;
		this.worldInit();
		this.city = new CityImpl();
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
		//System.out.println(city);

		// TODO
		// iterate through all zones: incr/decr value based on conditions
		// Residential gets new occupants if there is residential demand;
		// --demand when home found for new citizen
		// Residents without jobs, seek nearest place of employment (building in
		// C or I)
		// New industrial buildings only expand if there is demand; if demand
		// met -- demand
		// same for commerce

		Collection<Person> homeless = this.city.getHomeless();
		Collection<Person> unemployed = this.city.getUnemployed();

		if (!homeless.isEmpty()) {
			for (Person p : homeless) {
				Tile tile = tiles[Util.getRandomBetween(0, tiles.length)];
				if (p.getWork() != null) {
					Pos2D workLocation = p.getWork().getPos();
					tile = this.getTileAt(workLocation);
				}
				Building b = this.findClosestOpenHome(tile);
				if (b != null) {
					b.addOccupant(p);
				}
			}
		}

		if (!unemployed.isEmpty()) {
			for (Person p : unemployed) {
				Tile tile = tiles[Util.getRandomBetween(0, tiles.length)];
				if (p.getHome() != null) {
					Pos2D homeLocation = p.getHome().getPos();
					tile = this.getTileAt(homeLocation);
				}
				Building b = this.findClosestUnfilledJob(tile);
				if (b != null) {
					b.addOccupant(p);
				}
			}
		}

		for (int i = 0; i < tiles.length; i++) {
			tiles[i].update();
		}
		city.update();

	}

	private Building findClosestOpenHome(Tile t) {
		double minDist = Double.MAX_VALUE;
		Building bestCandidate = null;
		Pos2D origin = t.getPos();
		for (int i = 0; i < tiles.length; i++) {
			if (!tiles[i].equals(t)) {
				ZoneType type = tiles[i].getZone().getZoneType();
				if (type == ZoneType.RESIDENTIAL) {
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
	public Tile[] getTiles(){
		return this.tiles;
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
		return this.city;
	}

	@Override
	public void userStateChange(UserData userData) {
		this.setAllZonesAround(userData.getClickLocation(), userData.getZoneSelection(), userData.getRadius(), userData.isSquare());
		
	}

	@Override
	public World getWorld() {
		return this;
	}

}
