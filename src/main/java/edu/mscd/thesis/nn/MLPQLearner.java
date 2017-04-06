package edu.mscd.thesis.nn;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;

public class MLPQLearner implements AI {
	/**
	 * input layer is 9-cell moore's neighborhood via WorldRepresentation
	 * ZoneVector as STATE And single ACTION repr'd as a single ZoneVector a
	 * Q-score is produced then to indicate if the move will generally help or
	 * hurt the world score
	 * 
	 */
	private static final int ZONETYPES = ZoneType.values().length;
	private static final int INPUT_LAYER_SIZE = 9 * ZONETYPES + ZONETYPES;
	private static final int OUTPUT_LAYER_SIZE = 1;
	public static final BasicNetwork network = new BasicNetwork();
	public static final MLDataSet DATASET = new BasicMLDataSet();
	private Model state;

	private ZoneDecider zoner;

	public MLPQLearner(Model state) {
		this.state = ModelStripper.reducedCopy(state);
		this.zoner = new ZoneDecider(this.state);
		initNetwork();
		initTraining();
		trainResilient();
	}

	private void initNetwork() {
		network.addLayer(new BasicLayer(null, true, INPUT_LAYER_SIZE));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, (INPUT_LAYER_SIZE * 3) / 2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, (INPUT_LAYER_SIZE / 4)));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, OUTPUT_LAYER_SIZE));
		network.getStructure().finalizeStructure();
		network.reset();
	}

	private void initTraining() {
		double[][] input = new double[10][INPUT_LAYER_SIZE];
		double[][] output = new double[10][OUTPUT_LAYER_SIZE];
		double[] r = WorldRepresentation.getZoneAsVector(ZoneType.RESIDENTIAL);
		double[] c = WorldRepresentation.getZoneAsVector(ZoneType.COMMERICAL);
		double[] indy = WorldRepresentation.getZoneAsVector(ZoneType.INDUSTRIAL);
		double[] empty = WorldRepresentation.getZoneAsVector(ZoneType.EMPTY);
		input[0] = this.constructSampleInput(r, empty);
		output[0] = new double[] { 0 };
		input[1] = this.constructSampleInput(c, empty);
		output[1] = new double[] { 0 };
		input[2] = this.constructSampleInput(indy, empty);
		output[2] = new double[] { 0.1 };
		input[3] = this.constructSampleInput(empty, empty);
		output[3] = new double[] { 0 };
		input[4] = this.constructSampleInput(r, indy);
		output[4] = new double[] { 0.1 };
		input[5] = this.constructSampleInput(indy, r);
		output[5] = new double[] { 0.9 };
		input[6] = this.constructSampleInput(empty, r);
		output[6] = new double[] { 1 };
		input[7] = this.constructSampleInput(empty, c);
		output[7] = new double[] { 1 };
		input[8] = this.constructSampleInput(empty, indy);
		output[8] = new double[] { 1 };
		input[9] = this.constructSampleInput(indy, indy);
		output[9] = new double[] { 1 };

		for (int i = 0; i < input.length; i++) {
			MLData trainingIn = new BasicMLData(input[i]);
			MLData idealOut = new BasicMLData(output[i]);
			DATASET.add(trainingIn, idealOut);
		}

	}

	private double[] constructSampleInput(double[] zoneVector, double[] action) {
		double[] inputSet = new double[INPUT_LAYER_SIZE];
		for (int cell = 0; cell < 9; cell++) {
			for (int v = 0; v < zoneVector.length; v++) {
				inputSet[cell * zoneVector.length + v] = zoneVector[v];
			}
		}
		for (int i = 0; i < action.length; i++) {
			inputSet[inputSet.length - action.length + i] = action[i];
		}
		return inputSet;
	}

	private void trainResilient() {
		ResilientPropagation train = new ResilientPropagation(network, DATASET);
		int epoch = 1;

		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > 0.01 && epoch < 50);
		train.finishTraining();

		Encog.getInstance().shutdown();
	}

	@Override
	public void setWorldState(Model state) {
		this.state = ModelStripper.reducedCopy(state);
		this.zoner.setWorldState(this.state);

	}

	private double getOutput(double[] input) {
		MLData output = network.compute(new BasicMLData(input));
		return output.getData()[0];
	}

	@Override
	public UserData takeNextAction() {
		ZoneType zoneAction = this.zoner.takeNextAction().getZoneSelection();
		double[] zoneVector = WorldRepresentation.getZoneAsVector(zoneAction);
		World w = this.state.getWorld();
		Tile[] tiles = w.getTiles();
		double[] map = new double[tiles.length];
		Pos2D[] locations = new Pos2D[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			Pos2D p = tiles[i].getPos();
			locations[i] = p;
			double[] input = addActionVector(getInputAroundTile(w, p), zoneVector);
			double output = getOutput(input);
			map[i]=output;
		}
		
		int maxIndex = 0;
		int minIndex = 0;
		double maxScore = 0;
		double minScore = Rules.MAX;
		for(int i=0; i<map.length; i++){
			if(map[i]<minScore){
				minScore = map[i];
				minIndex = i;
			}
			if(map[i]>maxScore){
				maxScore = map[i];
				maxIndex = i;
			}
		}
		
		System.out.println("Possible actions based on Mapped Score domain["+map[minIndex]+","+map[maxIndex]+"]");
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
		fake.setSquare(true);
		fake.setTakeStep(false);
		fake.setDrawFlag(true);
		fake.setAI(true);
		return fake;
	}

	@Override
	public void addCase(Model state, Model prev, UserData action) {
		this.zoner.addCase(state, prev, action);

		Pos2D pos = action.getClickLocation();
		ZoneType zoneAct = action.getZoneSelection();
		double prevScore = Rules.score(prev.getWorld().getTileAt(pos));
		double currentScore = Rules.score(state.getWorld().getTileAt(pos));
		double normalizedScoreDiff = ((currentScore - prevScore) / 2.0) + 0.5;
		double[] input = addActionVector(getInputAroundTile(prev.getWorld(), pos),
				WorldRepresentation.getZoneAsVector(zoneAct));
		learn(input, new double[] { normalizedScoreDiff });

	}

	private void learn(double[] input, double[] output) {
		MLData trainingIn = new BasicMLData(input);
		MLData idealOut = new BasicMLData(output);
		DATASET.add(trainingIn, idealOut);
		trainResilient();
	}

	private double[] getInputAroundTile(World w, Pos2D p) {
		Tile[] tiles = getNeighbors(w, p);
		double[] vals = new double[tiles.length * ZONETYPES];
		for (int i = 0; i < tiles.length; i++) {
			double[] zVector = WorldRepresentation.getTileAsZoneVector(tiles[i]);
			int index = i * ZONETYPES;
			for (int j = 0; j < ZONETYPES; j++) {
				vals[index + j] = zVector[j];
			}

		}
		return vals;
	}

	private double[] addActionVector(double[] inputVector, double[] action) {
		double[] enlongated = new double[inputVector.length + action.length];
		for (int i = 0; i < action.length; i++) {
			enlongated[i] = inputVector[i];
		}
		for (int i = 0; i < action.length; i++) {
			enlongated[enlongated.length - action.length + i] = action[i];
		}
		return enlongated;
	}

	private Tile[] getNeighbors(World w, Pos2D p) {
		Tile[] tiles = new Tile[9];
		int index = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				Pos2D nLoc = new Pos2D(p.getX() + i, p.getY() + j);
				tiles[index] = w.getTileAt(nLoc);
				index++;
			}
		}
		return tiles;
	}

}
