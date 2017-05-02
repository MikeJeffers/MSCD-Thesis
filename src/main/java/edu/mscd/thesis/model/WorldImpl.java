package edu.mscd.thesis.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.TileUpdaterService;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.view.Selection;
import javafx.application.Platform;

public class WorldImpl implements World {
	private Tile[] tiles;
	private int cols, rows;
	private City city;
	private TileUpdaterService tileUpdater;

	private List<Observer<ModelData>> observers;

	private BlockingQueue<Action> queue = new LinkedBlockingQueue<Action>();
	private boolean updateCalled;
	private volatile boolean isRunning = true;
	private Lock lock;

	public WorldImpl(int sizeX, int sizeY) {
		this.lock = new ReentrantLock();
		this.observers = new ArrayList<Observer<ModelData>>();
		int size = sizeX * sizeY;
		tiles = new Tile[size];
		this.rows = sizeY;
		this.cols = sizeX;
		this.smoothWorldInit(Rules.WORLD_TILE_NOISE);
		this.city = new CityImpl(this);
		tileUpdater = new TileUpdaterService(this);
	}
	
	/**
	 * World created with smooth transitions between random mountain peaks and water bodies
	 */
	private void smoothWorldInit(int noise){
		Random r = new Random();
		TileType[] types = TileType.values();
		ZoneFactory zFact = new ZoneFactoryImpl();
		for (int i = 0; i < tiles.length; i++) {
			Pos2D p = new Pos2D((i % cols), (i / cols));
			Tile t = new TileImpl(p, TileType.FOREST, zFact);
			tiles[i] = t;
		}
		int totalCells = tiles.length;
		int numMountains = (int) Math.sqrt(rows+r.nextInt(rows));
		int numOceans = (int) Math.sqrt(rows+r.nextInt(rows));
		int maxSize = (int) Util.boundValue(Math.sqrt(cols)/2, 1, 5);
		for(int i=0; i<numMountains; i++){
			int location = r.nextInt(totalCells);
			Tile t = new TileImpl(tiles[location].getPos(), TileType.MOUNTAIN, zFact);
			tiles[location] = t;	
			List<Tile> neighbors = Util.getNeighborsCircularDist(t, tiles, r.nextInt(maxSize));
			for(Tile n: neighbors){
				int index = Util.getIndexOf(n, this.tiles);
				tiles[index] = new TileImpl(tiles[index].getPos(), TileType.MOUNTAIN, zFact);
			}
		}
		for(int i=0; i<numOceans; i++){
			int location = r.nextInt(totalCells);
			Tile t = new TileImpl(tiles[location].getPos(), TileType.OCEAN, zFact);
			tiles[location] = t;	
			List<Tile> neighbors = Util.getNeighborsCircularDist(t, tiles, r.nextInt(maxSize));
			for(Tile n: neighbors){
				int index = Util.getIndexOf(n, this.tiles);
				tiles[index] = new TileImpl(tiles[index].getPos(), TileType.OCEAN, zFact);
			}
		}
		List<Tile> smoothed = new ArrayList<Tile>();
		for (int i = 0; i < tiles.length; i++) {
			double distToMtn = distanceTo(tiles[i].getPos(), TileType.MOUNTAIN);
			double distToOcean = distanceTo(tiles[i].getPos(), TileType.OCEAN);
			double ratio = distToOcean/(distToOcean+distToMtn+0.001);
			int typeSelection = (int) Math.floor(ratio*(types.length));
			typeSelection+=(int)Util.getRandomBetween(-noise, noise+1);
			typeSelection = (int) Util.boundValue(typeSelection, 0, types.length);
			typeSelection = (typeSelection)%types.length;
			Tile t = new TileImpl(tiles[i].getPos(), types[typeSelection], zFact);
			smoothed.add(t);
		}
		for(int i=0; i<tiles.length; i++){
			tiles[i] = smoothed.get(i);
		}
		
	}
	
	private double distanceTo(Pos2D origin, TileType tileOfThisType){
		double minDist = Double.MAX_VALUE;
		for(int i=0; i<this.tiles.length; i++){
			if(tiles[i].getType()==tileOfThisType){
				double dist = origin.distBetween(tiles[i].getPos());
				if(dist<minDist){
					minDist = dist;
				}
			}
		}
		return minDist;
	}
	

	@Override
	public void run() {
		while (isRunning) {
			Action msg;
			while ((msg = queue.poll()) != null) {
				this.lock.lock();
				try{
					processAction(msg);
				}finally{
					lock.unlock();
				}
			}
			if(updateCalled){
				this.lock.lock();
				try{
					updateTasks();
				}finally{
					lock.unlock();
				}
				updateCalled=false;
			}
		}

	}

	private void processAction(Action data) {
		this.setAllZonesAround(data.getTarget(), data.getZoneType(), data.getRadius(), data.isSquare(), data.isMove());
	}

	
	private void updateTasks(){
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
		Platform.runLater(new Runnable() {
            @Override public void run() {
            	notifyObserver(city.getData());
            }
        });
	}
	
	@Override
	public void update() {
		this.updateCalled =true;
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
			}
			reZone.setSelection(new Selection(true, zt));
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
		try {
			this.queue.put(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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

	@Override
	public void halt() {
		this.isRunning = false;
		
	}

	@Override
	public Lock getLock() {
		return this.lock;
	}



}
