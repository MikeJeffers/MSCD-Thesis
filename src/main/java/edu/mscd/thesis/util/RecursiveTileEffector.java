package edu.mscd.thesis.util;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.zones.ZoneType;

public class RecursiveTileEffector extends RecursiveTask<World>{
	private static final long serialVersionUID = 1L;
	int low;
    int high;
    World w;
	
	public RecursiveTileEffector(World w, int lo, int hi){
		this.w = w;
		this.low = lo;
		this.high = hi;
	}


	@Override
	protected World compute() {
		int diff = high-low;
		if(diff<Util.MAX_SEQUENTIAL){
			for(int i=low; i<high; i++){
				Tile t = w.getTiles()[i];
				ZoneType zt = t.getZone().getZoneType();
				if(zt==ZoneType.INDUSTRIAL){
					pollute(t);
				}else if(zt==ZoneType.RESIDENTIAL||zt==ZoneType.COMMERICAL){
					growLandValue(t);
				}
			}
			return w;
		}
		int mid = low + (high - low) / 2;
		RecursiveTileEffector left  = new RecursiveTileEffector(w, low, mid);
		RecursiveTileEffector right = new RecursiveTileEffector(w, mid, high);
        left.fork();
        right.compute();
        left.join();
        return w;
	}
	
	private void pollute(Tile origin){
		int intensity = origin.getZone().getBuilding().getDensity().getDensityLevel()+1;
		List<Tile>tilesInRange = Util.getNeighborsCircularDist(origin, w.getTiles(), intensity);
		for(Tile t: tilesInRange){
			double dist = Math.max(t.getPos().distBetween(origin.getPos()), 0.1);//TODO arbitrary minimum factor
			double pollutionAmount = Rules.POLLUTION_UNIT*(intensity*(1-(dist/intensity)));
			t.pollute(pollutionAmount);
			t.modifyLandValue(-pollutionAmount);
		}
	}
	
	private void growLandValue(Tile origin){
		int intensity = origin.getZone().getBuilding().getDensity().getDensityLevel()+1;
		List<Tile>tilesInRange = Util.getNeighborsCircularDist(origin, w.getTiles(), intensity);
		for(Tile t: tilesInRange){
			double dist = Math.max(t.getPos().distBetween(origin.getPos()), 0.1);//TODO arbitrary minimum factor
			double landValueIncr = Rules.LANDVALUE_UNIT*(intensity*(1-(dist/intensity)));
			t.modifyLandValue(landValueIncr);
		}
	}
	


}
