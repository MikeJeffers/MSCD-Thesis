package edu.mscd.thesis.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.controller.ModelData;
import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.city.City;
import edu.mscd.thesis.model.city.CityImpl;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneFactory;
import edu.mscd.thesis.model.zones.ZoneFactoryImpl;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.TileUpdaterService;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.view.Selection;

public class WorldImpl implements World {
	private Tile[] tiles;
	private int cols, rows;
	private City city;
	private TileUpdaterService tileUpdater;

	private List<Observer<ModelData>> observers;

	public WorldImpl(int sizeX, int sizeY) {
		this.observers = new ArrayList<Observer<ModelData>>();
		int size = sizeX * sizeY;
		tiles = new Tile[size];
		this.rows = sizeY;
		this.cols = sizeX;
		this.worldInit();
		this.city = new CityImpl(this);
		tileUpdater = new TileUpdaterService(this);
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
		// System.out.println(city);

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

		tileUpdater.runUpdates();
		city.update();
		this.notifyObserver(city.getData());
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
	public boolean setAllZonesAround(Pos2D pos, ZoneType zt, int radius, boolean squareSelect, boolean commitAction) {
		clearSelectionOnWorld();
		Tile t = this.getTileAt(pos);
		if (t == null) {
			return false;
		}
		List<Tile> tilesInRange;
		if (squareSelect) {
			tilesInRange = Util.getNeighborsManhattanDist(t, tiles, radius, cols, rows);
		} else {
			tilesInRange = Util.getNeighborsCircularDist(t, tiles, radius);
		}

		for (Tile reZone : tilesInRange) {
			if (commitAction) {
				reZone.setZone(zt);
			} else {
				reZone.setSelection(new Selection(true, zt));
			}

		}
		return true;
	}

	private void clearSelectionOnWorld() {
		for (Tile t : tiles) {
			t.setSelection(new Selection(false, t.getZoneType()));
		}
	}

	@Override
	public Tile[] getTiles() {
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
	public void notifyNewData(Action data) {
		this.setAllZonesAround(data.getTarget(), data.getZoneType(), data.getRadius(), data.isSquare(), data.isMove());
	}

	@Override
	public World getWorld() {
		return this;
	}

	@Override
	public int width() {
		return this.cols;
	}

	@Override
	public int height() {
		return this.rows;
	}

	@Override
	public void attachObserver(Observer<ModelData> obs) {
		this.observers.add(obs);

	}

	@Override
	public void detachObserver(Observer<ModelData> obs) {
		this.observers.remove(obs);

	}

	@Override
	public void notifyObserver(ModelData data) {
		for (Observer<ModelData> o : this.observers) {
			o.notifyNewData(data);
		}

	}

	@Override
	public void setOverlay(double[] data) {
		if (data == null || data.length != this.tiles.length) {
			System.err.println("length mismatch");
			return;
		}
		for (int i = 0; i < tiles.length; i++) {
			tiles[i].setOverlayValue(data[i]);
		}

	}

	@Override
	public void setSelected(Selection[] selections) {
		if (selections == null || selections.length != this.tiles.length) {
			System.err.println("length mismatch");
			return;
		}
		for (int i = 0; i < tiles.length; i++) {
			tiles[i].setSelection(selections[i]);
		}
		
	}


}
