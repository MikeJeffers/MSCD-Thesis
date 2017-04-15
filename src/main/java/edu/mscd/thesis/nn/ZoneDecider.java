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
import edu.mscd.thesis.model.city.CityData;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.ModelToVec;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

/**
 * Neural net that produces ZoneType for next-actions, based on current CityData
 * state MLP Q-Learner Input-layer: CityData vector(state), ZoneType vector
 * (action) Output-layer: Q-value of state-action pair All 4 outputs across all
 * 4 possible actions are compared, highest is selected.
 * 
 * @author Mike
 */
public class ZoneDecider implements Actor, Learner {
	private Model<UserData, CityData> state;

	private static final BasicNetwork network = new BasicNetwork();
	private static final MLDataSet DATASET = new BasicMLDataSet();
	private static final int INPUT_LAYER_SIZE = CityProperty.values().length + ZoneType.values().length;
	private static final int OUTPUT_LAYER_SIZE = 1;

	public ZoneDecider(Model<UserData, CityData> initialState) {
		this.state = ModelStripper.reducedCopy(initialState);
		initNetwork();
		initTraining();
		trainResilient();
	}

	private void initTraining() {
		double[][] input = new double[28][INPUT_LAYER_SIZE];
		double[][] output = new double[28][OUTPUT_LAYER_SIZE];
		int i = 0;
		CityData dataVec = new CityData();
		for (CityProperty prop : CityProperty.values()) {
			dataVec.setProperty(prop, 0);
		}
		// Empty model
		double[] emptyCityData = ModelToVec.getCityDataVector(dataVec);
		for (ZoneType zone : ZoneType.values()) {
			double[] zoneAction = ModelToVec.getZoneAsVector(zone);
			input[i] = Util.appendVectors(emptyCityData, zoneAction);
			if (ZoneType.EMPTY == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.RESIDENTIAL == zone) {
				output[i] = new double[] { 1 };
			} else if (ZoneType.COMMERICAL == zone) {
				output[i] = new double[] { 0.5 };
			} else if (ZoneType.INDUSTRIAL == zone) {
				output[i] = new double[] { 0.4 };
			}
			i++;
		}

		// High R demand
		dataVec.setProperty(CityProperty.R_DEMAND, 1.0);
		double[] highR = ModelToVec.getCityDataVector(dataVec);
		dataVec.setProperty(CityProperty.R_DEMAND, 0);
		for (ZoneType zone : ZoneType.values()) {
			double[] zoneAction = ModelToVec.getZoneAsVector(zone);
			input[i] = Util.appendVectors(highR, zoneAction);
			if (ZoneType.EMPTY == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.RESIDENTIAL == zone) {
				output[i] = new double[] { 0.75 };
			} else if (ZoneType.COMMERICAL == zone) {
				output[i] = new double[] { 0.0 };
			} else if (ZoneType.INDUSTRIAL == zone) {
				output[i] = new double[] { 0.0 };
			}
			i++;
		}

		// High C demand
		dataVec.setProperty(CityProperty.C_DEMAND, 1.0);
		double[] highC = ModelToVec.getCityDataVector(dataVec);
		dataVec.setProperty(CityProperty.C_DEMAND, 0);
		for (ZoneType zone : ZoneType.values()) {
			double[] zoneAction = ModelToVec.getZoneAsVector(zone);
			input[i] = Util.appendVectors(highC, zoneAction);
			if (ZoneType.EMPTY == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.RESIDENTIAL == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.COMMERICAL == zone) {
				output[i] = new double[] { 0.75 };
			} else if (ZoneType.INDUSTRIAL == zone) {
				output[i] = new double[] { 0.0 };
			}
			i++;
		}

		// High industrial demand
		dataVec.setProperty(CityProperty.I_DEMAND, 1.0);
		double[] highIndy = ModelToVec.getCityDataVector(dataVec);
		dataVec.setProperty(CityProperty.I_DEMAND, 0);
		for (ZoneType zone : ZoneType.values()) {
			double[] zoneAction = ModelToVec.getZoneAsVector(zone);
			input[i] = Util.appendVectors(highIndy, zoneAction);
			if (ZoneType.EMPTY == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.RESIDENTIAL == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.COMMERICAL == zone) {
				output[i] = new double[] { 0.0 };
			} else if (ZoneType.INDUSTRIAL == zone) {
				output[i] = new double[] { 0.75 };
			}
			i++;
		}

		// high homelessness
		dataVec.setProperty(CityProperty.HOMELESS, 1.0);
		double[] highHomeless = ModelToVec.getCityDataVector(dataVec);
		dataVec.setProperty(CityProperty.HOMELESS, 0);
		for (ZoneType zone : ZoneType.values()) {
			double[] zoneAction = ModelToVec.getZoneAsVector(zone);
			input[i] = Util.appendVectors(highHomeless, zoneAction);
			if (ZoneType.EMPTY == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.RESIDENTIAL == zone) {
				output[i] = new double[] { 1 };
			} else if (ZoneType.COMMERICAL == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.INDUSTRIAL == zone) {
				output[i] = new double[] { 0.0 };
			}
			i++;
		}

		// high unemployment with high C
		dataVec.setProperty(CityProperty.UNEMPLOY, 1.0);
		dataVec.setProperty(CityProperty.C_DEMAND, 1.0);
		double[] highJoblessC = ModelToVec.getCityDataVector(dataVec);
		dataVec.setProperty(CityProperty.UNEMPLOY, 0);
		dataVec.setProperty(CityProperty.C_DEMAND, 0);
		for (ZoneType zone : ZoneType.values()) {
			double[] zoneAction = ModelToVec.getZoneAsVector(zone);
			input[i] = Util.appendVectors(highJoblessC, zoneAction);
			if (ZoneType.EMPTY == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.RESIDENTIAL == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.COMMERICAL == zone) {
				output[i] = new double[] { 1 };
			} else if (ZoneType.INDUSTRIAL == zone) {
				output[i] = new double[] { 0.0 };
			}
			i++;
		}

		// High unemployment with high indy demand
		dataVec.setProperty(CityProperty.UNEMPLOY, 1.0);
		dataVec.setProperty(CityProperty.I_DEMAND, 1.0);
		double[] highJobLessI = ModelToVec.getCityDataVector(dataVec);
		dataVec.setProperty(CityProperty.UNEMPLOY, 0);
		dataVec.setProperty(CityProperty.I_DEMAND, 0);
		for (ZoneType zone : ZoneType.values()) {
			double[] zoneAction = ModelToVec.getZoneAsVector(zone);
			input[i] = Util.appendVectors(highJobLessI, zoneAction);
			if (ZoneType.EMPTY == zone) {
				output[i] = new double[] { 0 };
			} else if (ZoneType.RESIDENTIAL == zone) {
				output[i] = new double[] { 0.0 };
			} else if (ZoneType.COMMERICAL == zone) {
				output[i] = new double[] { 0.0 };
			} else if (ZoneType.INDUSTRIAL == zone) {
				output[i] = new double[] { 1.0 };
			}
			i++;
		}

		for (int j = 0; j < input.length; j++) {
			MLData trainingIn = new BasicMLData(input[j]);
			MLData idealOut = new BasicMLData(output[j]);
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
		network.addLayer(new BasicLayer(null, true, INPUT_LAYER_SIZE));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, (int) (INPUT_LAYER_SIZE * 1.5)));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, INPUT_LAYER_SIZE / 2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, OUTPUT_LAYER_SIZE));
		network.getStructure().finalizeStructure();
		network.reset();
	}

	@Override
	public UserData takeNextAction() {
		CityData cityData = state.getWorld().getCity().getData();
		double[] modelVector = ModelToVec.getCityDataVector(cityData);
		double[] qValues = new double[ZoneType.values().length];
		int maxIndex = 0;
		double maxScore = -1;
		for (int i = 0; i < ZoneType.values().length; i++) {
			double[] zoneAction = ModelToVec.getZoneAsVector(ZoneType.values()[i]);
			double[] input = Util.appendVectors(modelVector, zoneAction);
			MLData data = new BasicMLData(input);
			double qValue = network.compute(data).getData(0);
			qValues[i] = qValue;
			if (qValue > maxScore) {
				maxScore = qValue;
				maxIndex = i;
			}
		}

		int strength = (int) Math.round(Util.mapValue(maxScore, new double[] { 0, 1 }, new double[] { 0, 3 }));

		ZoneType AIselection = ZoneType.values()[maxIndex];

		UserData fake = new UserData();
		fake.setZoneSelection(AIselection);
		fake.setRadius(strength);
		fake.setAI(true);
		return fake;

	}

	@Override
	public void addCase(Model<UserData, CityData> prev, Model<UserData, CityData> current, UserData action,
			double userRating) {
		double currentScore = Rules.score(state);
		double prevScore = Rules.score(prev);
		CityData cityData = prev.getWorld().getCity().getData();
		double[] modelVector = ModelToVec.getCityDataVector(cityData);
		double[] zoneAction = ModelToVec.getZoneAsVector(action.getZoneSelection());
		double[] input = Util.appendVectors(modelVector, zoneAction);
		double[] output = new double[] { currentScore };
		MLData trainingIn = new BasicMLData(input);
		MLData idealOut = new BasicMLData(output);
		DATASET.add(trainingIn, idealOut);
		this.trainResilient();
	}

	@Override
	public void setState(Model<UserData, CityData> state) {
		this.state = state;

	}

}
