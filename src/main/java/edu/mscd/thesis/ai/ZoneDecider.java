package edu.mscd.thesis.ai;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.city.CityData;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelToVec;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.util.WeightVector;
import edu.mscd.thesis.view.viewdata.Action;
import edu.mscd.thesis.view.viewdata.AiAction;

/**
 * Neural net that produces ZoneType for next-actions, based on current CityData
 * state MLP Q-Learner Input-layer: CityData vector(state), ZoneType vector
 * (action) Output-layer: Q-value of state-action pair All 4 outputs across all
 * 4 possible actions are compared, highest is selected.
 * 
 * @author Mike
 */
public class ZoneDecider extends AbstractNetwork implements Actor, Learner {
	private Model state;

	public ZoneDecider(Model initialState) {
		inputLayerSize = CityProperty.values().length + ZoneType.values().length;
		this.state = initialState;
		initNetwork();
		initTraining();
		train();
	}

	@Override
	protected void initTraining() {
		super.initTraining();
		double[][] input = new double[28][inputLayerSize];
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
				output[i] = new double[] { 0.5 };
			} else if (ZoneType.COMMERICAL == zone) {
				output[i] = new double[] { 0.5 };
			} else if (ZoneType.INDUSTRIAL == zone) {
				output[i] = new double[] { 0.5 };
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
				output[i] = new double[] { 1.0 };
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
				output[i] = new double[] { 1.0 };
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
				output[i] = new double[] { 1.0 };
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

	@Override
	public Action takeNextAction() {
		CityData cityData = state.getWorld().getCity().getData();
		double[] modelVector = ModelToVec.getCityDataVector(cityData);
		double[] qValues = new double[ZoneType.values().length];
		int maxIndex = 0;
		double maxScore = -Rules.MAX;
		for (int i = 0; i < ZoneType.values().length; i++) {
			double[] zoneAction = ModelToVec.getZoneAsVector(ZoneType.values()[i]);
			double[] input = Util.appendVectors(modelVector, zoneAction);
			MLData data = new BasicMLData(input);
			double qValue = computeOutput(data);
			qValues[i] = qValue;
			if (qValue > maxScore) {
				maxScore = qValue;
				maxIndex = i;
			}
		}

		int strength = (int) Math.round(Util.mapValue(maxScore, new double[] { 0, 1 }, new double[] { 0, 3 }));

		ZoneType AIselection = ZoneType.values()[maxIndex];

		AiAction fake = new AiAction();
		fake.setZoneType(AIselection);
		fake.setRadius(strength);
		return fake;

	}

	@Override
	public void addCase(Model prev, Model current, Action action, WeightVector<CityProperty> weights) {
		double actionScore = getActionScore(prev, current, action, weights);
		CityData cityData = prev.getWorld().getCity().getData();
		double[] modelVector = ModelToVec.getCityDataVector(cityData);
		double[] zoneAction = ModelToVec.getZoneAsVector(action.getZoneType());
		double[] input = Util.appendVectors(modelVector, zoneAction);
		double[] output = new double[] { actionScore };
		MLData trainingIn = new BasicMLData(input);
		MLData idealOut = new BasicMLData(output);
		super.learn(new BasicMLDataPair(trainingIn, idealOut));
	}

	@Override
	public void setState(Model state) {
		this.state = state;

	}

}
