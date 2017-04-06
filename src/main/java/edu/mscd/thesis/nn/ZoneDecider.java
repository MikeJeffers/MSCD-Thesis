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

public class ZoneDecider implements AI{
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
		
		double[][] input = new double[8][4];
		double[][] output = new double[8][4];
		for(int i=0; i<ZoneType.values().length; i++){
			input[i] = WorldRepresentation.getZoneAsVector(ZoneType.values()[i]);
			output[i]=WorldRepresentation.getZoneAsVector(ZoneType.values()[i]);
		}
		input[4][0] = 0.5;//R
		input[4][1] = 0.5;//C
		input[4][2] = 1.0;//I
		input[4][3] = 0.0;//0
		
		output[4][0] = 0.0;//R
		output[4][1] = 0.0;//C
		output[4][2] = 1.0;//I
		output[4][3] = 0.0;//0
		
		input[5][0] = 0.0;//R
		input[5][1] = 1.0;//C
		input[5][2] = 1.0;//I
		input[5][3] = 0.0;//0
		
		output[5][0] = 0.0;//R
		output[5][1] = 0.0;//C
		output[5][2] = 1.0;//I
		output[5][3] = 0.0;//0
		
		input[6][0] = 0.0;//R
		input[6][1] = 0.5;//C
		input[6][2] = 1.0;//I
		input[6][3] = 0.0;//0
		
		output[6][0] = 0.0;//R
		output[6][1] = 0.0;//C
		output[6][2] = 1.0;//I
		output[6][3] = 0.0;//0
		
		input[7][0] = 0.2;//R
		input[7][1] = 0.4;//C
		input[7][2] = 0.4;//I
		input[7][3] = 1.0;//0
		
		output[7][0] = 0.0;//R
		output[7][1] = 0.0;//C
		output[7][2] = 1.0;//I
		output[7][3] = 0.0;//0
		

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

	private static int getOutput(double[] input) {
		MLData data = new BasicMLData(input);
		return network.winner(data);
	}

	@Override
	public void setWorldState(Model state) {
		this.state = ModelStripper.reducedCopy(state);

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
		
		ZoneType AIselection = ZoneType.values()[getOutput(input)];

		UserData fake = new UserData();
		fake.setZoneSelection(AIselection);
		fake.setAI(true);
		return fake;
		

	}

	@Override
	public void addCase(Model state, Model prev, UserData action) {
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


	
	

}
