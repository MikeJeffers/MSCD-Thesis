package edu.mscd.thesis.nn;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationCompetitive;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.JordanPattern;

import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.City;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public class ZoneDecider implements Actor, Learner{
	private Model state;
	
	public final static BasicNetwork network = new BasicNetwork();
	public final static MLDataSet DATASET = new BasicMLDataSet();
	
	
	
	public ZoneDecider(Model initialState){
		this.state = ModelStripper.reducedCopy(initialState);
		initNetwork();
		initTrainingDataSet();
		trainResilient();
	}
	
	
	private void initTrainingDataSet(){
		
		double[][] input = new double[15][4];
		double[][] output = new double[15][4];
		for(int i=0; i<ZoneType.values().length; i++){
			input[i] = WorldRepresentation.getZoneAsVector(ZoneType.values()[i]);
			output[i]=WorldRepresentation.getZoneAsVector(ZoneType.values()[i]);
		}
		input[4][0] = 0.77;//R
		input[4][1] = 0.77;//C
		input[4][2] = 1.0;//I
		output[4] = WorldRepresentation.getZoneAsVector(ZoneType.INDUSTRIAL);

		input[5][0] = 0.55;//R
		input[5][1] = 1.0;//C
		input[5][2] = 1.0;//I
		output[5] = WorldRepresentation.getZoneAsVector(ZoneType.INDUSTRIAL);
		
		input[6][0] = 0.0;//R
		input[6][1] = 0.5;//C
		input[6][2] = 0.6;//I
		output[6] = WorldRepresentation.getZoneAsVector(ZoneType.INDUSTRIAL);
		
		input[7][0] = 0.2;//R
		input[7][1] = 0.4;//C
		input[7][2] = 0.4;//I
		output[7] = WorldRepresentation.getZoneAsVector(ZoneType.INDUSTRIAL);
		
		input[8][0] = 0.55;//R
		input[8][1] = 0.4;//C
		input[8][2] = 0.4;//I
		output[8] = WorldRepresentation.getZoneAsVector(ZoneType.RESIDENTIAL);
		
		input[9][0] = 0.85;//R
		input[9][1] = 0.7;//C
		input[9][2] = 0.7;//I
		output[9] = WorldRepresentation.getZoneAsVector(ZoneType.RESIDENTIAL);
		
		input[10][0] = 0.05;//R
		input[10][1] = 0.15;//C
		input[10][2] = 0.0;//I
		output[10] = WorldRepresentation.getZoneAsVector(ZoneType.COMMERICAL);
		
		input[11][0] = 0.05;//R
		input[11][1] = 0.55;//C
		input[11][2] = 0.45;//I
		output[11] = WorldRepresentation.getZoneAsVector(ZoneType.COMMERICAL);
		
		input[12][0] = 0.65;//R
		input[12][1] = 0.55;//C
		input[12][2] = 0.45;//I
		output[12] = WorldRepresentation.getZoneAsVector(ZoneType.RESIDENTIAL);
		
		input[13][0] = 0.05;//R
		input[13][1] = 0.10;//C
		input[13][2] = 0.15;//I
		output[13] = WorldRepresentation.getZoneAsVector(ZoneType.INDUSTRIAL);
		
		input[14][0] = 0.15;//R
		input[14][1] = 0.25;//C
		input[14][2] = 0.35;//I
		output[14] = WorldRepresentation.getZoneAsVector(ZoneType.INDUSTRIAL);
		

		for (int i = 0; i < input.length; i++) {
			MLData trainingIn = new BasicMLData(input[i]);
			MLData idealOut = new BasicMLData(output[i]);
			DATASET.add(trainingIn, idealOut);
		}
	}


	private void trainResilient() {
		ResilientPropagation train = new ResilientPropagation(network, DATASET);
		int epoch = 1;

		do {
			train.iteration();
			epoch++;
		} while (train.getError() > 0.01 && epoch < 50);
		train.finishTraining();

		Encog.getInstance().shutdown();
	}

	private void initNetwork() {
		network.addLayer(new BasicLayer(null, true, 4));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 12));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 7));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 4));
		network.getStructure().finalizeStructure();
		network.reset();
	}

	private int getZoneIndex(double[] input) {
		MLData data = new BasicMLData(input);
		return network.winner(data);
	}


	@Override
	public UserData takeNextAction() {
		City city = state.getWorld().getCity();
		double r = city.residentialDemand();
		double c = city.commercialDemand();
		double indy = city.industrialDemand();
		System.out.println("residential demand:"+r);
		System.out.println("commercial demand:"+c);
		System.out.println("industrial demand:"+indy);
		double[] input = new double[4];
		for(int i=0; i<ZoneType.values().length; i++){
			input[i]= Rules.getDemandForZoneType(ZoneType.values()[i], state.getWorld());
			System.out.println(ZoneType.values()[i]+" "+ input[i]);
		}
		
		MLData data = new BasicMLData(input);
		int index = network.winner(data);
		double indexValue = network.compute(data).getData(index);
		
		int strength = (int)Math.round(Util.mapValue(indexValue, new double[]{0,1}, new double[]{0,3}));
		
		ZoneType AIselection = ZoneType.values()[getZoneIndex(input)];

		UserData fake = new UserData();
		fake.setZoneSelection(AIselection);
		fake.setRadius(strength);
		fake.setAI(true);
		return fake;
		

	}

	@Override
	public void addCase(Model prev, Model current, UserData action, double userRating) {
		double currentScore = Rules.score(state);
		double prevScore = Rules.score(prev);
		
		//Train on positive cases,online learning
		if (currentScore > prevScore) {
			/*
			int zoneChoice = action.getZoneSelection().ordinal();
			double[] input = new double[4];
			double[] output = new double[4];
			for(int i=0; i<ZoneType.values().length; i++){
				input[i]= Rules.getDemandForZoneType(ZoneType.values()[i], prev.getWorld());
			}
			output[zoneChoice] = 1;
			MLData trainingIn = new BasicMLData(input);
			MLData idealOut = new BasicMLData(output);
			DATASET.add(trainingIn, idealOut);
			this.trainResilient();
			*/
			System.out.println("AI move improvedScore! " + currentScore + " from " + prevScore);

		} else {
			System.out.println("AI move dropped score =( " + currentScore + " from " + prevScore);
		}

	}



	@Override
	public void setState(Model state) {
		this.state = state;
		
	}


	
	

}
