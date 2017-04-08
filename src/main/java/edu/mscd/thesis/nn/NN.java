package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.CityData;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public class NN implements AI{
	private Model<UserData, CityData> state;
	private TileMapper tileMap;
	private ZoneDecider zoneDecider;
	private ZoneMapper zoneMap;
	
	public NN(Model<UserData, CityData> state){
		this.state = ModelStripper.reducedCopy(state);
		this.zoneMap = new ZoneMapper(this.state);
		this.tileMap = new TileMapper(this.state);
		this.zoneDecider = new ZoneDecider(this.state);
		
	}

	@Override
	public UserData takeNextAction() {
		UserData zoneAction = this.zoneDecider.takeNextAction();
		ZoneType zoneType = zoneAction.getZoneSelection();
		int radius = zoneAction.getRadius();
		Pos2D[] locations = new Pos2D[this.state.getWorld().getTiles().length];
		double[] mapA = this.tileMap.getMapOfValues(this.state, zoneAction);
		double[] mapB = this.zoneMap.getMapOfValues(this.state, zoneAction);
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
		
		System.out.println("ZoneDecider picked:{"+zoneType+"}");
		if(minIndex==maxIndex){
			System.out.println("AI can not find ideal move to make");
			return null;
		}

		UserData fake = new UserData();
		fake.setClickLocation(locations[maxIndex]);
		fake.setZoneSelection(zoneType);
		fake.setRadius(radius);
		fake.setSquare(false);
		fake.setTakeStep(false);
		fake.setDrawFlag(true);
		fake.setAI(true);
		return fake;
	}


	@Override
	public void addCase(Model<UserData, CityData> prev, Model<UserData, CityData> current, UserData action, double userRating) {
		this.zoneDecider.addCase(state, prev, action, userRating);
		this.tileMap.addCase(state, prev, action, userRating);
		this.zoneMap.addCase(state, prev, action, userRating);
		
	}

	@Override
	public void setState(Model<UserData, CityData> state) {
		this.state = ModelStripper.reducedCopy(state);
		this.zoneDecider.setState(this.state);
		
	}

}
