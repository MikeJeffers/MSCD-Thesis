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
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;

public class ZoneDecider implements AI{
	private Model state;
	
	public static BasicNetwork network = new BasicNetwork();
	public static MLDataSet DATASET = new BasicMLDataSet();
	
	
	
	public ZoneDecider(Model initialState){
		this.state = ModelStripper.reducedCopy(initialState);
		initNetwork();
		initTrainingDataSet();
		trainBackProp();
	}
	
	private void initTrainingDataSet(){
		
		double[][] input = new double[2][8];
		for(int i=0; i<ZoneType.values().length; i++){
			input[0][i+4]= Rules.getDemandForZoneType(ZoneType.values()[i], this.state.getWorld());
			input[0][i] = 0;
		}
		for(int i=0; i<ZoneType.values().length; i++){
			input[1][i+4]= Rules.getDemandForZoneType(ZoneType.values()[i], this.state.getWorld());
			input[1][i] = 0;
		}
		input[1][2] = 1;
		double[][] idealout = new double[][]{{Rules.score(this.state)}, {Rules.score(this.state)*1.1}};

		for (int i = 0; i < input.length; i++) {
			MLData trainingIn = new BasicMLData(input[i]);
			MLData idealOut = new BasicMLData(idealout[i]);
			DATASET.add(trainingIn, idealOut);
		}
	}

	private void trainBackProp() {
		Backpropagation train = new Backpropagation(network, DATASET);
		int epoch = 1;

		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > 0.01 && epoch < 100);
		train.finishTraining();

		Encog.getInstance().shutdown();
	}

	private void trainResilient() {
		ResilientPropagation train = new ResilientPropagation(network, DATASET);
		int epoch = 1;

		do {
			train.iteration();
			// System.out.println("Epoch #" + epoch + " Error:" +
			// train.getError());
			epoch++;
		} while (train.getError() > 0.01 && epoch < 50);
		train.finishTraining();

		Encog.getInstance().shutdown();
	}

	private void initNetwork() {
		network.addLayer(new BasicLayer(null, true, 8));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 15));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		network.getStructure().finalizeStructure();
		network.reset();
	}

	private static double getOutput(double[] input) {
		MLData data = new BasicMLData(input);
		MLData out = network.compute(data);
		double[] output = out.getData();
		return output[0];
	}

	@Override
	public void setWorldState(Model state) {
		this.state = ModelStripper.reducedCopy(state);

	}

	@Override
	public UserData takeNextAction() {
		double[] outArray = new double[ZoneType.values().length];
		for(ZoneType zt: ZoneType.values()){
			double[] input = new double[8];
			for(int i=0; i<ZoneType.values().length; i++){
				input[i+4]= Rules.getDemandForZoneType(ZoneType.values()[i], state.getWorld());
				input[i] = 0;
			}
			input[zt.ordinal()]=1;
			outArray[zt.ordinal()] = getOutput(input);
		}
		ZoneType bestChoice = ZoneType.EMPTY;
		double maxValue = 0;
		for(ZoneType zt: ZoneType.values()){
			if(outArray[zt.ordinal()]>maxValue){
				bestChoice = zt;
				maxValue = outArray[zt.ordinal()];
			}
		}

		UserData fake = new UserData();
		fake.setZoneSelection(bestChoice);
		fake.setAI(true);
		return fake;
		

	}

	@Override
	public void addCase(Model state, Model prev, UserData action) {
		double currentScore = Rules.score(state);
		double prevScore = Rules.score(prev);
		int zoneChoice = action.getZoneSelection().ordinal();
		double[] input = new double[8];
		for(int i=0; i<ZoneType.values().length; i++){
			input[i+4]= Rules.getDemandForZoneType(ZoneType.values()[i], prev.getWorld());
			input[i] = 0;
		}
		input[zoneChoice] = 1;
		MLData trainingIn = new BasicMLData(input);
		MLData idealOut = new BasicMLData(new double[] { currentScore });
		DATASET.add(trainingIn, idealOut);
		this.trainBackProp();
		// this.trainResilient();
		if (currentScore > prevScore) {
			System.out.println("AI move improvedScore! " + currentScore + " from " + prevScore);

		} else {
			System.out.println("AI move dropped score =( " + currentScore + " from " + prevScore);
		}

	}


	
	

}
