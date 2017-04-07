package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public class NN implements AI{
	private Model state;
	private TileMapper tileMap;
	private ZoneDecider zoneDecider;
	private ZoneMapper zoneMap;
	
	public NN(Model state){
		this.state = ModelStripper.reducedCopy(state);
		this.zoneMap = new ZoneMapper(this.state);
		this.tileMap = new TileMapper(this.state);
		this.zoneDecider = new ZoneDecider(this.state);
		
	}

	@Override
	public void setWorldState(Model state) {
		this.state = ModelStripper.reducedCopy(state);
		this.tileMap.setWorldState(this.state);
		this.zoneMap.setWorldState(this.state);
		this.zoneDecider.setWorldState(this.state);
	}

	@Override
	public UserData takeNextAction() {
		ZoneType zoneAction = this.zoneDecider.takeNextAction().getZoneSelection();
		this.tileMap.setZoneOfAction(zoneAction);
		this.zoneMap.setZoneOfAction(zoneAction);
		Pos2D[] locations = new Pos2D[this.state.getWorld().getTiles().length];
		double[] mapA = this.tileMap.getMapOfValues();
		double[] mapB = this.zoneMap.getMapOfValues();
		double[] combined = new double[mapA.length];
		double[] src = new double[]{0,2.0};
		double[] targ = new double[]{0, 1.0};
		int maxIndex = 0;
		int minIndex = 0;
		double maxScore = 0;
		double minScore = Rules.MAX;
		assert(mapA.length==mapB.length && locations.length==mapA.length);
		for(int i=0; i<mapA.length; i++){
			locations[i] = this.state.getWorld().getTiles()[i].getPos();
			double value =  Util.mapValue(mapA[i]+mapB[i], src, targ);
			combined[i]=value;
			if(value<minScore){
				minScore = value;
				minIndex = i;
			}
			if(value>maxScore){
				maxScore = value;
				maxIndex = i;
			}
		}
		System.out.println("Possible actions based on Mapped Score domain["+combined[minIndex]+","+combined[maxIndex]+"]");
		System.out.print("Best move:{");
		System.out.print(locations[maxIndex]);
		System.out.println();
		System.out.print("Worst move:{");
		System.out.print(locations[minIndex]);
		System.out.println();
		
		System.out.println("ZoneDecider picked:{"+zoneAction+"}");
		if(minIndex==maxIndex){
			System.out.println("AI can not find ideal move to make");
			return null;
		}

		UserData fake = new UserData();
		fake.setClickLocation(locations[maxIndex]);
		fake.setZoneSelection(zoneAction);
		fake.setRadius(1);
		fake.setSquare(false);
		fake.setTakeStep(false);
		fake.setDrawFlag(true);
		fake.setAI(true);
		return fake;
	}

	@Override
	public void addCase(Model state, Model prev, UserData action) {
		this.zoneDecider.addCase(state, prev, action);
		this.tileMap.addCase(state, prev, action);
		this.zoneMap.addCase(state, prev, action);
		
	}

}
