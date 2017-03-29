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
		
		double[][] input = new double[4][4];
		double[][] output = new double[4][4];
		input[0][0] = 1.0;//R
		input[0][1] = 0.0;//C
		input[0][2] = 0.0;//I
		input[0][3] = 0.0;//0
		
		output[0][0] = 1.0;//R
		output[0][1] = 0.0;//C
		output[0][2] = 0.0;//I
		output[0][3] = 0.0;//0
		
		input[1][0] = 0.0;//R
		input[1][1] = 1.0;//C
		input[1][2] = 0.0;//I
		input[1][3] = 0.0;//0
		
		output[1][0] = 0.0;//R
		output[1][1] = 1.0;//C
		output[1][2] = 0.0;//I
		output[1][3] = 0.0;//0
		
		input[2][0] = 0.0;//R
		input[2][1] = 0.0;//C
		input[2][2] = 1.0;//I
		input[2][3] = 0.0;//0
		
		output[2][0] = 0.0;//R
		output[2][1] = 0.0;//C
		output[2][2] = 1.0;//I
		output[2][3] = 0.0;//0
		
		input[3][0] = 0.0;//R
		input[3][1] = 0.0;//C
		input[3][2] = 0.0;//I
		input[3][3] = 1.0;//0
		
		output[3][0] = 0.0;//R
		output[3][1] = 0.0;//C
		output[3][2] = 0.0;//I
		output[3][3] = 1.0;//0
		

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
