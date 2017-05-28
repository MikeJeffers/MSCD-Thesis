package edu.mscd.thesis.model.tiles;

import java.util.concurrent.RecursiveTask;

import edu.mscd.thesis.util.Util;

public class RecursiveTileUpdater extends RecursiveTask<Tile[]>{
	private static final long serialVersionUID = 1L;
	int low;
    int high;
    Tile[] tiles;
	
	public RecursiveTileUpdater(Tile[] tileArray, int lo, int hi){
		this.tiles = tileArray;
		this.low = lo;
		this.high = hi;
	}


	@Override
	protected Tile[] compute() {
		int diff = high-low;
		if(diff<Util.MAX_SEQUENTIAL){
			for(int i=low; i<high; i++){
				tiles[i].update();
			}
			return tiles;
		}
		int mid = low + (high - low) / 2;
		RecursiveTileUpdater left  = new RecursiveTileUpdater(tiles, low, mid);
		RecursiveTileUpdater right = new RecursiveTileUpdater(tiles, mid, high);
        left.fork();
        tiles = right.compute();
        tiles = left.join();
        return tiles;
	}

}
